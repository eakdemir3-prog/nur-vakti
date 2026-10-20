package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.UiState
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    uiState: UiState,
    onNavigate: (AppScreen) -> Unit,
    onOpenCityPicker: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenPremium: () -> Unit = {},
    onAutoDetectLocation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSourcesDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // 1. User Header & Spiritual Identity
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldChampagne.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Mümin Kul",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Konum: ${uiState.selectedCity.name}, ${uiState.selectedCity.country}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Cihaz İçi Güvenli Kayıt • Üyelik Zorunlu Değildir",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. NurVakti Pro Card (Premier Pro Satın Alma / Üyelik Ekranı)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenPremium() }
                    .testTag("pro_purchase_card"),
                shape = RoundedCornerShape(20.dp),
                color = if (uiState.isPremiumActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Pro",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NurVakti Pro",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = if (uiState.isPremiumActive) "PRO AKTİF" else "AYRICALIKLAR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (uiState.isPremiumActive)
                                    "Tüm temalar, ezber stüdyosu ve widget'lar aktif"
                                else
                                    "Kâbe Kiswa teması, hatim stüdyosu & widget'ları açın",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onOpenPremium,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (uiState.isPremiumActive) "Yönet" else "Yükselt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Spiritual Stats Grid (Namaz, Zikir, Kur'an, Hatim)
        item {
            Text(
                text = "İbadet ve İlerleme Takibiniz",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Namaz",
                    value = "${uiState.completedPrayersToday.size} / 5",
                    subtitle = "Bugün Kılınan",
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Zikir",
                    value = "${uiState.todayDhikrCompletedTotal}",
                    subtitle = "Bugünkü Toplam",
                    icon = Icons.Default.Fingerprint,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Hatim",
                    value = "${uiState.completedJuzs.size} / 30",
                    subtitle = "Okunan Cüz",
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Favoriler",
                    value = "${uiState.favoriteSurahs.size + uiState.favoriteDuas.size}",
                    subtitle = "Sure & Dua",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Settings Items
        item {
            Text(
                text = "Uygulama ve Tercihler",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Auto GPS Location
            SettingsRow(
                title = "Konumumu Otomatik Bul (GPS)",
                subtitle = if (uiState.isLocating) "GPS konumu aranıyor..." else "Mevcut: ${uiState.selectedCity.name}",
                icon = Icons.Default.MyLocation,
                onClick = onAutoDetectLocation
            )

            // City Selection
            SettingsRow(
                title = "Şehir ve Konum Listesi",
                subtitle = "${uiState.selectedCity.name} (${uiState.selectedCity.country})",
                icon = Icons.Default.LocationOn,
                onClick = onOpenCityPicker
            )

            // Theme Toggle
            SettingsRow(
                title = "Görünüm Teması",
                subtitle = if (uiState.isDarkMode) "Gece Zümrüdü (Karanlık)" else "Fildişi Taşı (Aydınlık)",
                icon = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                onClick = onToggleTheme
            )

            // Kaza Namazı Tracker shortcut
            SettingsRow(
                title = "Kaza Namazı Defteri",
                subtitle = "Geçmiş namaz borçlarınızı hesaplayın ve kaydedin",
                icon = Icons.Default.FormatListNumbered,
                onClick = { onNavigate(AppScreen.KAZA_NAMAZ) }
            )

            // Quran Settings shortcut
            SettingsRow(
                title = "Kur'an Okuma Ayarları",
                subtitle = "Yazı boyutları: Arapça ${uiState.arabicFontSize}sp, Meal ${uiState.mealFontSize}sp",
                icon = Icons.Default.FormatSize,
                onClick = { onNavigate(AppScreen.QURAN) }
            )

            // Verified Sources & Licenses
            SettingsRow(
                title = "Dini Kaynaklar ve Lisanslar",
                subtitle = "Diyanet İşleri Başkanlığı, Tanzil, Aladhan ve EveryAyah",
                icon = Icons.Default.Verified,
                onClick = { showSourcesDialog = true }
            )

            // Privacy & Data
            SettingsRow(
                title = "Gizlilik ve Güvenlik",
                subtitle = "Verileriniz yalnızca telefonunuzda saklanır",
                icon = Icons.Default.Security,
                onClick = { showPrivacyDialog = true }
            )

            // Version info
            SettingsRow(
                title = "NurVakti Hakkında",
                subtitle = "Sürüm 1.0.0 (Google Play Yayınına Hazır)",
                icon = Icons.Default.Info,
                onClick = {
                    Toast.makeText(context, "NurVakti: Premium Kur'an-ı Kerim ve İslami Yaşam", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    if (showSourcesDialog) {
        AlertDialog(
            onDismissRequest = { showSourcesDialog = false },
            title = {
                Text(
                    "Doğrulanmış Dini Kaynaklar",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "1. Kur'an-ı Kerim Metni: Tanzil.net projesinin Uthmani Mushaf standartlarına uygun, hareke doğrulaması yapılmış resmi metni.\n\n" +
                               "2. Türkçe Mealler: Diyanet İşleri Başkanlığı Kur'an Meali.\n\n" +
                               "3. Namaz Vakitleri & Kıble: Astronomik güneş declination formülü, Diyanet İşleri Başkanlığı ve Muslim World League hesaplama parametreleri (Aladhan API uyumlu).\n\n" +
                               "4. Sesli Tilavetler: EveryAyah ve Mp3Quran lisanslı yüksek kaliteli kâri ses kayıtları.\n\n" +
                               "5. Dualar & Esma: Kütüb-i Sitte hadis kaynakları ve Kur'an-ı Kerim ayetleri.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourcesDialog = false }) {
                    Text("Anladım", color = GoldChampagne)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        val policyUrl = "https://eakdemir3-prog.github.io/fitnex-legal/nurvakti/gizlilik.html"
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoldChampagne
                    )
                    Text(
                        "Gizlilik Politikası",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NurVakti (" + "com.aistudio.nurvakti.app" + ") kullanıcı mahremiyetini ve ibadet gizliliğini temel ilke kabul eder:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = GoldChampagne
                    )
                    Text(
                        text = "1. Konum Bilgisi: Yalnızca namaz vakitlerinin ve kıble açısının cihaz üzerinde yerel hesaplanması için kullanılır. Hiçbir sunucuya gönderilmez.\n\n" +
                               "2. Sensörler: Pusula verisi ekranda kıbleyi göstermek için anlık okunur, asla kaydedilmez.\n\n" +
                               "3. Cihaz İçi Depolama: Hatim, zikir, kaza ve favori kayıtlarınız sadece telefonunuzda saklanır.\n\n" +
                               "4. Reklamsız & Güvenli: Kullanıcıyı takip eden ticari kodlar veya üçüncü taraf reklam modülleri içermez.\n\n" +
                               "5. Geliştirici & Destek: eakdemir3@gmail.com\n" +
                               "GitHub: github.com/eakdemir3-prog",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Tarayıcı açılamadı: $policyUrl", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Web'de Aç (GitHub)", color = GoldChampagne)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = ClipData.newPlainText("NurVakti Gizlilik Politikası", policyUrl)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, "Gizlilik politikası bağlantısı kopyalandı", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Linki Kopyala", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(18.dp)),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
