package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.billing.BillingManager
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.SubScreenScaffold
import com.eavisionlabs.nurvakti.ui.theme.*

@Composable
fun PremiumScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onPurchase: (String) -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlan by remember { mutableStateOf("yearly") }
    val prices = uiState.premiumPrices
    fun price(id: String) = prices[id] ?: "—"

    SubScreenScaffold(title = t("NurVakti Pro"), onBack = onBack) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding).testTag("premium_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(GoldChampagne.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(40.dp)) }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        t("İbadet yolculuğunuzu derinleştirin"),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GoldLight),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t("Namaz vakitleri, ezan, Kur'an ve tüm temel özellikler her zaman ücretsizdir. Pro, ek araçlarla geliştirmemize destek olur."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(18.dp))
                }
            }

            if (uiState.isPremiumActive) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(t("NurVakti Pro aktif"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    when (uiState.premiumProduct) {
                                        BillingManager.LIFETIME -> t("Ömür boyu erişim")
                                        BillingManager.SUB_MONTHLY -> t("Aylık abonelik")
                                        BillingManager.SUB_YEARLY -> t("Yıllık abonelik")
                                        else -> ""
                                    } + " • " + t("Desteğiniz için teşekkür ederiz."),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProFeature(Icons.Default.Palette, t("Kâbe Örtüsü (Kiswa) teması"), t("Siyah-altın özel uygulama teması."))
                        ProFeature(Icons.Default.Repeat, t("Hıfz (ezber) modu"), t("Her ayeti 3, 5 veya 10 kez tekrarlatma; Arapça metni ya da meali gizleyip kendinizi sınama."))
                        ProFeature(Icons.Default.Insights, t("Aylık ibadet analizleri"), t("Son 30 günün namaz grafiği ve ayrıntılı seri istatistikleri."))
                        ProFeature(Icons.Default.Share, t("İmsakiye paylaşımı"), t("Bulunduğunuz yerin Ramazan imsakiyesini metin olarak paylaşın."))
                        ProFeature(Icons.Default.Favorite, t("Geliştirmeye destek"), t("NurVakti'nin reklamsız ve ücretsiz kalmasına katkı sağlarsınız."))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (!uiState.isPremiumActive) {
                item {
                    Text(t("Planınızı seçin"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlanCard(t("Yıllık"), price(BillingManager.SUB_YEARLY), t("yıllık"), t("EN AVANTAJLI"), selectedPlan == "yearly", { selectedPlan = "yearly" }, Modifier.weight(1f))
                        PlanCard(t("Aylık"), price(BillingManager.SUB_MONTHLY), t("aylık"), null, selectedPlan == "monthly", { selectedPlan = "monthly" }, Modifier.weight(1f))
                        // Ömür boyu yalnızca Play'de tanımlıysa (fiyat geldiyse) gösterilir
                        if (prices.isEmpty() || prices.containsKey(BillingManager.LIFETIME)) {
                            PlanCard(t("Ömür Boyu"), price(BillingManager.LIFETIME), t("tek seferlik"), null, selectedPlan == "lifetime", { selectedPlan = "lifetime" }, Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onPurchase(selectedPlan) },
                        enabled = !uiState.isPurchasing,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest)
                    ) {
                        if (uiState.isPurchasing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = EmeraldDarkest)
                        else Text(t("Devam et"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    if (prices.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            t("Fiyatlar Google Play'den yükleniyor. Uygulama Play Store'dan yüklenmediyse satın alma yapılamaz."),
                            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = onRestore, modifier = Modifier.fillMaxWidth()) { Text(t("Satın alımları geri yükle"), color = GoldChampagne) }
                Text(
                    t("Abonelikler Google Play hesabınız üzerinden yönetilir ve dönem sonunda otomatik yenilenir. İstediğiniz zaman Google Play > Ödemeler ve abonelikler bölümünden iptal edebilirsiniz."),
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun ProFeature(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(GoldChampagne.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlanCard(
    title: String, price: String, period: String, badge: String?,
    isSelected: Boolean, onSelect: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) GoldChampagne else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(vertical = 12.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (badge != null) {
                Surface(shape = RoundedCornerShape(8.dp), color = GoldChampagne) {
                    Text(badge, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = EmeraldDarkest, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), maxLines = 1)
                }
                Spacer(Modifier.height(4.dp))
            } else Spacer(Modifier.height(18.dp))
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(price, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (isSelected) GoldChampagne else MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(period, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
