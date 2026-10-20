package com.eavisionlabs.nurvakti.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.eavisionlabs.nurvakti.data.CityProvider
import com.eavisionlabs.nurvakti.data.PrayerCalculationEngine
import com.eavisionlabs.nurvakti.data.ReligiousDaysProvider
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.model.City
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.model.PrayerType
import java.util.Calendar

/**
 * Tüm zamanlanmış olayları (vakit girişi, vakit öncesi hatırlatma, cuma, kandil, günün ayeti,
 * gece yarısı yenileme) hesaplar ve sıradaki olay için tek bir kesin alarm kurar.
 * Alarm çaldığında [PrayerAlarmReceiver] o ana denk gelen olayları işler ve bir sonrakini kurar.
 */
object PrayerAlarmScheduler {

    const val ACTION_ALARM = "com.eavisionlabs.nurvakti.ACTION_PRAYER_ALARM"
    private const val REQUEST_CODE = 4242

    enum class Kind { PRAYER, PRE_REMINDER, FRIDAY, KANDIL, DAILY_VERSE, REFRESH }

    data class Event(val time: Long, val kind: Kind, val prayerIndex: Int = -1)

    val prayerTypes = listOf(
        PrayerType.IMSAK, PrayerType.GUNES, PrayerType.OGLE, PrayerType.IKINDI, PrayerType.AKSAM, PrayerType.YATSI
    )

    fun currentCity(context: Context): City {
        CityProvider.init(context)
        val prefs = UserPreferences(context)
        return prefs.loadCity() ?: CityProvider.findById(prefs.selectedCityId) ?: CityProvider.defaultCity
    }

    /** Ayarlardaki düzeltmeleri motora yükler (alıcılar uygulama kapalıyken de çalışır). */
    fun applyEngineSettings(context: Context) {
        val prefs = UserPreferences(context)
        PrayerCalculationEngine.userAdjustments = prefs.getPrayerAdjustments()
        PrayerCalculationEngine.hijriOffsetDays = prefs.hijriOffset
    }

    /** [from] sonrasındaki olaylar, zamana göre sıralı (yaklaşık 2 gün). */
    fun upcomingEvents(context: Context, from: Long = System.currentTimeMillis()): List<Event> {
        applyEngineSettings(context)
        val prefs = UserPreferences(context)
        val city = currentCity(context)
        val tz = PrayerCalculationEngine.timeZoneOf(city)
        val events = mutableListOf<Event>()
        val pre = prefs.preReminderMinutes

        for (dayOffset in 0..2) {
            val day = Calendar.getInstance(tz).apply { timeInMillis = from; add(Calendar.DAY_OF_MONTH, dayOffset) }
            val times = PrayerCalculationEngine.timesForDate(city, day)
            times.forEachIndexed { i, time ->
                val pref = prefs.getNotificationPref(prayerTypes[i])
                // Kalıcı geri sayım her vakitte güncellenir; bildirim kapalı olsa bile olay kurulur
                if (pref != NotificationPreference.DISABLED || prefs.ongoingCountdownEnabled) {
                    events += Event(time, Kind.PRAYER, i)
                }
                if (pre > 0 && pref != NotificationPreference.DISABLED && prayerTypes[i] != PrayerType.GUNES) {
                    events += Event(time - pre * 60_000L, Kind.PRE_REMINDER, i)
                }
            }

            val dow = day.get(Calendar.DAY_OF_WEEK)
            if (prefs.fridayReminderEnabled && dow == Calendar.FRIDAY) {
                // Cuma namazından (öğle) 1 saat önce
                events += Event(times[2] - 60 * 60_000L, Kind.FRIDAY)
            }
            if (prefs.dailyVerseNotificationEnabled) {
                // Güneş doğduktan 1,5 saat sonra
                events += Event(times[1] + 90 * 60_000L, Kind.DAILY_VERSE)
            }
            if (prefs.kandilReminderEnabled) {
                val todays = ReligiousDaysProvider.todaysEvent(day)
                if (todays != null) {
                    // Kandil gecesi için akşamdan 1 saat önce, bayram/özel gün için sabah 09:00
                    val time = if (ReligiousDaysProvider.isKandil(todays.id)) times[4] - 60 * 60_000L
                    else Calendar.getInstance(tz).apply {
                        timeInMillis = day.timeInMillis
                        set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    events += Event(time, Kind.KANDIL)
                }
            }
            // Gece yarısı yenileme (widget ve gün değişimi)
            val midnight = Calendar.getInstance(tz).apply {
                timeInMillis = day.timeInMillis
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 1); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            events += Event(midnight, Kind.REFRESH)
        }
        return events.filter { it.time > from }.sortedBy { it.time }
    }

    /** Sıradaki olay için alarm kurar; widget ve kalıcı bildirimi de günceller. */
    fun scheduleNext(context: Context) {
        val ctx = context.applicationContext
        NotificationHelper.createChannels(ctx)
        val next = upcomingEvents(ctx).firstOrNull() ?: return
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = alarmIntent(ctx)
        am.cancel(pi)
        try {
            if (canScheduleExact(ctx)) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.time, pi)
            } else {
                // Kesin alarm izni yoksa en yakın uygun zamanda (birkaç dakika sapabilir)
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.time, pi)
            }
        } catch (_: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.time, pi)
        }
        CountdownNotifier.update(ctx)
        NextPrayerWidget.updateAll(ctx)
    }

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }

    private fun alarmIntent(context: Context): PendingIntent {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).setAction(ACTION_ALARM)
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
