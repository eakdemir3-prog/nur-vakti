package com.eavisionlabs.nurvakti.notify

import android.content.Context
import androidx.core.app.NotificationCompat
import com.eavisionlabs.nurvakti.data.PrayerCalculationEngine
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t

/**
 * Bildirim çubuğunda sıradaki vakte geri sayan kalıcı bildirim.
 * Servis gerektirmez: sistem kronometresi geri sayar, her vakit girişinde alarm tarafından yenilenir.
 */
object CountdownNotifier {

    fun update(context: Context) {
        val prefs = UserPreferences(context)
        if (!prefs.ongoingCountdownEnabled) {
            NotificationHelper.cancel(context, NotificationHelper.ID_COUNTDOWN)
            return
        }
        I18n.language = prefs.language
        PrayerAlarmScheduler.applyEngineSettings(context)
        val city = PrayerAlarmScheduler.currentCity(context)
        val schedule = PrayerCalculationEngine.calculateDailySchedule(city)
        val next = schedule.nextPrayer ?: return
        val tz = PrayerCalculationEngine.timeZoneOf(city)
        val line = schedule.prayerTimes.joinToString("  ") { "${t(it.type.titleTr).take(3)} ${it.timeFormatted}" }

        val builder = NotificationHelper.base(context, NotificationHelper.CH_COUNTDOWN)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(true)
            .setWhen(next.timestampMillis)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setContentTitle(String.format(t("%1\$s vaktine • %2\$s"), t(next.type.titleTr), PrayerCalculationEngine.formatTime(next.timestampMillis, tz)))
            .setContentText(city.displayName)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${city.displayName}\n$line"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(NotificationHelper.openAppIntent(context, 50, "PRAYER_TIMES"))
        NotificationHelper.notify(context, NotificationHelper.ID_COUNTDOWN, builder)
    }
}
