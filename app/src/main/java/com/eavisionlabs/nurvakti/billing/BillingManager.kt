package com.eavisionlabs.nurvakti.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.eavisionlabs.nurvakti.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Play Faturalandırma (PBL 8).
 * Play Console'da oluşturulması gereken ürünler:
 *  - nurvakti_pro_monthly  (abonelik)
 *  - nurvakti_pro_yearly   (abonelik)
 *  - nurvakti_pro_lifetime (tek seferlik, tüketilmeyen ürün)
 */
class BillingManager(context: Context) : PurchasesUpdatedListener {

    companion object {
        const val SUB_MONTHLY = "nurvakti_pro_monthly"
        const val SUB_YEARLY = "nurvakti_pro_yearly"
        const val LIFETIME = "nurvakti_pro_lifetime"
        val ALL = listOf(SUB_MONTHLY, SUB_YEARLY, LIFETIME)
    }

    data class State(
        val isConnected: Boolean = false,
        val isPremium: Boolean = false,
        val activeProduct: String? = null,
        /** ürün kimliği -> Play'deki biçimli fiyat ("49,99 ₺") */
        val prices: Map<String, String> = emptyMap(),
        val message: String? = null,
        val isPurchasing: Boolean = false
    )

    private val appContext = context.applicationContext
    private val prefs = UserPreferences(appContext)
    private val _state = MutableStateFlow(State(isPremium = prefs.isPremiumActive, activeProduct = prefs.premiumProductId))
    val state: StateFlow<State> = _state.asStateFlow()

    private val details = mutableMapOf<String, ProductDetails>()

    private val client: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun connect() {
        if (client.isReady) { refresh(); return }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _state.value = _state.value.copy(isConnected = true)
                    refresh()
                } else {
                    _state.value = _state.value.copy(isConnected = false)
                }
            }

            override fun onBillingServiceDisconnected() {
                _state.value = _state.value.copy(isConnected = false)
            }
        })
    }

    private fun refresh() {
        queryDetails(BillingClient.ProductType.SUBS, listOf(SUB_MONTHLY, SUB_YEARLY))
        queryDetails(BillingClient.ProductType.INAPP, listOf(LIFETIME))
        restorePurchases()
    }

    private fun queryDetails(type: String, ids: List<String>) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(ids.map {
                QueryProductDetailsParams.Product.newBuilder().setProductId(it).setProductType(type).build()
            })
            .build()
        client.queryProductDetailsAsync(params) { result, queryResult ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryProductDetailsAsync
            val prices = _state.value.prices.toMutableMap()
            for (pd in queryResult.productDetailsList) {
                details[pd.productId] = pd
                val price = if (pd.productType == BillingClient.ProductType.SUBS) {
                    pd.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice
                } else {
                    pd.oneTimePurchaseOfferDetails?.formattedPrice
                }
                if (price != null) prices[pd.productId] = price
            }
            _state.value = _state.value.copy(prices = prices)
        }
    }

    /** Play'deki geçerli satın alımları kontrol eder (cihaz değişikliği / yeniden kurulum sonrası). */
    fun restorePurchases(onDone: ((Boolean) -> Unit)? = null) {
        if (!client.isReady) { onDone?.invoke(_state.value.isPremium); return }
        val owned = mutableListOf<Purchase>()
        var pending = 2
        val collect = { list: List<Purchase> ->
            synchronized(owned) {
                owned += list
                pending--
                if (pending == 0) {
                    applyPurchases(owned)
                    onDone?.invoke(_state.value.isPremium)
                }
            }
        }
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { r, list -> collect(if (r.responseCode == BillingClient.BillingResponseCode.OK) list else emptyList()) }
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ) { r, list -> collect(if (r.responseCode == BillingClient.BillingResponseCode.OK) list else emptyList()) }
    }

    private fun applyPurchases(purchases: List<Purchase>) {
        val valid = purchases.filter { p ->
            p.purchaseState == Purchase.PurchaseState.PURCHASED && p.products.any { it in ALL }
        }
        valid.filter { !it.isAcknowledged }.forEach { acknowledge(it) }
        val product = valid.flatMap { it.products }.firstOrNull { it in ALL }
        val premium = product != null
        prefs.isPremiumActive = premium
        prefs.premiumProductId = product
        _state.value = _state.value.copy(isPremium = premium, activeProduct = product, isPurchasing = false)
    }

    private fun acknowledge(p: Purchase) {
        client.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(p.purchaseToken).build()
        ) { }
    }

    /** Satın alma ekranını açar. Ürün Play'de bulunamazsa false döner. */
    fun launchPurchase(activity: Activity, productId: String): Boolean {
        val pd = details[productId]
        if (!client.isReady || pd == null) {
            _state.value = _state.value.copy(
                message = "Mağaza bağlantısı kurulamadı. Uygulamanın Google Play'den yüklendiğinden ve internetin açık olduğundan emin olun."
            )
            connect()
            return false
        }
        val paramsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(pd)
        if (pd.productType == BillingClient.ProductType.SUBS) {
            val token = pd.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return false
            paramsBuilder.setOfferToken(token)
        }
        val flow = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(paramsBuilder.build())).build()
        _state.value = _state.value.copy(isPurchasing = true, message = null)
        val result = client.launchBillingFlow(activity, flow)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _state.value = _state.value.copy(isPurchasing = false)
        }
        return result.responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val list = purchases.orEmpty()
                if (list.any { it.purchaseState == Purchase.PurchaseState.PENDING }) {
                    _state.value = _state.value.copy(
                        isPurchasing = false,
                        message = "Ödemeniz onay bekliyor. Onaylandığında Pro otomatik açılacak."
                    )
                }
                if (list.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }) {
                    applyPurchases(list)
                    _state.value = _state.value.copy(message = "NurVakti Pro etkinleştirildi. Teşekkür ederiz!")
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> restorePurchases()
            BillingClient.BillingResponseCode.USER_CANCELED ->
                _state.value = _state.value.copy(isPurchasing = false)
            else -> _state.value = _state.value.copy(
                isPurchasing = false,
                message = "Satın alma tamamlanamadı (kod ${result.responseCode})."
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun end() {
        if (client.isReady) client.endConnection()
    }
}
