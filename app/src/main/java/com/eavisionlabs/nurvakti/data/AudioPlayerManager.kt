package com.eavisionlabs.nurvakti.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

data class PlayerState(
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val currentSurahNumber: Int = 1,
    val currentSurahNameTr: String = "Fâtiha",
    val currentSurahNameAr: String = "الفاتحة",
    val currentReciterKey: String = "alafasy",
    val currentReciterName: String = "Mishary Rashid Alafasy",
    val progressMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val sleepTimerMinutesRemaining: Int? = null,
    val errorMessage: String? = null,
    /** Ayet ayet çalma modu (okuyucuda takip / hıfz tekrarı) */
    val ayahMode: Boolean = false,
    val currentAyahSurah: Int = 0,
    val currentAyah: Int = 0,
    /** Her ayetin kaç kez tekrar edileceği (hıfz) */
    val repeatEach: Int = 1,
    val currentRepeat: Int = 1
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var sleepTimer: CountDownTimer? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val currentPos = player.currentPosition
                    val duration = player.duration
                    _playerState.value = _playerState.value.copy(
                        progressMs = currentPos,
                        durationMs = if (duration > 0) duration else _playerState.value.durationMs
                    )
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    fun playSurah(surahNumber: Int, reciterKey: String? = null) {
        val surah = QuranRepository.surahs.find { it.number == surahNumber } ?: return
        val reciter = reciterKey ?: _playerState.value.currentReciterKey
        val reciterName = when (reciter) {
            "abdulbaset" -> "AbdulBaset AbdulSamad"
            "maher" -> "Maher Al-Muaiqly"
            "ghamdi" -> "Saad Al-Ghamdi"
            else -> "Mishary Rashid Alafasy"
        }

        stop()
        ayahQueue = emptyList()

        _playerState.value = _playerState.value.copy(
            ayahMode = false,
            status = PlaybackStatus.BUFFERING,
            currentSurahNumber = surah.number,
            currentSurahNameTr = surah.nameTurkish,
            currentSurahNameAr = surah.nameArabic,
            currentReciterKey = reciter,
            currentReciterName = reciterName,
            progressMs = 0,
            durationMs = 0,
            errorMessage = null
        )

        try {
            val url = QuranRepository.getSurahAudioUrl(surahNumber, reciter)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { player ->
                    setSpeed(_playerState.value.playbackSpeed)
                    player.start()
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.PLAYING,
                        durationMs = player.duration
                    )
                    handler.post(progressRunnable)
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.PAUSED,
                        progressMs = 0
                    )
                    // Auto play next surah if available
                    if (surahNumber < 114) {
                        playSurah(surahNumber + 1)
                    }
                }
                setOnErrorListener { _, what, extra ->
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.ERROR,
                        errorMessage = "Ses yüklenemedi (Hata kodu: $what, $extra). İnternet bağlantınızı kontrol ediniz."
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                status = PlaybackStatus.ERROR,
                errorMessage = "Oynatıcı başlatılamadı: ${e.localizedMessage}"
            )
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _playerState.value = _playerState.value.copy(status = PlaybackStatus.PAUSED)
            handler.removeCallbacks(progressRunnable)
        } else {
            player.start()
            _playerState.value = _playerState.value.copy(status = PlaybackStatus.PLAYING)
            handler.post(progressRunnable)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { player ->
            player.seekTo(positionMs)
            _playerState.value = _playerState.value.copy(progressMs = positionMs)
        }
    }

    fun seekRelative(secondsDelta: Int) {
        mediaPlayer?.let { player ->
            val newPos = (player.currentPosition + secondsDelta * 1000).coerceIn(0, player.duration)
            player.seekTo(newPos)
            _playerState.value = _playerState.value.copy(progressMs = newPos)
        }
    }

    fun setSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { player ->
                try {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                } catch (_: Exception) {}
            }
        }
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    // ------------------------------------------------------------------
    // Ayet ayet çalma
    // ------------------------------------------------------------------
    private var ayahQueue: List<Pair<Int, Int>> = emptyList()
    private var ayahIndex = 0

    /**
     * [queue] içindeki ayetleri sırayla çalar; her ayet [repeatEach] kez tekrarlanır.
     * [reciterKey] everyayah klasör anahtarı.
     */
    fun playAyahs(queue: List<Pair<Int, Int>>, startIndex: Int, repeatEach: Int, reciterKey: String) {
        if (queue.isEmpty()) return
        stop()
        ayahQueue = queue
        ayahIndex = startIndex.coerceIn(0, queue.lastIndex)
        val first = queue[ayahIndex]
        val surah = QuranRepository.surahs.getOrNull(first.first - 1)
        _playerState.value = _playerState.value.copy(
            ayahMode = true,
            repeatEach = repeatEach.coerceAtLeast(1),
            currentRepeat = 1,
            currentReciterKey = reciterKey,
            currentSurahNumber = first.first,
            currentSurahNameTr = surah?.nameTurkish ?: "",
            currentSurahNameAr = surah?.nameArabic ?: "",
            errorMessage = null
        )
        playCurrentAyah()
    }

    fun setRepeatEach(times: Int) {
        _playerState.value = _playerState.value.copy(repeatEach = times.coerceAtLeast(1))
    }

    private fun playCurrentAyah() {
        val (s, a) = ayahQueue.getOrNull(ayahIndex) ?: run { stop(); return }
        releaseOnly()
        val surah = QuranRepository.surahs.getOrNull(s - 1)
        _playerState.value = _playerState.value.copy(
            status = PlaybackStatus.BUFFERING,
            currentAyahSurah = s,
            currentAyah = a,
            currentSurahNumber = s,
            currentSurahNameTr = surah?.nameTurkish ?: _playerState.value.currentSurahNameTr,
            currentSurahNameAr = surah?.nameArabic ?: _playerState.value.currentSurahNameAr,
            progressMs = 0,
            durationMs = 0
        )
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(QuranRepository.getAyahAudioUrl(s, a, _playerState.value.currentReciterKey))
                setOnPreparedListener { player ->
                    setSpeed(_playerState.value.playbackSpeed)
                    player.start()
                    _playerState.value = _playerState.value.copy(status = PlaybackStatus.PLAYING, durationMs = player.duration)
                    handler.post(progressRunnable)
                }
                setOnCompletionListener { onAyahCompleted() }
                setOnErrorListener { _, what, extra ->
                    _playerState.value = _playerState.value.copy(
                        status = PlaybackStatus.ERROR,
                        errorMessage = "Ayet sesi yüklenemedi ($what, $extra). İnternet bağlantınızı kontrol edin."
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(status = PlaybackStatus.ERROR, errorMessage = e.localizedMessage)
        }
    }

    private fun onAyahCompleted() {
        val st = _playerState.value
        if (st.currentRepeat < st.repeatEach) {
            _playerState.value = st.copy(currentRepeat = st.currentRepeat + 1)
            playCurrentAyah()
            return
        }
        if (ayahIndex < ayahQueue.lastIndex) {
            ayahIndex++
            _playerState.value = st.copy(currentRepeat = 1)
            playCurrentAyah()
        } else {
            stop()
        }
    }

    private fun releaseOnly() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let {
            try { if (it.isPlaying) it.stop(); it.reset(); it.release() } catch (_: Exception) {}
        }
        mediaPlayer = null
    }

    fun playNext() {
        if (_playerState.value.ayahMode) {
            if (ayahIndex < ayahQueue.lastIndex) { ayahIndex++; _playerState.value = _playerState.value.copy(currentRepeat = 1); playCurrentAyah() }
            return
        }
        val next = _playerState.value.currentSurahNumber + 1
        if (next <= 114) {
            playSurah(next)
        }
    }

    fun playPrevious() {
        if (_playerState.value.ayahMode) {
            if (ayahIndex > 0) { ayahIndex--; _playerState.value = _playerState.value.copy(currentRepeat = 1); playCurrentAyah() }
            return
        }
        val prev = _playerState.value.currentSurahNumber - 1
        if (prev >= 1) {
            playSurah(prev)
        }
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimer?.cancel()
        if (minutes == null || minutes <= 0) {
            _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = null)
            return
        }
        _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = minutes)
        sleepTimer = object : CountDownTimer(minutes * 60 * 1000L, 60 * 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingMins = (millisUntilFinished / (60 * 1000)).toInt() + 1
                _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = remainingMins)
            }

            override fun onFinish() {
                togglePlayPause()
                _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = null)
            }
        }.start()
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            } catch (_: Exception) {}
        }
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(status = PlaybackStatus.IDLE, currentAyah = 0, currentAyahSurah = 0)
    }
}
