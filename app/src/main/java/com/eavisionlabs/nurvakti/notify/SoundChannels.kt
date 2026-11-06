package com.eavisionlabs.nurvakti.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.t
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

/**
 * Ezan ve uyandırma sesleri artık bildirimin kendi sesi olarak çalar (ön plan hizmeti yok).
 * Bildirim izni kapalıysa ses de çalmaz; bildirimi kaydırınca ses susar.
 * Android'de kanal sesi sonradan değiştirilemediği için her ses için ayrı kanal kimliği kullanılır.
 */
object SoundChannels {

    private const val ADHAN_PREFIX = "ezan_ses_"
    const val CH_WAKE = "uyandirma_ney_v1"

    private val attrs: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private fun rawUri(context: Context, resId: Int): Uri =
        Uri.parse("android.resource://${context.packageName}/$resId")

    /** Kullanıcının seçtiği ezan dosyasının uygulama içindeki kopyası (sistem ses çalıcısı okuyabilsin diye) */
    private fun customFile(context: Context): File = File(File(context.filesDir, "sounds").apply { mkdirs() }, "ozel_ezan")

    /** Seçilen ses dosyasını kopyalar; başarılıysa true */
    fun importCustomAdhan(context: Context, source: Uri): Boolean = runCatching {
        context.contentResolver.openInputStream(source)!!.use { input ->
            customFile(context).outputStream().use { input.copyTo(it) }
        }
        true
    }.getOrDefault(false)

    fun clearCustomAdhan(context: Context) { runCatching { customFile(context).delete() } }

    private fun adhanSound(context: Context): Pair<String, Uri> {
        val prefs = UserPreferences(context)
        val f = customFile(context)
        // Eski sürümde seçilmiş dosya: bir kez uygulama içine kopyala
        if (prefs.adhanSoundUri != null && !f.exists()) {
            runCatching { importCustomAdhan(context, Uri.parse(prefs.adhanSoundUri)) }
        }
        if (prefs.adhanSoundUri != null && f.exists() && f.length() > 0) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", f)
            // Bildirim sesini sistem arayüzü çalar; okuma izni verilir
            runCatching { context.grantUriPermission("com.android.systemui", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            return "ozel_${f.length()}_${f.lastModified() / 1000}" to uri
        }
        return "varsayilan_v1" to rawUri(context, R.raw.ezan_varsayilan)
    }

    /** Ezan kanalının kimliği; kanal yoksa oluşturur, eski ezan kanallarını siler */
    fun adhanChannel(context: Context): String {
        val (key, uri) = adhanSound(context)
        val id = ADHAN_PREFIX + key
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java) ?: return id
            if (nm.getNotificationChannel(id) == null) {
                nm.notificationChannels.filter { it.id.startsWith(ADHAN_PREFIX) || it.id == NotificationHelper.CH_ADHAN }
                    .forEach { runCatching { nm.deleteNotificationChannel(it.id) } }
                nm.createNotificationChannel(
                    NotificationChannel(id, t("Ezan"), NotificationManager.IMPORTANCE_HIGH).apply {
                        description = t("Namaz vakti girdiğinde ezan sesiyle uyarır")
                        setSound(uri, attrs)
                        enableVibration(true)
                    }
                )
            }
        }
        return id
    }

    fun wakeChannel(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java) ?: return CH_WAKE
            if (nm.getNotificationChannel(CH_WAKE) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(CH_WAKE, t("Sahur ve teheccüd alarmı"), NotificationManager.IMPORTANCE_HIGH).apply {
                        description = t("Huzurlu ney sesiyle uyandırır")
                        setSound(rawUri(context, R.raw.huzur_alarm), attrs)
                        enableVibration(true)
                    }
                )
            }
        }
        return CH_WAKE
    }

    // ------------------------------------------------------------------
    // Uygulama içi önizleme (Ayarlar > Dinle): uygulama açıkken medya sesinden çalar
    // ------------------------------------------------------------------
    val previewPlaying = MutableStateFlow(false)
    private var player: MediaPlayer? = null

    fun previewAdhan(context: Context) {
        val f = customFile(context)
        val prefs = UserPreferences(context)
        if (prefs.adhanSoundUri != null && f.exists() && f.length() > 0) play { java.io.FileInputStream(f).use { setDataSource(it.fd) } }
        else playRaw(context, R.raw.ezan_varsayilan)
    }

    fun previewWake(context: Context) = playRaw(context, R.raw.huzur_alarm)

    private fun playRaw(context: Context, resId: Int) = play {
        context.resources.openRawResourceFd(resId).use { afd -> setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) }
    }

    private fun play(source: MediaPlayer.() -> Unit) {
        stopPreview()
        val p = MediaPlayer()
        try {
            p.setAudioAttributes(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            p.source()
            p.setOnCompletionListener { stopPreview() }
            p.setOnErrorListener { _, _, _ -> stopPreview(); true }
            p.prepare()
            p.start()
            player = p
            previewPlaying.value = true
        } catch (_: Exception) {
            p.release()
            previewPlaying.value = false
        }
    }

    fun stopPreview() {
        player?.let { runCatching { it.stop() }; it.release() }
        player = null
        previewPlaying.value = false
    }
}
