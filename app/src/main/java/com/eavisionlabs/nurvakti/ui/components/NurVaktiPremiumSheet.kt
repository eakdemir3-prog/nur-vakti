package com.eavisionlabs.nurvakti.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurVaktiPremiumSheet(
    isPremiumActive: Boolean,
    onActivatePremium: () -> Unit,
    onOpenKazaTracker: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("yearly") } // "monthly", "yearly", "lifetime"
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = GoldChampagne) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Crown badge & Title
            item {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(GoldChampagne.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "NurVakti Premium",
                        tint = GoldChampagne,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "NurVakti Pro",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne,
                        letterSpacing = 0.5.sp
                    )
                )

                Text(
                    text = "Ayrıcalıklı, Derinlikli ve Huzurlu İbadet Deneyimi",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Feature Highlights
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PremiumFeatureRow(
                            icon = Icons.Default.Palette,
                            title = "Kâbe Örtüsü (Kiswa) & Medine Temaları",
                            subtitle = "Derin siyah-altın hat teması ve Medine Mushafı sayfa düzeni"
                        )
                        PremiumFeatureRow(
                            icon = Icons.Default.Repeat,
                            title = "Gelişmiş Ezberleme (Hıfz) Stüdyosu",
                            subtitle = "Ayet tekrar döngüleri (3x, 5x, 10x), gizle-göster ezber modu"
                        )
                        PremiumFeatureRow(
                            icon = Icons.Default.VolumeUp,
                            title = "Kutsal Beldeler Ezan Kayıtları",
                            subtitle = "Mescid-i Nebevi, Mescid-i Aksa ve İstanbul Selatin makam ezanları"
                        )
                        PremiumFeatureRow(
                            icon = Icons.Default.FormatListNumbered,
                            title = "Kaza Namazı Takip Defteri",
                            subtitle = "Tüm geçmiş kaza namazlarınızı tek dokunuşla kaydetme ve takip"
                        )
                        PremiumFeatureRow(
                            icon = Icons.Default.Widgets,
                            title = "Kilit Ekranı ve Canlı Widget'lar",
                            subtitle = "Ana ekranınızda şık namaz vakti geri sayımı ve zikir widget'ı"
                        )
                        PremiumFeatureRow(
                            icon = Icons.Default.Insights,
                            title = "Gelişmiş İbadet Analizleri",
                            subtitle = "Haftalık ve aylık Kur'an okuma grafikleri, PDF imsakiye paylaşımı"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Pricing Plans Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Yearly Plan (Best Value)
                    PlanCard(
                        title = "Yıllık",
                        price = "299,99 ₺",
                        subtext = "24,99 ₺ / ay",
                        badge = "%50 İndirim",
                        isSelected = selectedPlan == "yearly",
                        onSelect = { selectedPlan = "yearly" },
                        modifier = Modifier.weight(1f)
                    )

                    // Monthly Plan
                    PlanCard(
                        title = "Aylık",
                        price = "49,99 ₺",
                        subtext = "7 Gün Ücretsiz",
                        badge = null,
                        isSelected = selectedPlan == "monthly",
                        onSelect = { selectedPlan = "monthly" },
                        modifier = Modifier.weight(1f)
                    )

                    // Lifetime Plan
                    PlanCard(
                        title = "Ömür Boyu",
                        price = "499,99 ₺",
                        subtext = "Tek Seferlik",
                        badge = "Kalıcı",
                        isSelected = selectedPlan == "lifetime",
                        onSelect = { selectedPlan = "lifetime" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Primary Call to Action Button
            item {
                if (isPremiumActive) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = GoldChampagne.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldChampagne)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NurVakti Pro Üyeliğiniz Aktif",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldChampagne
                                )
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onActivatePremium()
                            showSuccessSnackbar = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("premium_cta_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = EmeraldDarkest
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (selectedPlan == "monthly") "7 Gün Ücretsiz Deneyin" else "Hemen Pro'ya Geçin",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Shortcut to Kaza Tracker
                TextButton(
                    onClick = {
                        onDismiss()
                        onOpenKazaTracker()
                    }
                ) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kaza Namazı Takip Defterine Git", color = GoldChampagne)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ethical Assurance Note
                Text(
                    text = "Manevi Güvence: Kur'an-ı Kerim metni, temel namaz vakitleri, ezan bildirimleri ve kıble pusulası tüm müminler için her zaman ücretsiz ve reklamsız kalacaktır.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PremiumFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldChampagne,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    subtext: String,
    badge: String?,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GoldChampagne else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldChampagne
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldDarkest,
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldChampagne
                ),
                maxLines = 1
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}
