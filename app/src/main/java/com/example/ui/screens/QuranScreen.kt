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
import com.example.data.QuranRepository
import com.example.model.Surah
import com.example.ui.UiState
import com.example.ui.theme.EmeraldDarkest
import com.example.ui.theme.GoldChampagne

@Composable
fun QuranScreen(
    uiState: UiState,
    onSearchChange: (String) -> Unit,
    onOpenSurah: (Surah) -> Unit,
    onPlayAudio: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tümü") }

    val filteredSurahs = remember(uiState.surahs, selectedFilter, uiState.favoriteSurahs) {
        when (selectedFilter) {
            "Mekki" -> uiState.surahs.filter { it.revelationType == "Mekki" }
            "Medeni" -> uiState.surahs.filter { it.revelationType == "Medeni" }
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
                    .clickable { onOpenSurah(lastSurah) }
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
                                text = "Kaldığın Yerden Devam Et",
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
                                text = "${lastSurah.totalVerses} Ayet • ${lastSurah.revelationType} • ${lastSurah.juzNumber}. Cüz",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = { onOpenSurah(lastSurah) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = GoldChampagne,
                            contentColor = EmeraldDarkest
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Devam Et"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                        "Sure adı veya numarası ara (Örn: Yasin, Fatiha, 36)...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Tümü", "Mekki", "Medeni", "Favoriler").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
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
                                contentDescription = "Dinle",
                                tint = GoldChampagne
                            )
                        }

                        IconButton(
                            onClick = { onToggleFavorite(surah.number) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favori",
                                tint = if (isFav) GoldChampagne else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
