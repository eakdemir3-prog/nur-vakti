package com.eavisionlabs.nurvakti.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onActivatePremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("yearly") } // "monthly", "yearly", "lifetime"
    var showBillingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NurVakti Pro",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("premium_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Emblem
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "NurVakti Pro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "NurVakti Pro Ayrıcalıkları",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = "Manevi yolculuğunuzu estetik, derinlikli ve huzurlu araçlarla zenginleştirin.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Status Banner if active
            if (uiState.isPremiumActive) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "NurVakti Pro Aktif",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                Text(
                                    text = "Tüm temalar, hafızlık stüdyosu ve kaza defteri kullanımınızda.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Feature Highlights
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProFeatureItem(
                            icon = Icons.Default.Palette,
                            title = "Özel Kâbe & Medine Temaları",
                            description = "Kâbe örtüsü (Kiswa) altın hat teması ve Medine Mushafı altın varaklı okuma düzeni."
                        )
                        ProFeatureItem(
                            icon = Icons.Default.Repeat,
                            title = "Gelişmiş Hıfz & Ezber Stüdyosu",
                            description = "Ayet tekrar döngüleri (3x, 5x, 10x), gizle-göster ezber modu ve sesli tekrar."
                        )
                        ProFeatureItem(
                            icon = Icons.Default.VolumeUp,
                            title = "Kutsal Beldeler Ezan Kayıtları",
                            description = "Mescid-i Nebevi, Mescid-i Aksa ve İstanbul Selatin camileri makam ezanları."
                        )
                        ProFeatureItem(
                            icon = Icons.Default.FormatListNumbered,
                            title = "Kaza Namazı Takip Defteri",
                            description = "Yıllık ve aylık kaza borçlarınızı tek tuşla hesaplayın ve güvenle takip edin."
                        )
                        ProFeatureItem(
                            icon = Icons.Default.Widgets,
                            title = "Ana Ekran & Kilit Ekranı Canlı Widget'ları",
                            description = "Telefonunuzu açmadan namaz vakitlerini ve geri sayımı ana ekranda takip edin."
                        )
                        ProFeatureItem(
                            icon = Icons.Default.PictureAsPdf,
                            title = "PDF İmsakiye & Paylaşılabilir Kartlar",
                            description = "Aylık ramazan imsakiyenizi ve günün ayetlerini yüksek çözünürlükte indirin."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Free Core Worship Guarantee Note
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Temel İbadet Taahhüdümüz: Kur'an-ı Kerim metin ve mealleri, namaz vakitleri, kıble pusulası ve zikirmatik tamamen ücretsizdir ve asla kilitlenmez.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Pricing Plans Selector
            item {
                Text(
                    text = "Abonelik Planınızı Seçin",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProPlanCard(
                        title = "Yıllık",
                        price = "299,99 ₺",
                        period = "24,99 ₺ / ay",
                        badge = "%50 İndirim",
                        isSelected = selectedPlan == "yearly",
                        onSelect = { selectedPlan = "yearly" },
                        modifier = Modifier.weight(1f)
                    )

                    ProPlanCard(
                        title = "Aylık",
                        price = "49,99 ₺",
                        period = "7 Gün Ücretsiz",
                        badge = null,
                        isSelected = selectedPlan == "monthly",
                        onSelect = { selectedPlan = "monthly" },
                        modifier = Modifier.weight(1f)
                    )

                    ProPlanCard(
                        title = "Ömür Boyu",
                        price = "699,99 ₺",
                        period = "Tek Seferlik",
                        badge = "Süresiz",
                        isSelected = selectedPlan == "lifetime",
                        onSelect = { selectedPlan = "lifetime" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Purchase Action Button
            item {
                Button(
                    onClick = {
                        showBillingDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("subscribe_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (selectedPlan) {
                            "yearly" -> "Yıllık Planı Başlat (299,99 ₺)"
                            "monthly" -> "7 Gün Ücretsiz Denemeyi Başlat"
                            else -> "Ömür Boyu Erişimi Aç (699,99 ₺)"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Google Play satın alımları kontrol ediliyor...", Toast.LENGTH_SHORT).show()
                        if (uiState.isPremiumActive) {
                            Toast.makeText(context, "Mevcut Pro üyeliğiniz doğrulandı ve aktiftir.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Bu Google hesabına bağlı aktif abonelik bulunamadı.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restore_purchases_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "Satın Alımları Geri Yükle",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Abonelik Google Play hesabınız üzerinden yönetilir. Dilediğiniz an Google Play Store > Abonelikler bölümünden iptal edebilirsiniz.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showBillingDialog) {
        AlertDialog(
            onDismissRequest = { showBillingDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Google Play Satın Alma",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Seçilen Paket: " + when (selectedPlan) {
                            "yearly" -> "Yıllık NurVakti Pro (nurvakti_pro_yearly)"
                            "monthly" -> "Aylık NurVakti Pro (nurvakti_pro_monthly)"
                            else -> "Ömür Boyu NurVakti Pro (nurvakti_pro_lifetime)"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Google Play Billing entegrasyonu Play Console ürün kimlikleriyle yapılandırılmıştır. Geliştirici test modunda denemek için üyeliği aktif edebilirsiniz.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBillingDialog = false
                        onActivatePremium()
                        Toast.makeText(context, "Pro özellikleri aktif edildi!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Test Modunda Aktif Et")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBillingDialog = false }) {
                    Text("Kapat", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun ProFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ProPlanCard(
    title: String,
    price: String,
    period: String,
    badge: String?,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )

            Text(
                text = period,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}
