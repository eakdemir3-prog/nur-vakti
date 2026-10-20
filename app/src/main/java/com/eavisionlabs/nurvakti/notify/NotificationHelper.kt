package com.eavisionlabs.nurvakti.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.eavisionlabs.nurvakti.MainActivity
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.i18n.t

object NotificationHelper {

    const val CH_PRAYER_SOUND = "vakit_sesli"
    const val CH_PRAYER_SILENT = "vakit_sessiz"
    const val CH_PRAYER_VIBRATE = "vakit_titresim"
    const val CH_ADHAN = "vakit_ezan"
    const val CH_REMINDER = "vakit_hatirlatma"
    const val CH_COUNTDOWN = "vakit_geri_sayim"
    const val CH_GENERAL = "genel_hatirlatmalar"

    const val ID_COUNTDOWN = 1000
    const val ID_ADHAN_SERVICE = 1001
    const val ID_PRAYER_BASE = 1100
    const val ID_REMINDER_BASE = 1200
    const val ID_FRIDAY = 1300
    const val ID_KANDIL = 1301
    const val ID_DAILY_VERSE = 1302

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val notifAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val sound = NotificationChannel(CH_PRAYER_SOUND, t("Vakit bildirimi (sesli)"), NotificationManager.IMPORTANCE_HIGH).apply {
            description = t("Namaz vakti girdiğinde bildirim sesiyle uyarır")
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), notifAttrs)
            enableVibration(true)
        }
        val silent = NotificationChannel(CH_PRAYER_SILENT, t("Vakit bildirimi (sessiz)"), NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = t("Ses ve titreşim olmadan bildirim gösterir")
            setSound(null, null)
            enableVibration(false)
        }
        val vibrate = NotificationChannel(CH_PRAYER_VIBRATE, t("Vakit bildirimi (titreşim)"), NotificationManager.IMPORTANCE_HIGH).apply {
            description = t("Yalnızca titreşimle uyarır")
            setSound(null, null)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 600)
        }
        val adhan = NotificationChannel(CH_ADHAN, t("Ezan"), NotificationManager.IMPORTANCE_HIGH).apply {
            description = t("Ezan okunurken gösterilen bildirim")
            setSound(null, null)
            enableVibration(false)
        }
        val reminder = NotificationChannel(CH_REMINDER, t("Vakit öncesi hatırlatma"), NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = t("Vakit girmeden önce hatırlatır")
        }
        val countdown = NotificationChannel(CH_COUNTDOWN, t("Sıradaki vakit (kalıcı)"), NotificationManager.IMPORTANCE_LOW).apply {
            description = t("Bildirim çubuğunda sıradaki vakte kalan süreyi gösterir")
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }
        val general = NotificationChannel(CH_GENERAL, t("Cuma, kandil ve günün ayeti"), NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = t("Cuma ve kandil hatırlatmaları, günün ayeti")
        }
        nm.createNotificationChannels(listOf(sound, silent, vibrate, adhan, reminder, countdown, general))
    }

    fun canPost(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun openAppIntent(context: Context, requestCode: Int = 0, screen: String? = null): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (screen != null) putExtra(MainActivity.EXTRA_OPEN_SCREEN, screen)
        }
        return PendingIntent.getActivity(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun notify(context: Context, id: Int, builder: NotificationCompat.Builder) {
        if (!canPost(context)) return
        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun cancel(context: Context, id: Int) = NotificationManagerCompat.from(context).cancel(id)

    fun base(context: Context, channel: String): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_stat_crescent)
            .setColor(0xFFD8B875.toInt())
            .setAutoCancel(true)
}
