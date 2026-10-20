package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamazanHatimScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onToggleJuz: (Int) -> Unit,
    onNavigateZakat: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val schedule = uiState.prayerSchedule
    val imsakTime = schedule?.prayerTimes?.find { it.type.name == "IMSAK" }?.timeFormatted ?: "--:--"
    val iftarTime = schedule?.prayerTimes?.find { it.type.name == "AKSAM" }?.timeFormatted ?: "--:--"

    val completedCount = uiState.completedJuzs.size
    val progress = (completedCount.toFloat() / 30f).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ramazan & Hatim Takibi", fontWeight = FontWeight.Bold, color = GoldLight) },
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
                                text = "Günün İftar ve İmsak Vakitleri",
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
                                Text("Sahur / İmsak", style = MaterialTheme.typography.labelMedium.copy(color = SoftGrey))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(imsakTime, style = MaterialTheme.typography.headlineMedium.copy(color = CreamWhite, fontWeight = FontWeight.Bold))
                            }

                            VerticalDivider(modifier = Modifier.height(44.dp), color = EmeraldBorder)

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("İftar (Akşam)", style = MaterialTheme.typography.labelMedium.copy(color = SoftGrey))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(iftarTime, style = MaterialTheme.typography.headlineMedium.copy(color = GoldChampagne, fontWeight = FontWeight.Bold))
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
}
