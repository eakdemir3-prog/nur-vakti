package com.eavisionlabs.nurvakti.ui.screens

import com.eavisionlabs.nurvakti.i18n.t
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
    onSetKazaPerDay: (Int) -> Unit = {},
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
                title = { Text(t("Kaza Namazı Defteri"), fontWeight = FontWeight.Bold, color = GoldChampagne) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("Geri"), tint = GoldChampagne)
                    }
                },
                actions = {
                    IconButton(onClick = { showBulkAddDialog = true }) {
                        Icon(Icons.Default.AddBox, contentDescription = t("Toplu Kaza Ekle"), tint = GoldChampagne)
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
                            text = t("Toplam Kaza Borcu"),
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
                                Text(t("-1 Kılındı"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // +1 Ekle Button
                            IconButton(
                                onClick = { onUpdateKaza(code, 1) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = t("Ekle"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Akıllı plan
            item {
                Spacer(modifier = Modifier.height(16.dp))
                KazaPlanCard(uiState, onSetKazaPerDay)
            }
            // Hesaplayıcı
            item {
                Spacer(modifier = Modifier.height(16.dp))
                KazaCalculatorCard(onAdd = onAddBulkKazaDays)
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
                            text = t("Hızlı Kaza Ekle"),
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
            title = { Text(t("Toplu Kaza Namazı Ekle")) },
            text = {
                Text(t("Kaç günlük kaza namazı borcu eklemek istiyorsunuz? Her gün için 6 vakit (Sabah, Öğle, İkindi, Akşam, Yatsı, Vitir) eklenecektir."))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddBulkKazaDays(30)
                        showBulkAddDialog = false
                    }
                ) {
                    Text(t("+1 Ay (30 Gün) Ekle"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkAddDialog = false }) {
                    Text(t("Vazgeç"))
                }
            }
        )
    }
}


@Composable
private fun KazaPlanCard(uiState: UiState, onSetKazaPerDay: (Int) -> Unit) {
    val maxRemaining = listOf(uiState.kazaSabah, uiState.kazaOgle, uiState.kazaIkindi, uiState.kazaAksam, uiState.kazaYatsi, uiState.kazaVitir).maxOrNull() ?: 0
    val perDay = uiState.kazaPerDay
    val days = if (maxRemaining == 0) 0 else (maxRemaining + perDay - 1) / perDay
    val finish = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_MONTH, days) }
    val fmt = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.forLanguageTag(if (com.eavisionlabs.nurvakti.i18n.I18n.language == "tr") "tr" else "en"))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(t("Tamamlama Planı"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldChampagne))
            }
            Spacer(Modifier.height(8.dp))
            Text(t("Günde her vakitten kaç kaza kılacaksınız?"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            com.eavisionlabs.nurvakti.ui.components.ChoiceChips(
                options = listOf(1 to "+1", 2 to "+2", 3 to "+3", 5 to "+5"),
                selected = perDay,
                onSelect = onSetKazaPerDay
            )
            Spacer(Modifier.height(8.dp))
            if (maxRemaining == 0) {
                Text(t("Kayıtlı kaza borcunuz yok. Allah kabul etsin."), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    String.format(t("Günde +%1\$d ile yaklaşık %2\$d günde (%3\$s) tamamlanır."), perDay, days, fmt.format(finish.time)),
                    color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold
                )
                if (days > 365) {
                    Text(
                        String.format(t("Bu, yaklaşık %.1f yıl eder."), days / 365.0),
                        color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun KazaCalculatorCard(onAdd: (Int) -> Unit) {
    var pubertyAge by remember { mutableStateOf("12") }
    var startedAge by remember { mutableStateOf("") }
    var percent by remember { mutableStateOf(100f) }
    var confirm by remember { mutableStateOf(false) }
    val p = pubertyAge.toIntOrNull()
    val st = startedAge.toIntOrNull()
    val years = if (p != null && st != null && st > p) st - p else 0
    val days = (years * 365 * (percent / 100f)).toInt()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(t("Kaza Borcu Hesaplayıcı"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldChampagne))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = pubertyAge,
                    onValueChange = { v -> if (v.length <= 2 && v.all { it.isDigit() }) pubertyAge = v },
                    label = { Text(t("Ergenlik yaşı")) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = startedAge,
                    onValueChange = { v -> if (v.length <= 2 && v.all { it.isDigit() }) startedAge = v },
                    label = { Text(t("Düzenli kılmaya başladığım yaş")) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                String.format(t("Bu dönemde kılınmayan namaz oranı: %%%d"), percent.toInt()),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = percent, onValueChange = { percent = (it / 10f).toInt() * 10f }, valueRange = 10f..100f, steps = 8,
                colors = SliderDefaults.colors(thumbColor = GoldChampagne, activeTrackColor = GoldChampagne)
            )
            if (years > 0) {
                Text(
                    String.format(t("Tahmini borç: %1\$d yıl ≈ %2\$d gün (her vakitten %2\$d)"), years, days),
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { confirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) { Text(t("Kaza defterime ekle"), fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                t("Hesap tahminidir. Ergenlik (bulûğ) belirtileri erkeklerde en erken 12, kızlarda en erken 9 yaşında görülebilir; belirti yoksa 15 yaş esas alınır. Emin değilseniz bir din görevlisine danışın."),
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(t("Kaza defterine eklensin mi?")) },
            text = { Text(String.format(t("Her vakit (vitir dahil) için %d kaza eklenecek."), days)) },
            confirmButton = { Button(onClick = { onAdd(days); confirm = false }) { Text(t("Ekle")) } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(t("Vazgeç")) } }
        )
    }
}
