package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*

@Composable
fun ExploreScreen(
    uiState: UiState,
    onNavigate: (AppScreen) -> Unit,
    onOpenPremium: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val allTools = listOf(
        ExploreToolItem(
            id = "qibla",
            title = "Kıble Bulucu (Pusula)",
            subtitle = "Gerçek manyetik sensör ile Kâbe yönü ve derece göstergesi",
            icon = Icons.Default.Explore,
            accentColor = GoldChampagne,
            screen = AppScreen.QIBLA
        ),
        ExploreToolItem(
            id = "tasbih",
            title = "Dijital Tesbih & Zikirmatik",
            subtitle = "Dokunsal sayaç, hazır dualı zikirler, hedef takibi ve titreşim",
            icon = Icons.Default.Fingerprint,
            accentColor = Color(0xFF66BB6A),
            screen = AppScreen.TASBIH
        ),
        ExploreToolItem(
            id = "kaza",
            title = "Kaza Namazı Defteri",
            subtitle = "Tüm geçmiş namaz borçlarınızı hesaplayın ve tek tıkla kaydedin",
            icon = Icons.Default.FormatListNumbered,
            accentColor = Color(0xFFFF7043),
            screen = AppScreen.KAZA_NAMAZ
        ),
        ExploreToolItem(
            id = "duas",
            title = "Mübarek Dualar Kütüphanesi",
            subtitle = "Sabah, akşam, şifa ve Kur'an'da geçen 10 kategoride dualar",
            icon = Icons.Default.AutoAwesome,
            accentColor = Color(0xFFFFA726),
            screen = AppScreen.DUAS
        ),
        ExploreToolItem(
            id = "asma",
            title = "Esmaü'l-Hüsna (99 İsim)",
            subtitle = "Allah'ın yüce isimleri, tefsirleri ve manevi faziletleri",
            icon = Icons.Default.Star,
            accentColor = Color(0xFFAB47BC),
            screen = AppScreen.ASMAUL_HUSNA
        ),
        ExploreToolItem(
            id = "ramazan",
            title = "Ramazan & Hatim Takibi",
            subtitle = "İftar/İmsak geri sayımı ve 30 cüz etkileşimli hatim takibi",
            icon = Icons.Default.NightlightRound,
            accentColor = GoldChampagne,
            screen = AppScreen.RAMAZAN_HATIM
        ),
        ExploreToolItem(
            id = "zakat",
            title = "Zekât ve Fitre Hesaplayıcı",
            subtitle = "80.18g altın nisap eşiği ve %2.5 zekât oranı hesaplama",
            icon = Icons.Default.Calculate,
            accentColor = Color(0xFF26A69A),
            screen = AppScreen.ZAKAT_CALCULATOR
        ),
        ExploreToolItem(
            id = "quran",
            title = "Kur'an-ı Kerim & Sesli Tilavet",
            subtitle = "114 Sure, Uthmani hattı, Diyanet meali ve kâri kayıtları",
            icon = Icons.Default.MenuBook,
            accentColor = Color(0xFF42A5F5),
            screen = AppScreen.QURAN
        ),
        ExploreToolItem(
            id = "calendar",
            title = "Aylık Vakitler & İmsakiye",
            subtitle = "30 günlük tam namaz takvimi ve Hicri tarihler",
            icon = Icons.Default.CalendarMonth,
            accentColor = Color(0xFF78909C),
            screen = AppScreen.PRAYER_TIMES
        )
    )

    val filteredTools = remember(searchQuery) {
        if (searchQuery.isBlank()) allTools
        else allTools.filter {
            it.title.lowercase().contains(searchQuery.lowercase()) ||
            it.subtitle.lowercase().contains(searchQuery.lowercase())
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            Text(
                text = "İslami İbadet & Yaşam Araçları",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Günlük dini vazifelerinizi kolaylaştıran özellikler",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Pro Promo Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenPremium() },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldChampagne.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Pro",
                                tint = GoldChampagne,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NurVakti Pro Ayrıcalıkları",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldChampagne
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = if (uiState.isPremiumActive) "Üyeliğiniz aktif • Tüm özellikler açık" else "Kâbe Kiswa teması & hatim stüdyosuna geçin",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Aç",
                        tint = GoldChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                placeholder = {
                    Text(
                        "Özellik ara (Örn: kıble, zekat, tesbih, dua)...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Ara", tint = GoldChampagne)
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldChampagne,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tools List
        items(filteredTools, key = { it.id }) { tool ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigate(tool.screen) }
                    .testTag("explore_tool_${tool.id}"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(tool.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.title,
                            tint = tool.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tool.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tool.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = GoldChampagne.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Upcoming Religious Days Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Yaklaşan Mübarek Gün ve Geceler",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(uiState.religiousDays.take(4), key = { it.id }) { day ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = day.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${day.gregorianDate} • ${day.hijriDate}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldChampagne.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (day.daysRemaining == 0) "Bugün" else "${day.daysRemaining} Gün Kaldı",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private data class ExploreToolItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val screen: AppScreen
)
