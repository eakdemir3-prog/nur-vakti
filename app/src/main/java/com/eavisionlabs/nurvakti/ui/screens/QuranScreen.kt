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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
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
import com.eavisionlabs.nurvakti.data.QuranRepository
import com.eavisionlabs.nurvakti.model.Surah
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.i18n.t
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun QuranScreen(
    uiState: UiState,
    onSearchChange: (String) -> Unit,
    onOpenSurah: (Surah) -> Unit,
    onPlayAudio: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onOpenJuz: (Int) -> Unit = {},
    onOpenAyah: (Int, Int) -> Unit = { _, _ -> },
    onContinueReading: () -> Unit = {},
    onSetDailyGoal: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tümü") }
    var showGoalDialog by remember { mutableStateOf(false) }

    val filteredSurahs = remember(uiState.surahs, selectedFilter, uiState.favoriteSurahs) {
        when (selectedFilter) {
            "Favoriler" -> uiState.surahs.filter { uiState.favoriteSurahs.contains(it.number) }
            else -> uiState.surahs
        }
    }

    val lastSurah = QuranRepository.surahs.find { it.number == uiState.lastReadSurahNumber } ?: QuranRepository.surahs.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quran_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 1. Resume Last Read Hero Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onContinueReading() }
                    .testTag("resume_reading_card"),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldChampagne.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = t("Kaldığın Yerden Devam Et"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "${lastSurah.number}. ${lastSurah.nameTurkish} Suresi (${lastSurah.nameArabic})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = String.format(t("%1\$d. ayet • Sayfa %2\$d"), uiState.lastReadAyahNumber, uiState.lastReadPage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = { onContinueReading() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = GoldChampagne,
                            contentColor = EmeraldDarkest
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = t("Devam Et")
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 1b. Günlük okuma hedefi ve hatim ilerlemesi
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable { showGoalDialog = true },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            String.format(t("Bugün %1\$d / %2\$d sayfa"), uiState.pagesReadToday, uiState.dailyPageGoal),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.Edit, contentDescription = t("Hedefi değiştir"), tint = GoldChampagne, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (uiState.pagesReadToday.toFloat() / uiState.dailyPageGoal.coerceAtLeast(1)).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = GoldChampagne, trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        String.format(t("Hatim ilerlemesi: %1\$d / 604 sayfa • Tamamlanan hatim: %2\$d"), uiState.hatimPagesRead, uiState.completedHatims),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { uiState.hatimPagesRead / 604f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = GoldChampagne.copy(alpha = 0.7f), trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Search Field
        item {
            OutlinedTextField(
                value = uiState.quranSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quran_search_input"),
                placeholder = {
                    Text(
                        t("Sure adı, numarası ya da mealde kelime ara…"),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = t("Ara"),
                        tint = GoldChampagne
                    )
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
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Tümü", "Cüzler", "Yer İmleri", "Favoriler").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(t(filter), maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldChampagne,
                            selectedLabelColor = EmeraldDarkest,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3b. Mealde arama sonuçları
        if (uiState.verseSearchResults.isNotEmpty() && selectedFilter == "Tümü") {
            item {
                Text(
                    String.format(t("Mealde geçen ayetler (%d)"), uiState.verseSearchResults.size),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = GoldChampagne),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            items(uiState.verseSearchResults, key = { "vs_${it.surahNumber}_${it.ayahNumber}" }) { a ->
                AyahLinkCard(a) { onOpenAyah(a.surahNumber, a.ayahNumber) }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }

        if (selectedFilter == "Cüzler") {
            items((1..30).toList(), key = { "juz_$it" }) { juz ->
                val done = uiState.completedJuzs.contains(juz)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(16.dp)).clickable { onOpenJuz(juz) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (done) GoldChampagne else MaterialTheme.colorScheme.outline)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("$juz", fontWeight = FontWeight.Bold, color = GoldChampagne) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(String.format(t("%d. Cüz"), juz), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            val start = juzStartLabels.getOrNull(juz - 1) ?: ""
                            Text(start, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (done) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldChampagne)
                        else Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            return@LazyColumn
        }

        if (selectedFilter == "Yer İmleri") {
            val marks = uiState.bookmarks.mapNotNull { k ->
                val p = k.split(':'); val s1 = p.getOrNull(0)?.toIntOrNull(); val a1 = p.getOrNull(1)?.toIntOrNull()
                if (s1 != null && a1 != null) s1 to a1 else null
            }.sortedWith(compareBy({ it.first }, { it.second }))
            if (marks.isEmpty()) {
                item {
                    Text(
                        t("Henüz yer imi yok. Okurken ayetin yanındaki yer imi simgesine dokunun."),
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }
            items(marks, key = { "bm_${it.first}_${it.second}" }) { (s1, a1) ->
                val ayah = remember(s1, a1, I18n.language) { QuranRepository.getAyah(s1, a1) }
                if (ayah != null) AyahLinkCard(ayah) { onOpenAyah(s1, a1) }
            }
            return@LazyColumn
        }

        // 4. 114 Surahs List
        items(filteredSurahs, key = { it.number }) { surah ->
            val isFav = uiState.favoriteSurahs.contains(surah.number)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenSurah(surah) }
                    .testTag("surah_item_${surah.number}"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Surah Number Diamond/Circle Badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${surah.number}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldChampagne
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = surah.nameTurkish,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${surah.revelationType})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${surah.meaningTurkish} • ${surah.totalVerses} Ayet",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Arabic Calligraphy + Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.nameArabic,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldChampagne,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        IconButton(
                            onClick = { onPlayAudio(surah.number) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = t("Dinle"),
                                tint = GoldChampagne
                            )
                        }

                        IconButton(
                            onClick = { onToggleFavorite(surah.number) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = t("Favori"),
                                tint = if (isFav) GoldChampagne else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text(t("Günlük okuma hedefi")) },
            text = {
                Column {
                    Text(t("Her gün kaç sayfa okumak istiyorsunuz? (1 cüz ≈ 20 sayfa)"), style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    com.eavisionlabs.nurvakti.ui.components.ChoiceChips(
                        options = listOf(1, 2, 3, 5, 10, 20).map { it to "$it" },
                        selected = uiState.dailyPageGoal,
                        onSelect = { onSetDailyGoal(it); showGoalDialog = false }
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showGoalDialog = false }) { Text(t("Kapat")) } }
        )
    }
}

/** Her cüzün başladığı yer (Medine Mushafı) */
private val juzStartLabels = listOf(
    "Fâtiha 1", "Bakara 142", "Bakara 253", "Âl-i İmrân 93", "Nisâ 24", "Nisâ 148", "Mâide 82", "En'âm 111",
    "A'râf 88", "Enfâl 41", "Tevbe 93", "Hûd 6", "Yûsuf 53", "Hicr 1", "İsrâ 1", "Kehf 75", "Enbiyâ 1",
    "Mü'minûn 1", "Furkân 21", "Neml 56", "Ankebût 46", "Ahzâb 31", "Yâsîn 28", "Zümer 32", "Fussilet 47",
    "Ahkâf 1", "Zâriyât 31", "Mücâdele 1", "Mülk 1", "Nebe' 1"
)

@Composable
private fun AyahLinkCard(a: com.eavisionlabs.nurvakti.model.Ayah, onClick: () -> Unit) {
    val name = QuranRepository.surahs.getOrNull(a.surahNumber - 1)?.nameTurkish ?: ""
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("$name ${a.ayahNumber}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = GoldChampagne))
            Text(
                a.translationTr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3, overflow = TextOverflow.Ellipsis
            )
        }
    }
}
