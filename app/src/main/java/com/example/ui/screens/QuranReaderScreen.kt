package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.model.Surah
import com.example.ui.UiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surah: Surah?,
    verses: List<Ayah>,
    uiState: UiState,
    onBack: () -> Unit,
    onPlayAudio: (Int) -> Unit,
    onSetArabicFontSize: (Int) -> Unit,
    onSetMealFontSize: (Int) -> Unit,
    onSetReadingTheme: (String) -> Unit,
    onToggleShowMeal: () -> Unit,
    onToggleShowArabic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    if (surah == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sure bulunamadı.")
        }
        return
    }

    // Determine reader colors based on readingTheme
    val (readerBg, readerCard, readerText, readerGold) = when (uiState.readingTheme) {
        "LIGHT" -> Quad(IvoryBackground, CardLight, EmeraldTextDark, GoldAccentLight)
        "SEPIA" -> Quad(SepiaBackground, SepiaCard, SepiaText, SepiaGold)
        else -> Quad(EmeraldDarkest, EmeraldCard, CreamWhite, GoldChampagne)
    }

    var showSettingsSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${surah.number}. ${surah.nameTurkish} Suresi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = readerGold
                            )
                        )
                        Text(
                            text = "${surah.nameArabic} • ${surah.revelationType} • ${surah.totalVerses} Ayet",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = readerGold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onPlayAudio(surah.number) }) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Sureyi Dinle",
                            tint = readerGold
                        )
                    }
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Okuma Ayarları",
                            tint = readerGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = readerBg
                )
            )
        },
        containerColor = readerBg,
        modifier = modifier.testTag("quran_reader_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Surah Header Ornament Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = readerCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, readerGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = surah.nameArabic,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = readerGold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${surah.nameTurkish} Suresi • ${surah.meaningTurkish}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = readerText.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = readerGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Verses List
            items(verses, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = readerCard
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Top Ayah Badge & Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(readerGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${ayah.ayahNumber}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = readerGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            "Ayet",
                                            "${ayah.textArabic}\n${ayah.translationTr}\n(${surah.nameTurkish} Suresi, ${ayah.ayahNumber}. Ayet)"
                                        )
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Ayet panoya kopyalandı", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Kopyala",
                                        tint = readerGold.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "${ayah.textArabic}\n\n\"${ayah.translationTr}\"\n\n(${surah.nameTurkish} Suresi ${ayah.ayahNumber}. Ayet • NurVakti)"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Ayeti Paylaş"))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Paylaş",
                                        tint = readerGold.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Arabic Verse Text
                        if (uiState.showArabic) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${ayah.textArabic} ۝${ayah.ayahNumber}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = uiState.arabicFontSize.sp,
                                    lineHeight = (uiState.arabicFontSize * 1.7).sp,
                                    color = readerText,
                                    textAlign = TextAlign.Right
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Turkish Translation
                        if (uiState.showMeal) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = ayah.translationTr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = uiState.mealFontSize.sp,
                                    lineHeight = (uiState.mealFontSize * 1.5).sp,
                                    color = readerText.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }

            // Meal source footer note
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Meal Kaynağı: Diyanet İşleri Başkanlığı Kur'an-ı Kerim Meali doğrulanmış metni.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Reading Settings Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = readerBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Okuma Ayarları",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = readerGold
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Reading Theme Toggle (Dark, Light, Sepia)
                Text(
                    text = "Okuma Teması",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = readerText
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("DARK" to "Gece Zümrüdü", "LIGHT" to "Fildişi Açık", "SEPIA" to "Sıcak Sepya").forEach { (themeKey, label) ->
                        val isSel = uiState.readingTheme == themeKey
                        OutlinedButton(
                            onClick = { onSetReadingTheme(themeKey) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) readerGold else Color.Transparent,
                                contentColor = if (isSel) EmeraldDarkest else readerText
                            )
                        ) {
                            Text(text = label, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Arabic Font Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Arapça Yazı Boyutu: ${uiState.arabicFontSize}sp",
                        style = MaterialTheme.typography.bodyMedium.copy(color = readerText)
                    )
                    Row {
                        IconButton(onClick = { onSetArabicFontSize(uiState.arabicFontSize - 2) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Küçült", tint = readerGold)
                        }
                        IconButton(onClick = { onSetArabicFontSize(uiState.arabicFontSize + 2) }) {
                            Icon(Icons.Default.Add, contentDescription = "Büyüt", tint = readerGold)
                        }
                    }
                }

                // Turkish Meal Font Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meal Yazı Boyutu: ${uiState.mealFontSize}sp",
                        style = MaterialTheme.typography.bodyMedium.copy(color = readerText)
                    )
                    Row {
                        IconButton(onClick = { onSetMealFontSize(uiState.mealFontSize - 2) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Küçült", tint = readerGold)
                        }
                        IconButton(onClick = { onSetMealFontSize(uiState.mealFontSize + 2) }) {
                            Icon(Icons.Default.Add, contentDescription = "Büyüt", tint = readerGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Türkçe Meal Göster", color = readerText)
                    Switch(checked = uiState.showMeal, onCheckedChange = { onToggleShowMeal() })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Arapça Metni Göster", color = readerText)
                    Switch(checked = uiState.showArabic, onCheckedChange = { onToggleShowArabic() })
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
