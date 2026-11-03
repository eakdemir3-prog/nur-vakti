package com.eavisionlabs.nurvakti.notify

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.eavisionlabs.nurvakti.MainActivity
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.data.DailyContent
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t

/** Ana ekran widget'ı: her gün değişen günün ayeti (Arapça + meal). Dokununca ayet okuyucuda açılır. */
class VerseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it) }
    }

    companion object {
        const val SCREEN_PREFIX = "AYAH:"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, VerseWidget::class.java))
            ids.forEach { render(context, manager, it) }
        }

        private fun render(context: Context, manager: AppWidgetManager, widgetId: Int) {
            I18n.language = UserPreferences(context).language
            val views = RemoteViews(context.packageName, R.layout.widget_verse)
            val v = runCatching { DailyContent.verseOfDay(context) }.getOrNull()
            views.setTextViewText(R.id.v_title, t("Günün Ayeti"))
            if (v != null) {
                views.setTextViewText(R.id.v_ref, v.reference)
                views.setTextViewText(R.id.v_arabic, v.arabic)
                views.setTextViewText(R.id.v_text, v.text)
            }
            val open = PendingIntent.getActivity(
                context, 61,
                Intent(context, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_OPEN_SCREEN, if (v != null) "$SCREEN_PREFIX${v.surah}:${v.ayah}" else "HOME")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.v_root, open)
            manager.updateAppWidget(widgetId, views)
        }
    }
}
