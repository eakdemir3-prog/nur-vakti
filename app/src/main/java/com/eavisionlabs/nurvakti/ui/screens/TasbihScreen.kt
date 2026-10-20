package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onConfirmReset: () -> Unit,
    onSetResetDialogOpen: (Boolean) -> Unit,
    onSetTarget: (Int) -> Unit,
    onSelectPreset: (String, String, String, Int) -> Unit,
    onSetAddDialogOpen: (Boolean) -> Unit = {},
    onAddCustom: (String, String, String, Int) -> Unit = { _, _, _, _ -> },
    onRemoveCustom: (String) -> Unit = {},
    onSetVibration: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showHistory by remember { mutableStateOf(false) }
    BackHandler { onBack() }

    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "tasbih_scale"
    )

    val presets = listOf(
        Triple("Sübhanallah", "سُبْحَانَ اللَّهِ", "Allah her türlü noksanlıktan münezzehtir."),
        Triple("Elhamdülillah", "الْحَمْدُ لِلَّهِ", "Bütün hamd ve şükürler yalnız Allah'a aittir."),
        Triple("Allahu Ekber", "اللَّهُ أَكْبَرُ", "Allah en büyüktür, şanı her şeyden yücedir."),
        Triple("Lâ ilâhe illallâh", "لَا إِلَٰهَ إِلَّا اللَّهُ", "Allah'tan başka hiçbir ilâh yoktur."),
        Triple("Estağfirullah", "أَسْتَغْفِرُ اللَّهَ", "Yüce Allah'tan bağışlanma ve af dilerim."),
        Triple("Salavat-ı Şerife", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ", "Allah'ım! Efendimiz Hz. Muhammed'e salât eyle.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("Dijital Tesbih"), fontWeight = FontWeight.Bold, color = GoldLight) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = GoldChampagne)
                    }
                },
                actions = {
                    IconButton(onClick = { onSetVibration(!uiState.tasbihVibration) }) {
                        Icon(
                            if (uiState.tasbihVibration) Icons.Default.Vibration else Icons.Default.PhonelinkErase,
                            contentDescription = t("Titreşim"), tint = if (uiState.tasbihVibration) GoldChampagne else SoftGrey
                        )
                    }
                    IconButton(onClick = { showHistory = true }) {
                        Icon(Icons.Default.BarChart, contentDescription = t("Geçmiş"), tint = GoldChampagne)
                    }
                    IconButton(onClick = { onSetResetDialogOpen(true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sıfırla", tint = SoftGrey)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDarkest)
            )
        },
        containerColor = EmeraldDarkest,
        modifier = modifier.testTag("tasbih_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Preset Dhikrs Row
            Column {
                Text(
                    text = t("Zikirler"),
                    style = MaterialTheme.typography.labelMedium.copy(color = SoftGrey),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.customDhikrs) { c ->
                        val title = c[0]
                        val isSelected = uiState.selectedDhikrTitle == title
                        InputChip(
                            selected = isSelected,
                            onClick = { onSelectPreset(title, c[1], c[2], c[3].toIntOrNull() ?: 33) },
                            label = { Text(title) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close, contentDescription = t("Sil"),
                                    modifier = Modifier.size(16.dp).clickable { onRemoveCustom(title) }
                                )
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = GoldChampagne,
                                selectedLabelColor = EmeraldDarkest
                            )
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { onSetAddDialogOpen(true) },
                            label = { Text(t("Zikir ekle")) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    items(presets) { (title, arabic, meaning) ->
                        val isSelected = uiState.selectedDhikrTitle == title
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectPreset(title, arabic, meaning, 33) },
                            label = { Text(title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldChampagne,
                                selectedLabelColor = EmeraldDarkest,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // Current Selected Dhikr Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.selectedDhikrArabic,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.selectedDhikrTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.selectedDhikrMeaning,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Big Tactile Counter Button with Outer Target Ring
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(EmeraldCardElevated)
                    .clickable {
                        buttonPressed = true
                        onIncrement()
                        buttonPressed = false
                    }
                    .testTag("tasbih_counter_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.currentDhikrCount}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldChampagne
                        )
                    )
                    Text(
                        text = "Hedef: ${uiState.targetDhikrCount}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SoftGrey
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Çekmek için dokun",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldLight.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Target Selectors (33, 99, 100, 500, Serbest)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(33, 99, 100, 500).forEach { target ->
                    val isSel = uiState.targetDhikrCount == target
                    OutlinedButton(
                        onClick = { onSetTarget(target) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSel) GoldChampagne else Color.Transparent,
                            contentColor = if (isSel) EmeraldDarkest else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "$target", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Actions: Undo (Geri Al) & Daily Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onDecrement,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Geri Al",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Geri Al (-1)",
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Bugünkü Toplam",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        )
                        Text(
                            text = "${uiState.todayDhikrCompletedTotal} Zikir",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne
                            )
                        )
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (uiState.isResetConfirmDialogOpen) {
        AlertDialog(
            onDismissRequest = { onSetResetDialogOpen(false) },
            title = { Text("Sayacı Sıfırla") },
            text = { Text("Mevcut zikir sayacını sıfırlamak istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = onConfirmReset,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest)
                ) {
                    Text("Evet, Sıfırla")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetResetDialogOpen(false) }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    if (uiState.isAddCustomDhikrDialogOpen) {
        var title by remember { mutableStateOf("") }
        var arabic by remember { mutableStateOf("") }
        var meaning by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("33") }
        AlertDialog(
            onDismissRequest = { onSetAddDialogOpen(false) },
            title = { Text(t("Kendi zikrinizi ekleyin")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(title, { title = it }, label = { Text(t("Zikir adı (ör. Hasbünallah)")) }, singleLine = true)
                    OutlinedTextField(arabic, { arabic = it }, label = { Text(t("Arapça (isteğe bağlı)")) }, singleLine = true)
                    OutlinedTextField(meaning, { meaning = it }, label = { Text(t("Anlamı (isteğe bağlı)")) })
                    OutlinedTextField(
                        target, { v -> target = v.filter { it.isDigit() }.take(6) },
                        label = { Text(t("Hedef sayı")) }, singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onAddCustom(title, arabic, meaning, target.toIntOrNull() ?: 33) }, enabled = title.isNotBlank()) { Text(t("Ekle")) }
            },
            dismissButton = { TextButton(onClick = { onSetAddDialogOpen(false) }) { Text(t("Vazgeç")) } }
        )
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text(t("Zikir geçmişi (7 gün)")) },
            text = {
                Column {
                    com.eavisionlabs.nurvakti.ui.components.BarChart(uiState.dhikrWeek, maxValue = 100)
                    Spacer(Modifier.height(8.dp))
                    Text(String.format(t("Bugün: %1$d • Bu hafta: %2$d"), uiState.todayDhikrCompletedTotal, uiState.dhikrWeek.sumOf { it.value }))
                }
            },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text(t("Kapat")) } }
        )
    }
}
