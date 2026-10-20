package com.eavisionlabs.nurvakti.notify

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.t

/**
 * Vakit girdiğinde ezanı okur. Kullanıcı bir ezan ses dosyası seçtiyse onu,
 * seçmediyse cihazın alarm sesini çalar. Bildirimdeki "Durdur" ile ya da ses tuşlarıyla susturulabilir.
 */
class AdhanService : Service() {

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_TEXT = "text"
        const val ACTION_STOP = "com.eavisionlabs.nurvakti.STOP_ADHAN"
        const val EXTRA_PREVIEW = "preview"
        private const val MAX_DEFAULT_TONE_MS = 45_000L
        private const val MAX_ADHAN_MS = 6 * 60_000L
    }

    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable { stopSelfSafely() }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelfSafely()
            return START_NOT_STICKY
        }
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: t("Ezan")
        val text = intent?.getStringExtra(EXTRA_TEXT) ?: ""
        startAsForeground(title, text)
        play()
        return START_NOT_STICKY
    }

    private fun startAsForeground(title: String, text: String) {
        NotificationHelper.createChannels(this)
        val stopPi = PendingIntent.getService(
            this, 77, Intent(this, AdhanService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n = NotificationHelper.base(this, NotificationHelper.CH_ADHAN)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(NotificationHelper.openAppIntent(this, 40, "PRAYER_TIMES"))
            .setDeleteIntent(stopPi)
            .addAction(0, t("Durdur"), stopPi)
            .build()
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
        ServiceCompat.startForeground(this, NotificationHelper.ID_ADHAN_SERVICE, n, type)
    }

    private fun play() {
        releasePlayer()
        val prefs = UserPreferences(this)
        val custom = prefs.adhanSoundUri?.let { runCatching { Uri.parse(it) }.getOrNull() }
        val uri = custom
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (uri == null) { stopSelfSafely(); return }

        val p = MediaPlayer()
        try {
            p.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            p.setDataSource(this, uri)
            p.isLooping = custom == null
            p.setOnCompletionListener { stopSelfSafely() }
            p.setOnErrorListener { _, _, _ ->
                if (custom != null) {
                    // Seçilen dosya açılamadıysa (silinmiş olabilir) alarm sesine dön
                    prefs.adhanSoundUri = null
                    play()
                } else stopSelfSafely()
                true
            }
            p.prepare()
            p.start()
            player = p
            handler.postDelayed(stopRunnable, if (custom == null) MAX_DEFAULT_TONE_MS else MAX_ADHAN_MS)
        } catch (_: Exception) {
            p.release()
            if (custom != null) {
                prefs.adhanSoundUri = null
                play()
            } else stopSelfSafely()
        }
    }

    private fun releasePlayer() {
        handler.removeCallbacks(stopRunnable)
        player?.let { runCatching { it.stop() }; it.release() }
        player = null
    }

    private fun stopSelfSafely() {
        releasePlayer()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }
}
