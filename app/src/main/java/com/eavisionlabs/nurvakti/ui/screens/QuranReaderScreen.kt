package com.eavisionlabs.nurvakti.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.data.PlaybackStatus
import com.eavisionlabs.nurvakti.data.QuranRepository
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.model.Ayah
import com.eavisionlabs.nurvakti.ui.ReaderMode
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.ChoiceChips
import com.eavisionlabs.nurvakti.ui.components.ProBadge
import com.eavisionlabs.nurvakti.ui.theme.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

private sealed class ReaderItem {
    data class Header(val surah: Int, val fromStart: Boolean) : ReaderItem()
    data class Verse(val ayah: Ayah, val index: Int) : ReaderItem()
}

private const val BASMALA = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ"

fun toArabicDigits(n: Int): String = n.toString().map { c -> if (c in '0'..'9') ('٠' + (c - '0')) else c }.joinToString("")

val ayahReciters = listOf(
    "alafasy" to "Mishary Alafasy",
    "abdulbaset" to "Abdulbasit Abdussamed",
    "husary" to "Mahmud Halil el-Husarî",
    "maher" to "Maher el-Muaykılî",
    "ghamdi" to "Sa'd el-Ğâmidî"
)

@OptIn(ExperimentalMaterial3Api::class, kotlinx.coroutines.FlowPreview::class)
@Composable
fun QuranReaderScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSetArabicFontSize: (Int) -> Unit,
    onSetMealFontSize: (Int) -> Unit,
    onSetReadingTheme: (String) -> Unit,
    onToggleShowMeal: () -> Unit,
    onToggleShowArabic: () -> Unit,
    onVisible: (List<Ayah>) -> Unit,
    onToggleBookmark: (Int, Int) -> Unit,
    onPlayFrom: (Int, Int) -> Unit,
    onStopAudio: () -> Unit,
    onTogglePause: () -> Unit,
    onSetRepeat: (Int) -> Unit,
    onSetHifz: (Boolean, Boolean) -> Unit,
    onSetReciter: (String) -> Unit,
    onShare: (String) -> Unit,
    onOpenPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val verses = uiState.currentSurahVerses
    val surah = uiState.selectedSurah

    if (surah == null || verses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(EmeraldDarkest), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GoldChampagne)
        }
        return
    }

    val (readerBg, readerCard, readerText, readerGold) = when (uiState.readingTheme) {
        "LIGHT" -> listOf(IvoryBackground, CardLight, EmeraldTextDark, GoldAccentLight)
        "SEPIA" -> listOf(SepiaBackground, SepiaCard, SepiaText, SepiaGold)
        else -> listOf(EmeraldDarkest, EmeraldCard, CreamWhite, GoldChampagne)
    }

    // Liste öğeleri: cüz modunda her surenin başına başlık eklenir
    val items = remember(verses) {
        val list = mutableListOf<ReaderItem>()
        verses.forEachIndexed { i, a ->
            if (a.ayahNumber == 1 || (i == 0)) list += ReaderItem.Header(a.surahNumber, a.ayahNumber == 1)
            list += ReaderItem.Verse(a, i)
        }
        list
    }
    val initialIndex = remember(items, uiState.readerScrollTarget) {
        items.indexOfFirst { it is ReaderItem.Verse && it.index == uiState.readerScrollTarget }.coerceAtLeast(0)
            .let { if (uiState.readerScrollTarget == 0) 0 else it }
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // Görünen ayetleri kaydet (kaldığım yer + okunan sayfalar)
    LaunchedEffect(listState, items) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.mapNotNull { (items.getOrNull(it.index) as? ReaderItem.Verse)?.ayah }
        }
            .distinctUntilChanged()
            .debounce(1200)
            .collect { visible -> if (visible.isNotEmpty()) onVisible(visible) }
    }

    // Çalan ayeti takip et
    val player = uiState.playerState
    val playingKey = if (player.ayahMode && player.currentAyah > 0) "${player.currentAyahSurah}:${player.currentAyah}" else null
    LaunchedEffect(playingKey) {
        if (playingKey != null) {
            val idx = items.indexOfFirst { it is ReaderItem.Verse && "${it.ayah.surahNumber}:${it.ayah.ayahNumber}" == playingKey }
            if (idx >= 0) listState.animateScrollToItem(idx)
        }
    }

    var showSettingsSheet by remember { mutableStateOf(false) }
    val revealed = remember { mutableStateMapOf<String, Boolean>() }

    val title = if (uiState.readerMode == ReaderMode.JUZ) String.format(t("%d. Cüz"), uiState.readerJuz)
    else "${surah.number}. ${surah.nameTurkish}"
    val firstVisibleVerse = (items.getOrNull(listState.firstVisibleItemIndex) as? ReaderItem.Verse)?.ayah
        ?: (items.getOrNull(listState.firstVisibleItemIndex + 1) as? ReaderItem.Verse)?.ayah
    val subtitle = firstVisibleVerse?.let {
        String.format(t("Sayfa %1\$d • Cüz %2\$d"), it.page, it.juz)
    } ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = readerGold))
                        Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(color = readerText.copy(alpha = 0.7f)))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("Geri"), tint = readerGold) }
                },
                actions = {
                    IconButton(onClick = {
                        val start = (items.getOrNull(listState.firstVisibleItemIndex) as? ReaderItem.Verse)?.index
                            ?: (items.getOrNull(listState.firstVisibleItemIndex + 1) as? ReaderItem.Verse)?.index ?: 0
                        onPlayFrom(start, player.repeatEach)
                    }) { Icon(Icons.Default.PlayCircle, contentDescription = t("Ayet ayet dinle"), tint = readerGold) }
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(Icons.Default.Tune, contentDescription = t("Okuma ayarları"), tint = readerGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = readerBg)
            )
        },
        bottomBar = {
            if (player.ayahMode && player.status != PlaybackStatus.IDLE) {
                Surface(color = readerCard, tonalElevation = 6.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "${player.currentSurahNameTr} ${player.currentAyah}",
                                color = readerGold, fontWeight = FontWeight.Bold, maxLines = 1
                            )
                            Text(
                                if (player.status == PlaybackStatus.ERROR) player.errorMessage ?: t("Hata")
                                else if (player.repeatEach > 1) String.format(t("Tekrar %1\$d/%2\$d"), player.currentRepeat, player.repeatEach)
                                else if (player.status == PlaybackStatus.BUFFERING) t("Yükleniyor…") else t("Ayet ayet okunuyor"),
                                color = readerText.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall, maxLines = 1
                            )
                        }
                        IconButton(onClick = onTogglePause) {
                            Icon(
                                if (player.status == PlaybackStatus.PLAYING) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null, tint = readerGold, modifier = Modifier.size(32.dp)
                            )
                        }
                        IconButton(onClick = onStopAudio) { Icon(Icons.Default.StopCircle, contentDescription = t("Durdur"), tint = readerGold, modifier = Modifier.size(32.dp)) }
                    }
                }
            }
        },
        containerColor = readerBg,
        modifier = modifier.testTag("quran_reader_screen")
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            items(items, key = {
                when (it) {
                    is ReaderItem.Header -> "h_${it.surah}"
                    is ReaderItem.Verse -> "v_${it.ayah.surahNumber}_${it.ayah.ayahNumber}"
                }
            }) { item ->
                when (item) {
                    is ReaderItem.Header -> SurahHeader(item.surah, item.fromStart, readerCard, readerGold, readerText)
                    is ReaderItem.Verse -> {
                        val ayah = item.ayah
                        val key = "${ayah.surahNumber}:${ayah.ayahNumber}"
                        val isPlaying = key == playingKey
                        val isBookmarked = uiState.bookmarks.contains(key)
                        val hideArabic = uiState.hifzHideArabic && revealed[key + "a"] != true
                        val hideMeal = uiState.hifzHideMeal && revealed[key + "m"] != true
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isPlaying) readerGold.copy(alpha = 0.16f) else readerCard,
                            border = if (isPlaying) BorderStroke(1.5.dp, readerGold) else null
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(readerGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${ayah.ayahNumber}", style = MaterialTheme.typography.labelMedium.copy(color = readerGold, fontWeight = FontWeight.Bold))
                                    }
                                    Spacer(Modifier.weight(1f))
                                    SmallIcon(Icons.Default.PlayArrow, t("Buradan dinle"), readerGold) { onPlayFrom(item.index, player.repeatEach) }
                                    SmallIcon(if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, t("Yer imi"), readerGold) {
                                        onToggleBookmark(ayah.surahNumber, ayah.ayahNumber)
                                    }
                                    SmallIcon(Icons.Default.ContentCopy, t("Kopyala"), readerGold) {
                                        val name = QuranRepository.surahs.getOrNull(ayah.surahNumber - 1)?.nameTurkish ?: ""
                                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cb.setPrimaryClip(ClipData.newPlainText("Ayet", "${ayah.textArabic}\n${ayah.translationTr}\n($name, ${ayah.ayahNumber})"))
                                        Toast.makeText(context, t("Ayet panoya kopyalandı"), Toast.LENGTH_SHORT).show()
                                    }
                                    SmallIcon(Icons.Default.Share, t("Paylaş"), readerGold) {
                                        val name = QuranRepository.surahs.getOrNull(ayah.surahNumber - 1)?.nameTurkish ?: ""
                                        onShare("${ayah.textArabic}\n\n\"${ayah.translationTr}\"\n\n($name ${ayah.ayahNumber} • NurVakti)")
                                    }
                                }

                                if (uiState.showArabic) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = if (hideArabic) "• • • • • • • •" else "${ayah.textArabic} ۝${toArabicDigits(ayah.ayahNumber)}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = QuranFont,
                                            fontSize = uiState.arabicFontSize.sp,
                                            lineHeight = (uiState.arabicFontSize * 1.9).sp,
                                            color = if (hideArabic) readerText.copy(alpha = 0.35f) else readerText,
                                            textAlign = TextAlign.Right,
                                            textDirection = TextDirection.Rtl
                                        ),
                                        modifier = Modifier.fillMaxWidth().then(
                                            if (uiState.hifzHideArabic) Modifier.clickable { revealed[key + "a"] = !(revealed[key + "a"] ?: false) } else Modifier
                                        )
                                    )
                                }
                                if (uiState.showMeal) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = if (hideMeal) t("(Görmek için dokunun)") else ayah.translationTr,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = uiState.mealFontSize.sp,
                                            lineHeight = (uiState.mealFontSize * 1.5).sp,
                                            color = readerText.copy(alpha = if (hideMeal) 0.4f else 0.9f)
                                        ),
                                        modifier = if (uiState.hifzHideMeal) Modifier.clickable { revealed[key + "m"] = !(revealed[key + "m"] ?: false) } else Modifier
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    t("Meal: Diyanet İşleri Başkanlığı • Arapça metin: Kur'an Ansiklopedisi (Medine Mushafı sayfa düzeni)"),
                    style = MaterialTheme.typography.labelSmall.copy(color = readerText.copy(alpha = 0.6f), textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }, containerColor = readerBg) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp).navigationBarsPadding()
            ) {
                Text(t("Okuma Ayarları"), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = readerGold))
                Spacer(Modifier.height(14.dp))
                Label(t("Okuma Teması"), readerText)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("DARK" to t("Gece"), "LIGHT" to t("Açık"), "SEPIA" to t("Sepya")).forEach { (k, label) ->
                        val sel = uiState.readingTheme == k
                        OutlinedButton(
                            onClick = { onSetReadingTheme(k) }, modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (sel) readerGold else Color.Transparent,
                                contentColor = if (sel) readerBg else readerText
                            )
                        ) { Text(label, fontSize = 12.sp, maxLines = 1) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                SizeRow(String.format(t("Arapça yazı: %d"), uiState.arabicFontSize), readerText, readerGold,
                    { onSetArabicFontSize(uiState.arabicFontSize - 2) }, { onSetArabicFontSize(uiState.arabicFontSize + 2) })
                SizeRow(String.format(t("Meal yazısı: %d"), uiState.mealFontSize), readerText, readerGold,
                    { onSetMealFontSize(uiState.mealFontSize - 2) }, { onSetMealFontSize(uiState.mealFontSize + 2) })
                SwitchRow(t("Meali göster"), uiState.showMeal, readerText) { onToggleShowMeal() }
                SwitchRow(t("Arapça metni göster"), uiState.showArabic, readerText) { onToggleShowArabic() }

                Spacer(Modifier.height(12.dp))
                Label(t("Ayet ayet okuyan hafız"), readerText)
                ChoiceChips(options = ayahReciters, selected = uiState.ayahReciter, onSelect = onSetReciter)

                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(t("Hıfz (Ezber) Modu"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = readerGold))
                    Spacer(Modifier.width(8.dp))
                    if (!uiState.isPremiumActive) ProBadge()
                }
                Text(
                    t("Her ayeti istediğiniz kadar tekrarlatın, metni gizleyip kendinizi sınayın."),
                    style = MaterialTheme.typography.bodySmall, color = readerText.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(6.dp))
                Label(t("Her ayeti tekrarla"), readerText)
                ChoiceChips(
                    options = listOf(1 to "1×", 3 to "3×", 5 to "5×", 10 to "10×"),
                    selected = player.repeatEach,
                    onSelect = { onSetRepeat(it) }
                )
                SwitchRow(t("Arapça metni gizle"), uiState.hifzHideArabic, readerText) { onSetHifz(!uiState.hifzHideArabic, uiState.hifzHideMeal) }
                SwitchRow(t("Meali gizle"), uiState.hifzHideMeal, readerText) { onSetHifz(uiState.hifzHideArabic, !uiState.hifzHideMeal) }
                if (!uiState.isPremiumActive) {
                    TextButton(onClick = { showSettingsSheet = false; onOpenPremium() }) { Text(t("NurVakti Pro'yu incele"), color = readerGold) }
                }
            }
        }
    }
}

@Composable
private fun SurahHeader(number: Int, fromStart: Boolean, card: Color, gold: Color, text: Color) {
    val s = QuranRepository.surahs.getOrNull(number - 1) ?: return
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
        shape = RoundedCornerShape(20.dp),
        color = card,
        border = BorderStroke(1.dp, gold.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(s.nameArabic, style = MaterialTheme.typography.headlineMedium.copy(fontFamily = QuranFont, color = gold))
            Spacer(Modifier.height(4.dp))
            Text(
                "${s.number}. ${s.nameTurkish} • ${s.meaningTurkish}",
                style = MaterialTheme.typography.bodyMedium.copy(color = text.copy(alpha = 0.85f), fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
            Text(
                "${if (s.revelationType == "Mekki") t("Mekki") else t("Medeni")} • ${s.totalVerses} ${t("ayet")}",
                style = MaterialTheme.typography.labelSmall.copy(color = text.copy(alpha = 0.6f))
            )
            if (fromStart && number != 1 && number != 9) {
                Spacer(Modifier.height(10.dp))
                Text(BASMALA, style = MaterialTheme.typography.titleLarge.copy(fontFamily = QuranFont, color = gold, fontSize = 24.sp))
            }
        }
    }
}

@Composable
private fun SmallIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, tint: Color, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(icon, contentDescription = desc, tint = tint.copy(alpha = 0.8f), modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun Label(text: String, color: Color) {
    Text(text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, color = color), modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun SizeRow(label: String, textColor: Color, gold: Color, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = textColor))
        Row {
            IconButton(onClick = onMinus) { Icon(Icons.Default.Remove, contentDescription = t("Küçült"), tint = gold) }
            IconButton(onClick = onPlus) { Icon(Icons.Default.Add, contentDescription = t("Büyüt"), tint = gold) }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, textColor: Color, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = textColor)
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
