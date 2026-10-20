package com.example.data

import com.example.model.DailyPrayerSchedule
import com.example.model.MonthlyDaySchedule
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import java.util.Calendar
import java.util.Locale
import kotlin.math.*

object PrayerCalculationEngine {

    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    /**
     * Calculates the great-circle azimuth bearing in degrees (0..360) from [userLat, userLng] to the Kaaba.
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val lat1 = Math.toRadians(userLat)
        val lon1 = Math.toRadians(userLng)
        val lat2 = Math.toRadians(KAABA_LAT)
        val lon2 = Math.toRadians(KAABA_LNG)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing.toFloat()
    }

    /**
     * Converts a Gregorian date (year, month 1..12, day) into approximate Islamic Hijri date.
     */
    fun getHijriDate(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day computation
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        var jd = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l1 = l - 10631 * n + 354
        val j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        val l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val hijriMonth = (24 * l2) / 709
        val hijriDay = l2 - (709 * hijriMonth) / 24
        val hijriYear = 30 * n + j - 30

        val hijriMonthsTr = arrayOf(
            "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
            "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
            "Ramazan", "Şevval", "Zilkade", "Zilhicce"
        )
        val monthIdx = (hijriMonth - 1).coerceIn(0, 11)
        return "$hijriDay ${hijriMonthsTr[monthIdx]} $hijriYear"
    }

    /**
     * Calculates prayer times for a given day, latitude, longitude, and timezone offset.
     */
    fun calculateDailySchedule(
        cityName: String,
        districtName: String = "",
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double = 3.0
    ): DailyPrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val times = computeRawTimes(year, month, day, latitude, longitude, timezoneOffset)

        val prayerItems = mutableListOf<PrayerTimeItem>()
        val types = listOf(
            PrayerType.IMSAK to times[0],
            PrayerType.GUNES to times[1],
            PrayerType.OGLE to times[2],
            PrayerType.IKINDI to times[3],
            PrayerType.AKSAM to times[4],
            PrayerType.YATSI to times[5]
        )

        val nowMillis = System.currentTimeMillis()

        for ((type, hoursFloat) in types) {
            val totalMinutes = (hoursFloat * 60).roundToInt()
            val hh = (totalMinutes / 60) % 24
            val mm = totalMinutes % 60
            val formatted = String.format(Locale.getDefault(), "%02d:%02d", hh, mm)

            val pCal = calendar.clone() as Calendar
            pCal.set(Calendar.HOUR_OF_DAY, hh)
            pCal.set(Calendar.MINUTE, mm)
            pCal.set(Calendar.SECOND, 0)
            pCal.set(Calendar.MILLISECOND, 0)

            prayerItems.add(
                PrayerTimeItem(
                    type = type,
                    timeFormatted = formatted,
                    timestampMillis = pCal.timeInMillis
                )
            )
        }

        // Determine passed, current, next
        var nextIndex = -1
        for (i in prayerItems.indices) {
            if (prayerItems[i].timestampMillis > nowMillis) {
                nextIndex = i
                break
            }
        }

        val updatedItems = prayerItems.mapIndexed { index, item ->
            val isPassed = if (nextIndex != -1) index < nextIndex else true
            val isNext = index == nextIndex
            val isCurrent = if (nextIndex != -1) {
                index == (nextIndex - 1 + prayerItems.size) % prayerItems.size
            } else {
                index == prayerItems.lastIndex
            }
            item.copy(isPassed = isPassed, isNext = isNext, isCurrent = isCurrent)
        }

        val nextPrayer = if (nextIndex != -1) {
            updatedItems[nextIndex]
        } else {
            // Next is tomorrow's imsak
            val tomorrowCal = calendar.clone() as Calendar
            tomorrowCal.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrowTimes = computeRawTimes(
                tomorrowCal.get(Calendar.YEAR),
                tomorrowCal.get(Calendar.MONTH) + 1,
                tomorrowCal.get(Calendar.DAY_OF_MONTH),
                latitude, longitude, timezoneOffset
            )
            val tm = (tomorrowTimes[0] * 60).roundToInt()
            val hh = (tm / 60) % 24
            val mm = tm % 60
            tomorrowCal.set(Calendar.HOUR_OF_DAY, hh)
            tomorrowCal.set(Calendar.MINUTE, mm)
            tomorrowCal.set(Calendar.SECOND, 0)
            tomorrowCal.set(Calendar.MILLISECOND, 0)

            PrayerTimeItem(
                type = PrayerType.IMSAK,
                timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hh, mm),
                timestampMillis = tomorrowCal.timeInMillis,
                isNext = true
            )
        }

        val remainingMillis = (nextPrayer.timestampMillis - nowMillis).coerceAtLeast(0L)
        val currentPrayer = updatedItems.find { it.isCurrent }

        val turkishDayNames = arrayOf(
            "", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"
        )
        val turkishMonthNames = arrayOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )

        val dayName = turkishDayNames[calendar.get(Calendar.DAY_OF_WEEK)]
        val monthName = turkishMonthNames[calendar.get(Calendar.MONTH)]
        val gregorianFormatted = "$day $monthName $year $dayName"
        val hijriFormatted = getHijriDate(calendar)

        return DailyPrayerSchedule(
            cityName = cityName,
            districtName = districtName,
            gregorianDateFormatted = gregorianFormatted,
            hijriDateFormatted = hijriFormatted,
            prayerTimes = updatedItems,
            nextPrayer = nextPrayer,
            currentPrayer = currentPrayer,
            remainingMillisToNext = remainingMillis
        )
    }

    /**
     * Generates a monthly prayer table (e.g. for Ramadan Imsakiye or 30-day prayer schedule).
     */
    fun generateMonthlySchedule(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double = 3.0
    ): List<MonthlyDaySchedule> {
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val list = mutableListOf<MonthlyDaySchedule>()

        val c = calendar.clone() as Calendar
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1

        for (day in 1..daysInMonth) {
            c.set(Calendar.DAY_OF_MONTH, day)
            val times = computeRawTimes(year, month, day, latitude, longitude, timezoneOffset)
            val fmt = { h: Double ->
                val min = (h * 60).roundToInt()
                String.format(Locale.getDefault(), "%02d:%02d", (min / 60) % 24, min % 60)
            }
            list.add(
                MonthlyDaySchedule(
                    dayNumber = day,
                    gregorianDate = String.format(Locale.getDefault(), "%02d.%02d.%d", day, month, year),
                    hijriDate = getHijriDate(c),
                    imsak = fmt(times[0]),
                    gunes = fmt(times[1]),
                    ogle = fmt(times[2]),
                    ikindi = fmt(times[3]),
                    aksam = fmt(times[4]),
                    yatsi = fmt(times[5])
                )
            )
        }
        return list
    }

    private fun computeRawTimes(
        year: Int,
        month: Int,
        day: Int,
        lat: Double,
        lng: Double,
        tz: Double
    ): DoubleArray {
        // Astronomical calculations
        val d = 367 * year - (7 * (year + (month + 9) / 12)) / 4 + (275 * month) / 9 + day - 730530

        val w = 282.9404 + 4.70935E-5 * d
        val e = 0.016709 - 1.151E-9 * d
        val m = fixAngle(356.0470 + 0.9856002585 * d)

        val oblEcl = 23.4393 - 3.563E-7 * d
        val l = fixAngle(w + m)

        val eRad = Math.toRadians(m + Math.toDegrees(e * sin(Math.toRadians(m)) * (1.0 + e * cos(Math.toRadians(m)))))
        val x = cos(eRad) - e
        val y = sin(eRad) * sqrt(1.0 - e * e)
        val v = Math.toDegrees(atan2(y, x))
        val sunLon = fixAngle(v + w)

        val xEcl = cos(Math.toRadians(sunLon))
        val yEcl = sin(Math.toRadians(sunLon)) * cos(Math.toRadians(oblEcl))
        val zEcl = sin(Math.toRadians(sunLon)) * sin(Math.toRadians(oblEcl))

        val declination = Math.toDegrees(atan2(zEcl, sqrt(xEcl * xEcl + yEcl * yEcl)))
        var rightAscension = Math.toDegrees(atan2(yEcl, xEcl)) / 15.0
        rightAscension = fixHour(rightAscension)

        // Equation of Time in hours
        val eqTime = (fixAngle(l) / 15.0) - rightAscension

        // Solar Noon (Öğle)
        val solarNoon = 12.0 + tz - (lng / 15.0) - eqTime

        // Standard angles: Fajr 18.0°, Sunrise 0.833°, Sunset 0.833°, Isha 17.0°
        val fajrAngle = 18.0
        val sunriseAngle = 0.833
        val ishaAngle = 17.0

        val sunriseHourAngle = calculateHourAngle(lat, declination, 90.0 + sunriseAngle)
        val fajrHourAngle = calculateHourAngle(lat, declination, 90.0 + fajrAngle)
        val ishaHourAngle = calculateHourAngle(lat, declination, 90.0 + ishaAngle)

        // Asr calculation (Shafi'i: shadow factor = 1)
        val asrAngle = Math.toDegrees(
            atan(1.0 / (1.0 + tan(Math.toRadians(abs(lat - declination)))))
        )
        val asrHourAngle = calculateHourAngle(lat, declination, 90.0 - asrAngle)

        val fajr = solarNoon - fajrHourAngle
        val sunrise = solarNoon - sunriseHourAngle
        val dhuhr = solarNoon + (4.0 / 60.0) // 4 min precaution
        val asr = solarNoon + asrHourAngle
        val maghrib = solarNoon + sunriseHourAngle + (3.0 / 60.0)
        val isha = solarNoon + ishaHourAngle

        return doubleArrayOf(
            fixHour(fajr),
            fixHour(sunrise),
            fixHour(dhuhr),
            fixHour(asr),
            fixHour(maghrib),
            fixHour(isha)
        )
    }

    private fun calculateHourAngle(lat: Double, declination: Double, zenith: Double): Double {
        val latRad = Math.toRadians(lat)
        val decRad = Math.toRadians(declination)
        val zenRad = Math.toRadians(zenith)

        val cosH = (cos(zenRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clamped = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped)) / 15.0
    }

    private fun fixAngle(a: Double): Double {
        val b = a - 360.0 * floor(a / 360.0)
        return if (b < 0) b + 360.0 else b
    }

    private fun fixHour(h: Double): Double {
        val b = h - 24.0 * floor(h / 24.0)
        return if (b < 0) b + 24.0 else b
    }
}
