package com.eavisionlabs.nurvakti.data

import com.eavisionlabs.nurvakti.model.City
import com.eavisionlabs.nurvakti.model.DailyPrayerSchedule
import com.eavisionlabs.nurvakti.model.MonthlyDaySchedule
import com.eavisionlabs.nurvakti.model.PrayerTimeItem
import com.eavisionlabs.nurvakti.model.PrayerType
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

/**
 * Namaz vakti motoru — Diyanet İşleri Başkanlığı yöntemi:
 *  - İmsak: güneş ufkun 18° altında, Yatsı: 17° altında
 *  - İkindi: asr-ı evvel (gölge = 1 boy)
 *  - Temkinler: Güneş −7 dk, Öğle +5 dk, İkindi +4 dk, Akşam +7 dk
 *  - Yüksek enlemde (Avrupa yazı) "gecenin ortası" kuralı
 * Saatler şehrin saat dilimine (yaz saati dahil) göre hesaplanır.
 */
object PrayerCalculationEngine {

    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    private const val FAJR_ANGLE = 18.0
    private const val ISHA_ANGLE = 17.0
    private const val TEMKIN_SUNRISE = -7
    private const val TEMKIN_DHUHR = 5
    private const val TEMKIN_ASR = 4
    private const val TEMKIN_MAGHRIB = 7

    /** Kullanıcının dakika cinsinden ek düzeltmeleri (Ayarlar > Vakit düzeltme). Sıra: imsak..yatsı */
    @Volatile
    var userAdjustments: IntArray = IntArray(6)

    /** Hicri takvim gün düzeltmesi (−2..+2). */
    @Volatile
    var hijriOffsetDays: Int = 0

    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val lat1 = Math.toRadians(userLat)
        val lon1 = Math.toRadians(userLng)
        val lat2 = Math.toRadians(KAABA_LAT)
        val lon2 = Math.toRadians(KAABA_LNG)
        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return ((Math.toDegrees(atan2(y, x)) + 360.0) % 360.0).toFloat()
    }

    /**
     * Verilen gün için 6 vaktin epoch milisaniyesi (imsak, güneş, öğle, ikindi, akşam, yatsı),
     * en yakın dakikaya yuvarlanmış.
     */
    fun computeTimes(year: Int, month: Int, day: Int, lat: Double, lng: Double, applyUserAdjust: Boolean = true): LongArray {
        val st = SolarTime(year, month, day, lat, lng)
        val dayStartUtc = utcMidnight(year, month, day)
        fun at(hours: Double): Long = dayStartUtc + (hours * 3_600_000.0).toLong()

        val sunrise = at(st.sunrise)
        val sunset = at(st.sunset)
        val transit = at(st.transit)
        val asr = at(st.afternoon(1.0))

        var fajr = st.hourAngle(-FAJR_ANGLE, false).let { if (it.isNaN()) Long.MIN_VALUE else at(it) }
        var isha = st.hourAngle(-ISHA_ANGLE, true).let { if (it.isNaN()) Long.MAX_VALUE else at(it) }

        // Gecenin ortası kuralı (yüksek enlemlerde şafak hiç kaybolmadığında)
        val tomorrow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear(); set(year, month - 1, day); add(Calendar.DAY_OF_MONTH, 1)
        }
        val stTomorrow = SolarTime(
            tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH) + 1, tomorrow.get(Calendar.DAY_OF_MONTH), lat, lng
        )
        val tomorrowSunrise = tomorrow.timeInMillis + (stTomorrow.sunrise * 3_600_000.0).toLong()
        val night = tomorrowSunrise - sunset
        if (night > 0) {
            val safeFajr = sunrise - night / 2
            if (fajr == Long.MIN_VALUE || safeFajr > fajr) fajr = safeFajr
            val safeIsha = sunset + night / 2
            if (isha == Long.MAX_VALUE || safeIsha < isha) isha = safeIsha
        }

        val minute = 60_000L
        val raw = longArrayOf(
            fajr,
            sunrise + TEMKIN_SUNRISE * minute,
            transit + TEMKIN_DHUHR * minute,
            asr + TEMKIN_ASR * minute,
            sunset + TEMKIN_MAGHRIB * minute,
            isha
        )
        val adj = if (applyUserAdjust) userAdjustments else IntArray(6)
        return LongArray(6) { i -> roundToMinute(raw[i] + adj.getOrElse(i) { 0 } * minute) }
    }

    private fun roundToMinute(ms: Long): Long = Math.floorDiv(ms + 30_000L, 60_000L) * 60_000L

    private fun utcMidnight(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear(); set(year, month - 1, day) }.timeInMillis

    fun timeZoneOf(city: City): TimeZone = TimeZone.getTimeZone(city.timeZoneId)

    fun formatTime(millis: Long, tz: TimeZone): String {
        val c = Calendar.getInstance(tz).apply { timeInMillis = millis }
        return String.format(Locale.ROOT, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
    }

    /** Şehrin yerel takvimine göre "bugün" için 6 vakit. */
    fun timesForDate(city: City, date: Calendar): LongArray {
        val local = Calendar.getInstance(timeZoneOf(city)).apply { timeInMillis = date.timeInMillis }
        return computeTimes(
            local.get(Calendar.YEAR), local.get(Calendar.MONTH) + 1, local.get(Calendar.DAY_OF_MONTH),
            city.latitude, city.longitude
        )
    }

    // --- Hicri takvim -------------------------------------------------------

    val hijriMonthsTr = arrayOf(
        "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
        "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
        "Ramazan", "Şevval", "Zilkade", "Zilhicce"
    )

    /** (gün, ay 1..12, yıl) — aritmetik hicri takvim + kullanıcı düzeltmesi. */
    fun hijriParts(calendar: Calendar): Triple<Int, Int, Int> {
        val c = calendar.clone() as Calendar
        if (hijriOffsetDays != 0) c.add(Calendar.DAY_OF_MONTH, hijriOffsetDays)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jd = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l1 = l - 10631 * n + 354
        val j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        val l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val hMonth = (24 * l2) / 709
        val hDay = l2 - (709 * hMonth) / 24
        val hYear = 30 * n + j - 30
        return Triple(hDay, hMonth.coerceIn(1, 12), hYear)
    }

    fun getHijriDate(calendar: Calendar): String {
        val (d, m, y) = hijriParts(calendar)
        return "$d ${hijriMonthsTr[m - 1]} $y"
    }

    // --- Ekran modelleri ----------------------------------------------------

    private val turkishDayNames = arrayOf("", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi")
    private val turkishMonthNames = arrayOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    private val prayerOrder = listOf(
        PrayerType.IMSAK, PrayerType.GUNES, PrayerType.OGLE, PrayerType.IKINDI, PrayerType.AKSAM, PrayerType.YATSI
    )

    fun calculateDailySchedule(city: City, nowMillis: Long = System.currentTimeMillis()): DailyPrayerSchedule {
        val tz = timeZoneOf(city)
        val local = Calendar.getInstance(tz).apply { timeInMillis = nowMillis }
        val y = local.get(Calendar.YEAR)
        val mo = local.get(Calendar.MONTH) + 1
        val d = local.get(Calendar.DAY_OF_MONTH)
        val times = computeTimes(y, mo, d, city.latitude, city.longitude)

        val items = prayerOrder.mapIndexed { i, type ->
            PrayerTimeItem(type = type, timeFormatted = formatTime(times[i], tz), timestampMillis = times[i])
        }
        val nextIndex = items.indexOfFirst { it.timestampMillis > nowMillis }
        val updated = items.mapIndexed { index, item ->
            val isNext = index == nextIndex
            val isCurrent = if (nextIndex == -1) index == items.lastIndex
            else if (nextIndex == 0) false
            else index == nextIndex - 1
            item.copy(isPassed = nextIndex == -1 || index < nextIndex, isNext = isNext, isCurrent = isCurrent)
        }

        val nextPrayer = if (nextIndex != -1) updated[nextIndex] else {
            val t = Calendar.getInstance(tz).apply { timeInMillis = nowMillis; add(Calendar.DAY_OF_MONTH, 1) }
            val tt = computeTimes(t.get(Calendar.YEAR), t.get(Calendar.MONTH) + 1, t.get(Calendar.DAY_OF_MONTH), city.latitude, city.longitude)
            PrayerTimeItem(PrayerType.IMSAK, formatTime(tt[0], tz), tt[0], isNext = true)
        }
        // Gece yarısından imsaka kadar "şu anki vakit" yatsıdır
        val currentPrayer = updated.find { it.isCurrent } ?: if (nextIndex == 0) updated.last().copy(isCurrent = true) else null

        return DailyPrayerSchedule(
            cityName = city.displayName,
            districtName = city.country,
            gregorianDateFormatted = "$d ${turkishMonthNames[mo - 1]} $y ${turkishDayNames[local.get(Calendar.DAY_OF_WEEK)]}",
            hijriDateFormatted = getHijriDate(local),
            prayerTimes = updated,
            nextPrayer = nextPrayer,
            currentPrayer = currentPrayer,
            remainingMillisToNext = (nextPrayer.timestampMillis - nowMillis).coerceAtLeast(0L)
        )
    }

    /** Ayın tüm günleri (imsakiye / aylık tablo). [monthOffset] 0 = bu ay, 1 = gelecek ay. */
    fun generateMonthlySchedule(city: City, monthOffset: Int = 0): List<MonthlyDaySchedule> {
        val tz = timeZoneOf(city)
        val c = Calendar.getInstance(tz).apply { add(Calendar.MONTH, monthOffset); set(Calendar.DAY_OF_MONTH, 1) }
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        return (1..c.getActualMaximum(Calendar.DAY_OF_MONTH)).map { day ->
            c.set(Calendar.DAY_OF_MONTH, day)
            val t = computeTimes(year, month, day, city.latitude, city.longitude)
            MonthlyDaySchedule(
                dayNumber = day,
                gregorianDate = String.format(Locale.ROOT, "%02d.%02d.%d", day, month, year),
                hijriDate = getHijriDate(c),
                imsak = formatTime(t[0], tz),
                gunes = formatTime(t[1], tz),
                ogle = formatTime(t[2], tz),
                ikindi = formatTime(t[3], tz),
                aksam = formatTime(t[4], tz),
                yatsi = formatTime(t[5], tz)
            )
        }
    }

    /** Belirli bir aralık için (ör. Ramazan imsakiyesi) gün gün vakitler. */
    fun scheduleForRange(city: City, start: Calendar, days: Int): List<MonthlyDaySchedule> {
        val tz = timeZoneOf(city)
        val c = Calendar.getInstance(tz).apply { timeInMillis = start.timeInMillis }
        return (0 until days).map { i ->
            val y = c.get(Calendar.YEAR); val m = c.get(Calendar.MONTH) + 1; val d = c.get(Calendar.DAY_OF_MONTH)
            val t = computeTimes(y, m, d, city.latitude, city.longitude)
            val row = MonthlyDaySchedule(
                dayNumber = i + 1,
                gregorianDate = String.format(Locale.ROOT, "%02d.%02d.%d", d, m, y),
                hijriDate = getHijriDate(c),
                imsak = formatTime(t[0], tz), gunes = formatTime(t[1], tz), ogle = formatTime(t[2], tz),
                ikindi = formatTime(t[3], tz), aksam = formatTime(t[4], tz), yatsi = formatTime(t[5], tz)
            )
            c.add(Calendar.DAY_OF_MONTH, 1)
            row
        }
    }
}
