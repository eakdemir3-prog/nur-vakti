package com.eavisionlabs.nurvakti.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.eavisionlabs.nurvakti.data.DailyContent
import com.eavisionlabs.nurvakti.data.PrayerCalculationEngine
import com.eavisionlabs.nurvakti.data.ReligiousDaysProvider
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.model.NotificationPreference
import kotlin.concurrent.thread

/** Zamanlanmış alarm geldiğinde o ana ait olayları işler ve sıradakini kurar. */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val app = context.applicationContext
        thread {
            try {
                handleDueEvents(app)
            } catch (_: Exception) {
            } finally {
                try { PrayerAlarmScheduler.scheduleNext(app) } catch (_: Exception) {}
                pending.finish()
            }
        }
    }

    companion object {
        private const val KEY_LAST_HANDLED = "alarm_last_handled"

        fun handleDueEvents(context: Context) {
            val prefsRaw = context.getSharedPreferences(UserPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            val prefs = UserPreferences(context)
            I18n.language = prefs.language
            val now = System.currentTimeMillis()
            val lastHandled = prefsRaw.getLong(KEY_LAST_HANDLED, 0L)
            val due = PrayerAlarmScheduler.upcomingEvents(context, now - 3 * 60_000L)
                .filter { it.time <= now + 30_000L && it.time > lastHandled }
            if (due.isEmpty()) return
            for (e in due) handle(context, prefs, e)
            prefsRaw.edit().putLong(KEY_LAST_HANDLED, due.maxOf { it.time }).apply()
        }

        private fun handle(context: Context, prefs: UserPreferences, e: PrayerAlarmScheduler.Event) {
            val city = PrayerAlarmScheduler.currentCity(context)
            val tz = PrayerCalculationEngine.timeZoneOf(city)
            when (e.kind) {
                PrayerAlarmScheduler.Kind.PRAYER -> {
                    val type = PrayerAlarmScheduler.prayerTypes[e.prayerIndex]
                    val pref = prefs.getNotificationPref(type)
                    val title = String.format(t("%s vakti girdi"), t(type.titleTr))
                    val text = "${city.displayName} • ${PrayerCalculationEngine.formatTime(e.time, tz)}"
                    when (pref) {
                        NotificationPreference.DISABLED -> Unit
                        NotificationPreference.AZAN -> {
                            val i = Intent(context, AdhanService::class.java)
                                .putExtra(AdhanService.EXTRA_TITLE, title)
                                .putExtra(AdhanService.EXTRA_TEXT, text)
                            try {
                                ContextCompat.startForegroundService(context, i)
                            } catch (_: Exception) {
                                // Ön plan servisi başlatılamazsa sesli bildirime düş
                                postPrayer(context, NotificationHelper.CH_PRAYER_SOUND, e.prayerIndex, title, text)
                            }
                        }
                        NotificationPreference.NOTIFICATION_SOUND ->
                            postPrayer(context, NotificationHelper.CH_PRAYER_SOUND, e.prayerIndex, title, text)
                        NotificationPreference.SILENT ->
                            postPrayer(context, NotificationHelper.CH_PRAYER_SILENT, e.prayerIndex, title, text)
                        NotificationPreference.VIBRATE_ONLY ->
                            postPrayer(context, NotificationHelper.CH_PRAYER_VIBRATE, e.prayerIndex, title, text)
                    }
                }
                PrayerAlarmScheduler.Kind.PRE_REMINDER -> {
                    val type = PrayerAlarmScheduler.prayerTypes[e.prayerIndex]
                    val prayerTime = e.time + prefs.preReminderMinutes * 60_000L
                    val title = String.format(
                        t("%1\$d dakika sonra %2\$s vakti"), prefs.preReminderMinutes, t(type.titleTr)
                    )
                    val text = "${city.displayName} • ${PrayerCalculationEngine.formatTime(prayerTime, tz)}"
                    NotificationHelper.notify(
                        context, NotificationHelper.ID_REMINDER_BASE + e.prayerIndex,
                        NotificationHelper.base(context, NotificationHelper.CH_REMINDER)
                            .setContentTitle(title).setContentText(text)
                            .setTimeoutAfter(prefs.preReminderMinutes * 60_000L)
                            .setContentIntent(NotificationHelper.openAppIntent(context, 10 + e.prayerIndex))
                    )
                }
                PrayerAlarmScheduler.Kind.FRIDAY -> {
                    val msg = DailyContent.fridayMessage(context)
                    NotificationHelper.notify(
                        context, NotificationHelper.ID_FRIDAY,
                        NotificationHelper.base(context, NotificationHelper.CH_GENERAL)
                            .setContentTitle(t("Hayırlı Cumalar"))
                            .setContentText(msg)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                            .setContentIntent(NotificationHelper.openAppIntent(context, 20, "SHARE_GREETING"))
                    )
                }
                PrayerAlarmScheduler.Kind.KANDIL -> {
                    val day = ReligiousDaysProvider.todaysEvent() ?: return
                    val title = if (ReligiousDaysProvider.isKandil(day.id))
                        String.format(t("Bu gece %s"), t(day.title)) else t(day.title)
                    val msg = if (ReligiousDaysProvider.isKandil(day.id)) t("Kandiliniz mübarek olsun.") else t(day.description)
                    NotificationHelper.notify(
                        context, NotificationHelper.ID_KANDIL,
                        NotificationHelper.base(context, NotificationHelper.CH_GENERAL)
                            .setContentTitle(title).setContentText(msg)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                            .setContentIntent(NotificationHelper.openAppIntent(context, 21, "SHARE_GREETING"))
                    )
                }
                PrayerAlarmScheduler.Kind.DAILY_VERSE -> {
                    val v = DailyContent.verseOfDay(context) ?: return
                    val text = "${v.text}\n— ${v.reference}"
                    NotificationHelper.notify(
                        context, NotificationHelper.ID_DAILY_VERSE,
                        NotificationHelper.base(context, NotificationHelper.CH_GENERAL)
                            .setContentTitle(t("Günün Ayeti"))
                            .setContentText(v.text)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                            .setContentIntent(NotificationHelper.openAppIntent(context, 22, "HOME"))
                    )
                }
                PrayerAlarmScheduler.Kind.REFRESH -> Unit
            }
        }

        private fun postPrayer(context: Context, channel: String, index: Int, title: String, text: String) {
            NotificationHelper.notify(
                context, NotificationHelper.ID_PRAYER_BASE + index,
                NotificationHelper.base(context, channel)
                    .setContentTitle(title).setContentText(text)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(NotificationHelper.openAppIntent(context, 30 + index, "PRAYER_TIMES"))
            )
        }
    }
}

/** Telefon açıldığında, saat/saat dilimi değiştiğinde ve uygulama güncellendiğinde alarmları yeniden kurar. */
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val app = context.applicationContext
        thread {
            try { PrayerAlarmScheduler.scheduleNext(app) } catch (_: Exception) {} finally { pending.finish() }
        }
    }
}
