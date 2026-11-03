package com.eavisionlabs.nurvakti.ads

import android.app.Activity
import android.content.Context
import com.eavisionlabs.nurvakti.BuildConfig
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Orta yoğunlukta reklam politikası (banner yok):
 *  - Geçiş reklamı: doğal bir bitişte, en az 3 dakika arayla, günde en fazla 6.
 *    Kurulumdan sonraki ilk 30 dakika gösterilmez.
 *  - Açılış reklamı: uygulamaya arka plandan (en az 30 sn sonra) dönüşte, en az 4 saat arayla,
 *    kurulumun ilk gününde ve bildirim/widget'tan açılışta gösterilmez.
 *  - Ödüllü reklam: kullanıcı isterse, 24 saatlik Pro denemesi.
 *  - Pro kullanıcılara hiç reklam gösterilmez. Kur'an, ezan ve vakit ekranlarında reklam yok.
 */
object AdsManager {

    private const val INTERSTITIAL_GAP_MS = 3 * 60_000L
    private const val INTERSTITIAL_DAILY_CAP = 6
    private const val INSTALL_GRACE_MS = 30 * 60_000L
    private const val APP_OPEN_GAP_MS = 4 * 60 * 60_000L
    private const val APP_OPEN_FIRST_DAY_MS = 24 * 60 * 60_000L
    private const val AD_TTL_MS = 55 * 60_000L
    private const val APP_OPEN_TTL_MS = 4 * 60 * 60_000L
    const val REWARD_DURATION_MS = 24 * 60 * 60_000L

    private val initStarted = AtomicBoolean(false)
    @Volatile private var sdkReady = false

    private var interstitial: InterstitialAd? = null
    private var interstitialLoadedAt = 0L
    private var interstitialLoading = false

    private var appOpen: AppOpenAd? = null
    private var appOpenLoadedAt = 0L
    private var appOpenLoading = false

    private var rewarded: RewardedAd? = null
    private var rewardedLoading = false
    /** Ödüllü reklam hazır olduğunda arayüze haber verir */
    var onRewardedAvailabilityChanged: ((Boolean) -> Unit)? = null

    /** Bir tam ekran reklam şu an gösteriliyor mu (arka plan/ön plan geçişini yanlış saymamak için) */
    @Volatile var isShowingFullScreen = false
        private set

    val isConfigured: Boolean
        get() = BuildConfig.AD_INTERSTITIAL.isNotBlank() || BuildConfig.AD_APP_OPEN.isNotBlank() || BuildConfig.AD_REWARDED.isNotBlank()

    val rewardedReady: Boolean get() = rewarded != null

    private fun prefs(c: Context) = UserPreferences(c.applicationContext)

    private fun isPaidPro(c: Context) = prefs(c).isPremiumActive

    private fun installTime(c: Context): Long = runCatching {
        c.packageManager.getPackageInfo(c.packageName, 0).firstInstallTime
    }.getOrDefault(0L)

    /** Açılışta bir kez: onay penceresi (gerekirse) ve SDK başlatma. */
    fun init(activity: Activity) {
        if (!isConfigured || isPaidPro(activity)) return
        if (!initStarted.compareAndSet(false, true)) return
        val consent = UserMessagingPlatform.getConsentInformation(activity)
        val params = ConsentRequestParameters.Builder().build()
        consent.requestConsentInfoUpdate(activity, params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ ->
                if (consent.canRequestAds()) startSdk(activity)
            }
        }, { _ ->
            // Onay bilgisi alınamadı; önceki oturumda izin verildiyse yine de başlat
            if (consent.canRequestAds()) startSdk(activity)
        })
        if (consent.canRequestAds()) startSdk(activity)
    }

    /** Ayarlardaki "Reklam gizlilik seçenekleri" (yalnızca gerekli bölgelerde) */
    fun privacyOptionsRequired(context: Context): Boolean = isConfigured &&
        UserMessagingPlatform.getConsentInformation(context).privacyOptionsRequirementStatus ==
        ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { _ -> }
    }

    private fun startSdk(activity: Activity) {
        if (sdkReady) return
        val app = activity.applicationContext
        Thread {
            MobileAds.initialize(app) {
                sdkReady = true
                activity.runOnUiThread { preload(app) }
            }
        }.start()
    }

    private fun preload(c: Context) {
        if (!sdkReady || isPaidPro(c)) return
        loadInterstitial(c)
        loadAppOpen(c)
        loadRewarded(c)
    }

    // ------------------------------------------------------------------
    // Geçiş reklamı
    // ------------------------------------------------------------------
    private fun loadInterstitial(c: Context) {
        if (BuildConfig.AD_INTERSTITIAL.isBlank() || interstitialLoading) return
        if (interstitial != null && System.currentTimeMillis() - interstitialLoadedAt < AD_TTL_MS) return
        interstitialLoading = true
        InterstitialAd.load(c, BuildConfig.AD_INTERSTITIAL, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitial = ad; interstitialLoadedAt = System.currentTimeMillis(); interstitialLoading = false
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitial = null; interstitialLoading = false
            }
        })
    }

    /** Doğal bir bitişte çağrılır; sıklık sınırları uygunsa geçiş reklamı gösterir. */
    fun maybeShowInterstitial(activity: Activity) {
        if (!sdkReady || isPaidPro(activity) || isShowingFullScreen) return
        val p = prefs(activity)
        val now = System.currentTimeMillis()
        if (now - installTime(activity) < INSTALL_GRACE_MS) return
        val today = UserPreferences.dayKey()
        if (p.adsDayKey != today) { p.adsDayKey = today; p.adsDayCount = 0 }
        if (p.adsDayCount >= INTERSTITIAL_DAILY_CAP) return
        if (now - p.adsLastInterstitial < INTERSTITIAL_GAP_MS) return
        val ad = interstitial
        if (ad == null || now - interstitialLoadedAt > AD_TTL_MS) { interstitial = null; loadInterstitial(activity); return }
        interstitial = null
        ad.fullScreenContentCallback = fullScreenCallback(activity) { loadInterstitial(activity.applicationContext) }
        p.adsLastInterstitial = now
        p.adsDayCount = p.adsDayCount + 1
        isShowingFullScreen = true
        ad.show(activity)
    }

    // ------------------------------------------------------------------
    // Açılış reklamı
    // ------------------------------------------------------------------
    private fun loadAppOpen(c: Context) {
        if (BuildConfig.AD_APP_OPEN.isBlank() || appOpenLoading) return
        if (appOpen != null && System.currentTimeMillis() - appOpenLoadedAt < APP_OPEN_TTL_MS) return
        appOpenLoading = true
        AppOpenAd.load(c, BuildConfig.AD_APP_OPEN, AdRequest.Builder().build(), object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpen = ad; appOpenLoadedAt = System.currentTimeMillis(); appOpenLoading = false
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                appOpen = null; appOpenLoading = false
            }
        })
    }

    /** Uygulama arka plandan döndüğünde çağrılır. */
    fun maybeShowAppOpen(activity: Activity) {
        if (!sdkReady || isPaidPro(activity) || isShowingFullScreen) return
        val p = prefs(activity)
        val now = System.currentTimeMillis()
        if (now - installTime(activity) < APP_OPEN_FIRST_DAY_MS) return
        if (now - p.adsLastAppOpen < APP_OPEN_GAP_MS) return
        // Geçiş reklamıyla üst üste binmesin
        if (now - p.adsLastInterstitial < 60_000L) return
        val ad = appOpen
        if (ad == null || now - appOpenLoadedAt > APP_OPEN_TTL_MS) { appOpen = null; loadAppOpen(activity); return }
        appOpen = null
        ad.fullScreenContentCallback = fullScreenCallback(activity) { loadAppOpen(activity.applicationContext) }
        p.adsLastAppOpen = now
        isShowingFullScreen = true
        ad.show(activity)
    }

    // ------------------------------------------------------------------
    // Ödüllü reklam (24 saat Pro)
    // ------------------------------------------------------------------
    private fun loadRewarded(c: Context) {
        if (BuildConfig.AD_REWARDED.isBlank() || rewardedLoading || rewarded != null) return
        rewardedLoading = true
        RewardedAd.load(c, BuildConfig.AD_REWARDED, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewarded = ad; rewardedLoading = false
                onRewardedAvailabilityChanged?.invoke(true)
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewarded = null; rewardedLoading = false
                onRewardedAvailabilityChanged?.invoke(false)
            }
        })
    }

    /** Ödüllü reklamı gösterir; izlenirse [onReward] çağrılır. Reklam hazır değilse false döner. */
    fun showRewarded(activity: Activity, onReward: () -> Unit): Boolean {
        val ad = rewarded ?: run { if (sdkReady) loadRewarded(activity); return false }
        rewarded = null
        onRewardedAvailabilityChanged?.invoke(false)
        ad.fullScreenContentCallback = fullScreenCallback(activity) { loadRewarded(activity.applicationContext) }
        isShowingFullScreen = true
        ad.show(activity) { _ -> onReward() }
        return true
    }

    private fun fullScreenCallback(activity: Activity, reload: () -> Unit) = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            isShowingFullScreen = false
            reload()
        }
        override fun onAdFailedToShowFullScreenContent(error: AdError) {
            isShowingFullScreen = false
            reload()
        }
    }

    /** Pro satın alındığında yüklenmiş reklamları bırak. */
    fun onBecamePro() {
        interstitial = null; appOpen = null; rewarded = null
        onRewardedAvailabilityChanged?.invoke(false)
    }
}
