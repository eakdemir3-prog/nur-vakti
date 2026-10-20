package com.eavisionlabs.nurvakti.notify

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import com.eavisionlabs.nurvakti.MainActivity
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.data.PrayerCalculationEngine
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t

/** Ana ekran widget'ı: sıradaki vakit, geri sayım ve günün 6 vakti. */
class NextPrayerWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it) }
    }

    override fun onEnabled(context: Context) {
        PrayerAlarmScheduler.scheduleNext(context)
    }

    companion object {
        private val nameIds = intArrayOf(R.id.w_name_0, R.id.w_name_1, R.id.w_name_2, R.id.w_name_3, R.id.w_name_4, R.id.w_name_5)
        private val timeIds = intArrayOf(R.id.w_time_0, R.id.w_time_1, R.id.w_time_2, R.id.w_time_3, R.id.w_time_4, R.id.w_time_5)

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, NextPrayerWidget::class.java))
            ids.forEach { render(context, manager, it) }
        }

        private fun render(context: Context, manager: AppWidgetManager, widgetId: Int) {
            I18n.language = UserPreferences(context).language
            PrayerAlarmScheduler.applyEngineSettings(context)
            val city = PrayerAlarmScheduler.currentCity(context)
            val schedule = PrayerCalculationEngine.calculateDailySchedule(city)
            val next = schedule.nextPrayer
            val views = RemoteViews(context.packageName, R.layout.widget_next_prayer)

            views.setTextViewText(R.id.w_city, city.displayName)
            views.setTextViewText(R.id.w_hijri, schedule.hijriDateFormatted)
            if (next != null) {
                views.setTextViewText(R.id.w_next_label, String.format(t("%s vaktine"), t(next.type.titleTr)))
                val remaining = next.timestampMillis - System.currentTimeMillis()
                views.setChronometer(R.id.w_countdown, SystemClock.elapsedRealtime() + remaining, null, true)
                views.setChronometerCountDown(R.id.w_countdown, true)
            }
            schedule.prayerTimes.forEachIndexed { i, item ->
                if (i > 5) return@forEachIndexed
                views.setTextViewText(nameIds[i], t(item.type.titleTr))
                views.setTextViewText(timeIds[i], item.timeFormatted)
                val color = if (item.isNext) 0xFFD8B875.toInt() else 0xFFF7F4EC.toInt()
                views.setTextColor(nameIds[i], if (item.isNext) color else 0xFFA9B5AF.toInt())
                views.setTextColor(timeIds[i], color)
            }
            val open = PendingIntent.getActivity(
                context, 60,
                Intent(context, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_OPEN_SCREEN, "PRAYER_TIMES")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.w_root, open)
            manager.updateAppWidget(widgetId, views)
        }
    }
}
