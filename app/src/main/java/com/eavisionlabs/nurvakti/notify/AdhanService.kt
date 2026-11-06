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
        /** Ses çalıyor mu (bildirim izni kapalıyken uygulama içinden durdurabilmek için) */
        val playing = kotlinx.coroutines.flow.MutableStateFlow(false)

        fun stop(context: android.content.Context) {
            runCatching { context.startService(Intent(context, AdhanService::class.java).setAction(ACTION_STOP)) }
            playing.value = false
        }

        const val EXTRA_TITLE = "title"
        const val EXTRA_TEXT = "text"
        const val ACTION_STOP = "com.eavisionlabs.nurvakti.STOP_ADHAN"
        const val EXTRA_PREVIEW = "preview"
        /** Sahur / teheccüd uyandırması: uygulamadaki huzurlu ney sesi, sesi yavaşça yükselir */
        const val EXTRA_WAKE = "wake"
        private const val MAX_WAKE_MS = 3 * 60_000L
        private const val PREVIEW_MS = 12_000L
        private const val MAX_DEFAULT_TONE_MS = 45_000L
        private const val MAX_ADHAN_MS = 6 * 60_000L
    }

    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable { stopSelfSafely() }
    private var rampStep = 0
    /** "Dinle" önizlemesi medya ses seviyesinden çalar (kullanıcı ses tuşlarıyla duyar) */
    private var previewMode = false
    private var focusRequest: android.media.AudioFocusRequest? = null

    private fun attrs(): AudioAttributes = AudioAttributes.Builder()
        .setUsage(if (previewMode) AudioAttributes.USAGE_MEDIA else AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private fun requestFocus() {
        val am = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val r = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attrs()).build()
            focusRequest = r
            runCatching { am.requestAudioFocus(r) }
        }
    }

    /** Ses çalınamazsa nedenini bildirimde göster */
    private fun reportError(e: Throwable) {
        runCatching {
            NotificationHelper.notify(
                this, NotificationHelper.ID_ADHAN_SERVICE + 50,
                NotificationHelper.base(this, NotificationHelper.CH_GENERAL)
                    .setContentTitle(t("Ses çalınamadı"))
                    .setContentText("${e.javaClass.simpleName}: ${e.message ?: ""}".take(180))
            )
        }
    }
    private val rampRunnable = object : Runnable {
        override fun run() {
            val p = player ?: return
            rampStep++
            val v = (0.15f + rampStep * 0.05f).coerceAtMost(1f)
            runCatching { p.setVolume(v, v) }
            if (v < 1f) handler.postDelayed(this, 1500L)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelfSafely()
            return START_NOT_STICKY
        }
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: t("Ezan")
        val text = intent?.getStringExtra(EXTRA_TEXT) ?: ""
        startAsForeground(title, text)
        val wake = intent?.getBooleanExtra(EXTRA_WAKE, false) == true
        val preview = intent?.getBooleanExtra(EXTRA_PREVIEW, false) == true
        previewMode = preview
        playing.value = true
        requestFocus()
        if (wake) playWake(preview) else play()
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
        // Kullanıcı dosya seçmediyse uygulamanın kendi ezanı çalar
        if (custom == null && playBundledAdhan()) return
        val uri = custom
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (uri == null) { stopSelfSafely(); return }

        val p = MediaPlayer()
        try {
            p.setAudioAttributes(attrs())
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
        } catch (e: Exception) {
            reportError(e)
            p.release()
            if (custom != null) {
                prefs.adhanSoundUri = null
                play()
            } else stopSelfSafely()
        }
    }

    /**
     * Uygulamaya gömülü sesi dosyaya çıkarır. Play'den gelen pakette ses sıkıştırılmış olabildiği için
     * doğrudan kaynaktan açmak (openRawResourceFd) sessiz kalıyordu; dosyadan çalmak her durumda çalışır.
     */
    private fun rawFile(resId: Int, name: String): java.io.File {
        val dir = java.io.File(filesDir, "sounds").apply { mkdirs() }
        val out = java.io.File(dir, name)
        val tmp = java.io.File(dir, "$name.tmp")
        resources.openRawResource(resId).use { input ->
            val bytes = input.readBytes()
            if (!out.exists() || out.length() != bytes.size.toLong()) {
                tmp.writeBytes(bytes)
                tmp.renameTo(out)
            }
        }
        return out
    }

    /**
     * Ses dosyasını oynatıcıya dosya tanıtıcısı (FileDescriptor) olarak verir.
     * Yol ile vermek (setDataSource(path)) bazı telefonlarda "status=0x80000000" hatası veriyordu,
     * çünkü medya sunucusu uygulamanın özel klasörünü okuyamıyor.
     */
    private fun setSource(p: MediaPlayer, resId: Int, name: String) {
        java.io.FileInputStream(rawFile(resId, name)).use { input -> p.setDataSource(input.fd) }
    }

    /** Uygulamaya gömülü varsayılan ezan; açılamazsa false döner (alarm sesine düşülür) */
    private fun playBundledAdhan(): Boolean {
        val p = MediaPlayer()
        return try {
            p.setAudioAttributes(attrs())
            setSource(p, com.eavisionlabs.nurvakti.R.raw.ezan_varsayilan, "ezan_varsayilan.ogg")
            p.isLooping = false
            p.setOnCompletionListener { stopSelfSafely() }
            p.setOnErrorListener { _, what, extra -> reportError(IllegalStateException("MediaPlayer $what/$extra")); stopSelfSafely(); true }
            p.prepare()
            p.start()
            player = p
            handler.postDelayed(stopRunnable, MAX_ADHAN_MS)
            true
        } catch (e: Exception) {
            reportError(e)
            p.release()
            false
        }
    }

    private fun playWake(preview: Boolean) {
        releasePlayer()
        val p = MediaPlayer()
        try {
            p.setAudioAttributes(attrs())
            setSource(p, com.eavisionlabs.nurvakti.R.raw.huzur_alarm, "huzur_alarm.ogg")
            p.isLooping = true
            p.prepare()
            val start = if (preview) 0.7f else 0.15f
            p.setVolume(start, start)
            p.start()
            player = p
            rampStep = 0
            if (!preview) handler.postDelayed(rampRunnable, 1500L)
            handler.postDelayed(stopRunnable, if (preview) PREVIEW_MS else MAX_WAKE_MS)
        } catch (e: Exception) {
            reportError(e)
            p.release()
            play()
        }
    }

    private fun abandonFocus() {
        val am = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) focusRequest?.let { runCatching { am.abandonAudioFocusRequest(it) } }
        focusRequest = null
    }

    private fun releasePlayer() {
        handler.removeCallbacks(rampRunnable)
        handler.removeCallbacks(stopRunnable)
        player?.let { runCatching { it.stop() }; it.release() }
        player = null
    }

    private fun stopSelfSafely() {
        playing.value = false
        releasePlayer()
        abandonFocus()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        playing.value = false
        releasePlayer()
        super.onDestroy()
    }
}
