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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.model.MonthlyDaySchedule
import com.eavisionlabs.nurvakti.model.PrayerType
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    uiState: UiState,
    onOpenCityPicker: () -> Unit,
    onOpenMonthlySchedule: (Boolean) -> Unit,
    onOpenSettings: () -> Unit = {},
    onSetNotificationPref: (PrayerType, NotificationPreference) -> Unit = { _, _ -> },
    onShare: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val schedule = uiState.prayerSchedule
    var notifPrayer by remember { mutableStateOf<PrayerType?>(null) }

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
                        text = t("Namaz Vakitleri"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${schedule?.gregorianDateFormatted ?: ""}\n${schedule?.hijriDateFormatted ?: ""}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
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
                                    text = t(prayer.type.titleTr),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1
                                )
                                if (!prayer.type.isFardh) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = t("(Doğuş)"),
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

                        val pref = uiState.notificationPrefs[prayer.type] ?: NotificationPreference.DISABLED
                        IconButton(
                            onClick = { notifPrayer = prayer.type },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = when (pref) {
                                    NotificationPreference.DISABLED -> Icons.Default.NotificationsOff
                                    NotificationPreference.AZAN -> Icons.Default.Campaign
                                    NotificationPreference.SILENT -> Icons.Default.NotificationsNone
                                    NotificationPreference.VIBRATE_ONLY -> Icons.Default.Vibration
                                    else -> Icons.Default.NotificationsActive
                                },
                                contentDescription = t("Bildirim ayarı"),
                                tint = if (pref == NotificationPreference.DISABLED) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else GoldChampagne,
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
                    Text(t("Aylık Takvim"), fontWeight = FontWeight.Bold, maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        val s0 = uiState.prayerSchedule
                        if (s0 != null) onShare(
                            "${s0.cityName} • ${s0.gregorianDateFormatted}\n${s0.hijriDateFormatted}\n\n" +
                                s0.prayerTimes.joinToString("\n") { "${t(it.type.titleTr)}: ${it.timeFormatted}" } + "\n\n— NurVakti"
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldChampagne)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = t("Paylaş"), modifier = Modifier.size(18.dp))
                }

                OutlinedButton(
                    onClick = onOpenSettings,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldChampagne
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = t("Ezan Sesi"),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = t("Hesaplama: Diyanet İşleri Başkanlığı yöntemi (İmsak 18°, Yatsı 17°, asr-ı evvel, temkinler dahil). Ayarlar'dan dakika düzeltmesi yapabilirsiniz."),
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
                        text = "Aylık Vakitler (${uiState.selectedCity.displayName})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(onClick = { onOpenMonthlySchedule(false) }) {
                        Icon(Icons.Default.Close, contentDescription = t("Kapat"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text(t("Tarih"), modifier = Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("İmsak"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("Güneş"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("Öğle"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("İkindi"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("Akşam"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                    Text(t("Yatsı"), modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
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

    notifPrayer?.let { type ->
        AlertDialog(
            onDismissRequest = { notifPrayer = null },
            title = { Text(String.format(t("%s bildirimi"), t(type.titleTr))) },
            text = {
                Column {
                    NotificationPreference.entries.forEach { p ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.notificationPrefs[type] == p,
                                onClick = { onSetNotificationPref(type, p); notifPrayer = null }
                            )
                            Text(notificationPrefLabel(p), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { notifPrayer = null; onOpenSettings() }) { Text(t("Tüm ayarlar")) } },
            dismissButton = { TextButton(onClick = { notifPrayer = null }) { Text(t("Kapat")) } }
        )
    }
}
