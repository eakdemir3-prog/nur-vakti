package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KazaNamazScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onUpdateKaza: (type: String, delta: Int) -> Unit,
    onAddBulkKazaDays: (days: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showBulkAddDialog by remember { mutableStateOf(false) }

    val totalKazaPrayers = uiState.kazaSabah + uiState.kazaOgle + uiState.kazaIkindi +
            uiState.kazaAksam + uiState.kazaYatsi + uiState.kazaVitir
    val approxDays = (totalKazaPrayers / 6)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaza Namazı Defteri", fontWeight = FontWeight.Bold, color = GoldChampagne) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = GoldChampagne)
                    }
                },
                actions = {
                    IconButton(onClick = { showBulkAddDialog = true }) {
                        Icon(Icons.Default.AddBox, contentDescription = "Toplu Kaza Ekle", tint = GoldChampagne)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("kaza_namaz_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Summary Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Toplam Kaza Borcu",
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalKazaPrayers Vakit",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne
                            )
                        )
                        Text(
                            text = "Yaklaşık $approxDays günlük kaza namazı borcu bulunmaktadır.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Prayers list
            val kazaList = listOf(
                Triple("sabah", "Sabah Namazı (2 Rekat Farz)", uiState.kazaSabah),
                Triple("ogle", "Öğle Namazı (4 Rekat Farz)", uiState.kazaOgle),
                Triple("ikindi", "İkindi Namazı (4 Rekat Farz)", uiState.kazaIkindi),
                Triple("aksam", "Akşam Namazı (3 Rekat Farz)", uiState.kazaAksam),
                Triple("yatsi", "Yatsı Namazı (4 Rekat Farz)", uiState.kazaYatsi),
                Triple("vitir", "Vitir Vacip (3 Rekat)", uiState.kazaVitir)
            )

            items(kazaList.size) { index ->
                val (code, name, count) = kazaList[index]
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Kalan: $count vakit",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldChampagne)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // -1 Kılındı Button
                            FilledTonalButton(
                                onClick = { onUpdateKaza(code, -1) },
                                enabled = count > 0,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = GoldChampagne,
                                    contentColor = EmeraldDarkest
                                )
                            ) {
                                Text("-1 Kılındı", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // +1 Ekle Button
                            IconButton(
                                onClick = { onUpdateKaza(code, 1) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ekle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Quick Add Preset Card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Hızlı Kaza Ekle",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldChampagne)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "+1 Gün" to 1,
                                "+7 Gün (1 Hafta)" to 7,
                                "+30 Gün (1 Ay)" to 30,
                                "+365 Gün (1 Yıl)" to 365
                            ).forEach { (label, days) ->
                                OutlinedButton(
                                    onClick = { onAddBulkKazaDays(days) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldChampagne)
                                ) {
                                    Text(label, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBulkAddDialog) {
        AlertDialog(
            onDismissRequest = { showBulkAddDialog = false },
            title = { Text("Toplu Kaza Namazı Ekle") },
            text = {
                Text("Kaç günlük kaza namazı borcu eklemek istiyorsunuz? Her gün için 6 vakit (Sabah, Öğle, İkindi, Akşam, Yatsı, Vitir) eklenecektir.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddBulkKazaDays(30)
                        showBulkAddDialog = false
                    }
                ) {
                    Text("+1 Ay (30 Gün) Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkAddDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}
