package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.*
import com.eavisionlabs.nurvakti.ui.theme.*

@Composable
fun PrayerTrackerScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onTogglePrayerDone: (String) -> Unit,
    onOpenPremium: () -> Unit
) {
    val fardhs = listOf("imsak" to t("Sabah"), "ogle" to t("Öğle"), "ikindi" to t("İkindi"), "aksam" to t("Akşam"), "yatsi" to t("Yatsı"))
    val doneToday = fardhs.count { uiState.completedPrayersToday.contains(it.first) }

    SubScreenScaffold(title = t("Namaz Takibi"), onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 40.dp)) {
            item {
                Spacer(Modifier.height(8.dp))
                NurCard {
                    Text(t("Bugün kıldığım namazlar"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        String.format(t("%d / 5 vakit"), doneToday),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        fardhs.forEach { (code, title) ->
                            val done = uiState.completedPrayersToday.contains(code)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(if (done) GoldChampagne else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, GoldChampagne.copy(alpha = 0.6f), CircleShape)
                                        .clickable { onTogglePrayerDone(code) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (done) Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldDarkest)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatPill(uiState.prayerStreak.toString(), t("gün kesintisiz"), Modifier.weight(1f))
                    StatPill(uiState.prayerWeek.sumOf { it.value }.toString(), t("bu hafta (35)"), Modifier.weight(1f))
                    StatPill(uiState.prayerTotal30.toString(), t("son 30 gün (150)"), Modifier.weight(1f))
                }
            }

            item { SectionTitle(t("Son 7 gün")) }
            item {
                NurCard { BarChart(data = uiState.prayerWeek, maxValue = 5) }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle(t("Son 30 gün"))
                    if (!uiState.isPremiumActive) ProBadge()
                }
            }
            item {
                NurCard {
                    if (uiState.isPremiumActive) {
                        BarChart(data = uiState.prayerMonth, maxValue = 5, height = 110)
                        Spacer(Modifier.height(6.dp))
                        val fullDays = uiState.prayerMonth.count { it.value >= 5 }
                        Text(
                            String.format(t("30 günün %d gününde beş vaktin tamamı kılındı."), fullDays),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldChampagne)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                t("Aylık grafik ve ayrıntılı analizler NurVakti Pro ile açılır."),
                                modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onOpenPremium, shape = RoundedCornerShape(12.dp)) { Text(t("Pro'yu incele")) }
                    }
                }
            }

            item { SectionTitle(t("Kur'an okuma (sayfa)")) }
            item {
                NurCard {
                    BarChart(data = uiState.readingWeek, maxValue = uiState.dailyPageGoal)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        String.format(t("Günlük hedef: %d sayfa • Hatim ilerlemesi: %d/604"), uiState.dailyPageGoal, uiState.hatimPagesRead),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item { SectionTitle(t("Zikir (son 7 gün)")) }
            item { NurCard { BarChart(data = uiState.dhikrWeek, maxValue = 100) } }
        }
    }
}
