package com.eavisionlabs.nurvakti.billing

import android.app.Activity
import android.content.Context
import com.eavisionlabs.nurvakti.BuildConfig
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * NurVakti Pro satın alımları — RevenueCat.
 *
 * RevenueCat panelinde:
 *  - Entitlement: `pro`
 *  - Offering (current): paketler $rc_monthly, $rc_annual, $rc_lifetime
 * Google Play'de:
 *  - Abonelik `nurvakti_pro` (temel planlar: `aylik`, `yillik`)
 *  - Uygulama içi ürün `nurvakti_pro_lifetime`
 *
 * Ekranlar plan anahtarlarıyla (SUB_MONTHLY / SUB_YEARLY / LIFETIME) çalışır;
 * bu sınıf onları RevenueCat paketlerine eşler.
 */
class BillingManager(context: Context) {

    companion object {
        const val SUB_MONTHLY = "monthly"
        const val SUB_YEARLY = "yearly"
        const val LIFETIME = "lifetime"
        const val ENTITLEMENT = "pro"

        @Volatile private var configured = false

        /** Uygulama açılışında bir kez çağrılır. Anahtar yoksa satın alma devre dışı kalır. */
        fun configure(context: Context) {
            if (configured || BuildConfig.REVENUECAT_API_KEY.isBlank()) return
            Purchases.configure(
                PurchasesConfiguration.Builder(context.applicationContext, BuildConfig.REVENUECAT_API_KEY).build()
            )
            configured = true
        }
    }

    data class State(
        val isConnected: Boolean = false,
        val isPremium: Boolean = false,
        val activeProduct: String? = null,
        /** plan anahtarı -> Play'deki biçimli fiyat ("49,99 ₺") */
        val prices: Map<String, String> = emptyMap(),
        val message: String? = null,
        val isPurchasing: Boolean = false
    )

    private val appContext = context.applicationContext
    private val prefs = UserPreferences(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val packages = mutableMapOf<String, Package>()

    private val _state = MutableStateFlow(State(isPremium = prefs.isPremiumActive, activeProduct = prefs.premiumProductId))
    val state: StateFlow<State> = _state.asStateFlow()

    private val listener = UpdatedCustomerInfoListener { info -> apply(info) }

    fun connect() {
        configure(appContext)
        if (!configured) return
        Purchases.sharedInstance.updatedCustomerInfoListener = listener
        scope.launch {
            runCatching { loadOfferings() }
            runCatching { apply(Purchases.sharedInstance.awaitCustomerInfo()) }
        }
    }

    private suspend fun loadOfferings() {
        val offering = Purchases.sharedInstance.awaitOfferings().current ?: return
        val prices = mutableMapOf<String, String>()
        for (p in offering.availablePackages) {
            val key = when (p.packageType) {
                PackageType.MONTHLY -> SUB_MONTHLY
                PackageType.ANNUAL -> SUB_YEARLY
                PackageType.LIFETIME -> LIFETIME
                else -> continue
            }
            packages[key] = p
            prices[key] = p.product.price.formatted
        }
        _state.update { it.copy(isConnected = true, prices = prices) }
    }

    private fun apply(info: CustomerInfo) {
        // Önce "pro" yetkisine bakılır; adı farklı tanımlandıysa aktif herhangi bir yetki de Pro sayılır
        val ent = info.entitlements[ENTITLEMENT]?.takeIf { it.isActive }
            ?: info.entitlements.active.values.firstOrNull()
        val premium = ent?.isActive == true
        val productId = ent?.productIdentifier
        val plan = when {
            !premium -> null
            productId == null -> SUB_YEARLY
            productId.contains("lifetime") -> LIFETIME
            productId.contains("aylik") || productId.contains("month") -> SUB_MONTHLY
            else -> SUB_YEARLY
        }
        prefs.isPremiumActive = premium
        prefs.premiumProductId = plan
        _state.update { it.copy(isPremium = premium, activeProduct = plan, isPurchasing = false) }
    }

    /** Play'deki geçerli satın alımları RevenueCat üzerinden geri yükler. */
    fun restorePurchases(onDone: ((Boolean) -> Unit)? = null) {
        configure(appContext)
        if (!configured) { onDone?.invoke(_state.value.isPremium); return }
        scope.launch {
            val ok = runCatching { apply(Purchases.sharedInstance.awaitRestore()) }.isSuccess
            if (!ok) _state.update { it.copy(message = "Satın alımlar geri yüklenemedi. İnternet bağlantınızı kontrol edin.") }
            onDone?.invoke(_state.value.isPremium)
        }
    }

    /** Satın alma ekranını açar. */
    fun launchPurchase(activity: Activity, planKey: String): Boolean {
        configure(appContext)
        val pkg = packages[planKey]
        if (!configured || pkg == null) {
            _state.update {
                it.copy(message = "Mağaza bağlantısı kurulamadı. Uygulamanın Google Play'den yüklendiğinden ve internetin açık olduğundan emin olun.")
            }
            connect()
            return false
        }
        _state.update { it.copy(isPurchasing = true, message = null) }
        scope.launch {
            try {
                val result = Purchases.sharedInstance.awaitPurchase(PurchaseParams.Builder(activity, pkg).build())
                apply(result.customerInfo)
                if (_state.value.isPremium) {
                    _state.update { it.copy(message = "NurVakti Pro etkinleştirildi. Teşekkür ederiz!") }
                } else {
                    _state.update { it.copy(message = "Ödemeniz onay bekliyor. Onaylandığında Pro otomatik açılacak.") }
                }
            } catch (e: PurchasesTransactionException) {
                _state.update {
                    it.copy(
                        isPurchasing = false,
                        message = if (e.userCancelled) null else "Satın alma tamamlanamadı: ${e.error.message}"
                    )
                }
            } catch (e: PurchasesException) {
                _state.update { it.copy(isPurchasing = false, message = "Satın alma tamamlanamadı: ${e.error.message}") }
            }
        }
        return true
    }

    fun clearMessage() = _state.update { it.copy(message = null) }

    fun end() {
        if (configured) Purchases.sharedInstance.updatedCustomerInfoListener = null
        scope.cancel()
    }
}
