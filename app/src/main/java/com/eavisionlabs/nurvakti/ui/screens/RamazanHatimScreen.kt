package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamazanHatimScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onToggleJuz: (Int) -> Unit,
    onNavigateZakat: () -> Unit,
    onOpenJuz: (Int) -> Unit = {},
    onShare: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var showImsakiye by remember { mutableStateOf(false) }
    val ramadan = uiState.ramadan

    val schedule = uiState.prayerSchedule
    val imsakTime = schedule?.prayerTimes?.find { it.type.name == "IMSAK" }?.timeFormatted ?: "--:--"
    val iftarTime = schedule?.prayerTimes?.find { it.type.name == "AKSAM" }?.timeFormatted ?: "--:--"

    val completedCount = uiState.completedJuzs.size
    val progress = (completedCount.toFloat() / 30f).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Ramazan & Hatim Takibi"), fontWeight = FontWeight.Bold, color = GoldLight) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = GoldChampagne)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDarkest)
            )
        },
        containerColor = EmeraldDarkest,
        modifier = modifier.testTag("ramazan_hatim_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Sahur & Iftar Times Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = t("Bugünün İmsak ve İftar Vakitleri"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                            Icon(Icons.Default.NightlightRound, contentDescription = null, tint = GoldChampagne)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(t("Sahur / İmsak"), style = MaterialTheme.typography.labelMedium.copy(color = SoftGrey))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(imsakTime, style = MaterialTheme.typography.headlineMedium.copy(color = CreamWhite, fontWeight = FontWeight.Bold))
                            }

                            VerticalDivider(modifier = Modifier.height(44.dp), color = EmeraldBorder)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(t("İftar (Akşam)"), style = MaterialTheme.typography.labelMedium.copy(color = SoftGrey))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(iftarTime, style = MaterialTheme.typography.headlineMedium.copy(color = GoldChampagne, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // 1b. Ramazan durumu + iftar/sahur geri sayımı + imsakiye
            item {
                val times = schedule?.prayerTimes
                val now = System.currentTimeMillis()
                val imsakMs = times?.firstOrNull { it.type.name == "IMSAK" }?.timestampMillis ?: 0L
                val iftarMs = times?.firstOrNull { it.type.name == "AKSAM" }?.timestampMillis ?: 0L
                @Suppress("UNUSED_VARIABLE") val tick = schedule?.remainingMillisToNext
                val (label, target) = when {
                    now < imsakMs -> t("Sahura (imsak) kalan") to imsakMs
                    now < iftarMs -> t("İftara kalan") to iftarMs
                    else -> t("Yarın sahura kalan") to (imsakMs + 24 * 3600_000L)
                }
                val remaining = (target - now).coerceAtLeast(0)
                val hh = remaining / 3600_000; val mm = (remaining / 60_000) % 60; val ss = (remaining / 1000) % 60
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f))
                ) {
                    Column(Modifier.padding(18.dp)) {
                        if (ramadan.isRamadan) {
                            Text(String.format(t("Ramazan'ın %d. günü"), ramadan.dayOfRamadan), color = GoldLight, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(label, color = SoftGrey, style = MaterialTheme.typography.labelMedium)
                            Text(
                                String.format(java.util.Locale.ROOT, "%02d:%02d:%02d", hh, mm, ss),
                                color = GoldChampagne, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.displaySmall
                            )
                        } else if (ramadan.daysUntilRamadan > 0) {
                            Text(t("Ramazan-ı Şerif'e"), color = SoftGrey, style = MaterialTheme.typography.labelMedium)
                            Text(String.format(t("%d gün kaldı"), ramadan.daysUntilRamadan), color = GoldChampagne, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                            Text(String.format(t("İlk oruç: %s"), ramadan.startDateLabel), color = CreamWhite, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (ramadan.imsakiye.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { showImsakiye = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(String.format(t("%s İmsakiyesi"), uiState.selectedCity.name), fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. 30 Juz Hatim Progress Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "30 Cüz Hatim Takibi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CreamWhite
                                    )
                                )
                                Text(
                                    text = "$completedCount / 30 Cüz Tamamlandı (%${(progress * 100).toInt()})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(GoldChampagne.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "%${(progress * 100).toInt()}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldChampagne
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldChampagne,
                            trackColor = EmeraldCardElevated
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Okuduğunuz cüzlere dokunarak işaretleyin:",
                            style = MaterialTheme.typography.labelSmall.copy(color = SoftGrey)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 30 Juz Grid (6 columns x 5 rows)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (row in 0 until 5) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (col in 0 until 6) {
                                        val juzNum = row * 6 + col + 1
                                        val isDone = uiState.completedJuzs.contains(juzNum)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isDone) GoldChampagne else EmeraldCardElevated)
                                                .clickable { onToggleJuz(juzNum) }
                                                .testTag("juz_item_$juzNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$juzNum",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDone) EmeraldDarkest else CreamWhite
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Quick Action to Zakat Calculator
            item {
                Surface(
                    onClick = onNavigateZakat,
                    shape = RoundedCornerShape(18.dp),
                    color = EmeraldCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldChampagne.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldChampagne)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Zekât ve Fitre Hesaplayıcı",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CreamWhite
                                    )
                                )
                                Text(
                                    text = "Nisap miktarı ve %2.5 zekât oranı hesaplama",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SoftGrey)
                                )
                            }
                        }

                        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showImsakiye) {
        ModalBottomSheet(onDismissRequest = { showImsakiye = false }, containerColor = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        String.format(t("%s İmsakiyesi"), uiState.selectedCity.displayName),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoldChampagne),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (!uiState.isPremiumActive) { onShare(t("NurVakti ile Ramazan imsakiyesi") + ": " + uiState.selectedCity.displayName); return@IconButton }
                        onShare(
                            String.format(t("%s Ramazan İmsakiyesi"), uiState.selectedCity.displayName) + "\n" +
                                t("Gün | Tarih | İmsak | İftar") + "\n" +
                                ramadan.imsakiye.joinToString("\n") { "${it.dayNumber} | ${it.gregorianDate} | ${it.imsak} | ${it.aksam}" } +
                                "\n\n— NurVakti"
                        )
                    }) { Icon(Icons.Default.Share, contentDescription = t("Paylaş"), tint = GoldChampagne) }
                }
                if (!uiState.isPremiumActive) {
                    Text(t("İmsakiyenin tamamını metin olarak paylaşmak Pro özelliğidir."), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    listOf(t("Gün"), t("Tarih"), t("İmsak"), t("Güneş"), t("İftar"), t("Yatsı")).forEachIndexed { i, h ->
                        Text(h, modifier = Modifier.weight(if (i == 1) 1.6f else 1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldChampagne)
                    }
                }
                HorizontalDivider()
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                    items(ramadan.imsakiye) { d ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text("${d.dayNumber}", Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(d.gregorianDate.take(5), Modifier.weight(1.6f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(d.imsak, Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(d.gunes, Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(d.aksam, Modifier.weight(1f), fontSize = 12.sp, color = GoldChampagne, fontWeight = FontWeight.SemiBold)
                            Text(d.yatsi, Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
