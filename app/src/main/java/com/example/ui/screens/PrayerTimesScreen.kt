package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthlyDaySchedule
import com.example.model.PrayerType
import com.example.ui.UiState
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    uiState: UiState,
    onOpenCityPicker: () -> Unit,
    onOpenMonthlySchedule: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = uiState.prayerSchedule
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_times_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // 1. Header with City & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Namaz Vakitleri",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${schedule?.gregorianDateFormatted ?: ""} • ${schedule?.hijriDateFormatted ?: ""}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    onClick = onOpenCityPicker,
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.selectedCity.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Grand Countdown Card
        item {
            val next = schedule?.nextPrayer
            val remainingMs = schedule?.remainingMillisToNext ?: 0L
            val totalSecs = (remainingMs / 1000).coerceAtLeast(0)
            val hours = totalSecs / 3600
            val minutes = (totalSecs % 3600) / 60
            val seconds = totalSecs % 60
            val countdownStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sıradaki Vakit: ${next?.type?.titleTr ?: "İmsak"}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = countdownStr,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vakit Saati: ${next?.timeFormatted ?: "--:--"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. 6 Prayer Times Detailed List
        val prayers = schedule?.prayerTimes ?: emptyList()
        items(prayers) { prayer ->
            val isNext = prayer.isNext
            val isCurrent = prayer.isCurrent
            val isPassed = prayer.isPassed

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = when {
                    isNext -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    isCurrent -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isNext) 1.5.dp else 1.dp,
                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isNext -> MaterialTheme.colorScheme.primary
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (prayer.type) {
                                    PrayerType.IMSAK -> Icons.Default.NightsStay
                                    PrayerType.GUNES -> Icons.Default.WbSunny
                                    PrayerType.OGLE -> Icons.Default.LightMode
                                    PrayerType.IKINDI -> Icons.Default.WbTwilight
                                    PrayerType.AKSAM -> Icons.Default.Bedtime
                                    PrayerType.YATSI -> Icons.Default.DarkMode
                                },
                                contentDescription = null,
                                tint = if (isNext || isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prayer.type.titleTr,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1
                                )
                                if (!prayer.type.isFardh) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(Doğuş)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                            Text(
                                text = prayer.type.titleAr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = prayer.timeFormatted,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isNext) MaterialTheme.colorScheme.primary else if (isPassed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { showNotificationSettingsDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Ezan Bildirimi",
                                tint = if (isNext) GoldChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Action Buttons (Aylık İmsakiye & Ezan Ayarları)
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onOpenMonthlySchedule(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldChampagne,
                        contentColor = EmeraldDarkest
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aylık Takvim (İmsakiye)", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showNotificationSettingsDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldChampagne
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Ezan Sesi",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hesaplama Yöntemi: Diyanet İşleri Başkanlığı ve Müslüman Âlemi Birliği (MWL) astronomik parametreleri esas alınmıştır. Konumunuza göre Güneş açısı hesaplanır.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }

    // Monthly Schedule Sheet
    if (uiState.isMonthlyScheduleVisible) {
        ModalBottomSheet(
            onDismissRequest = { onOpenMonthlySchedule(false) },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aylık Vakitler (${uiState.selectedCity.name})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(onClick = { onOpenMonthlySchedule(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tarih", modifier = Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("İmsak", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("Güneş", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("Öğle", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("İkindi", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("Akşam", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text("Yatsı", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(uiState.monthlySchedule, key = { it.dayNumber }) { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(day.gregorianDate.take(5), modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(day.imsak, modifier = Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(day.gunes, modifier = Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(day.ogle, modifier = Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(day.ikindi, modifier = Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(day.aksam, modifier = Modifier.weight(1f), fontSize = 11.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                            Text(day.yatsi, modifier = Modifier.weight(1f), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }

    // Adhan & Notification Settings Dialog
    if (showNotificationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSettingsDialog = false },
            title = { Text("Ezan & Bildirim Ayarları", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    Text(
                        text = "Vakit girdiğinde bildirim ve ezan tercihlerinizi belirleyin:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        "Ezan Sesi Çal (Mekke Ezanı)" to true,
                        "15 Dakika Önce Hatırlat" to true,
                        "Vakit Girişinde Titreşim" to true,
                        "Sessiz Bildirim Göster" to false
                    ).forEach { (prefTitle, defaultChecked) ->
                        var isChecked by remember { mutableStateOf(defaultChecked) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prefTitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Switch(checked = isChecked, onCheckedChange = { isChecked = it })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldChampagne,
                        contentColor = EmeraldDarkest
                    )
                ) {
                    Text("Kaydet")
                }
            }
        )
    }
}
