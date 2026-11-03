package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.data.DailyContent
import com.eavisionlabs.nurvakti.data.TopicIndex
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.data.DailyVerse
import com.eavisionlabs.nurvakti.ui.components.SubScreenScaffold
import com.eavisionlabs.nurvakti.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private fun topicIcon(id: String): ImageVector = when (id) {
    "huzur" -> Icons.Default.Spa
    "sabir" -> Icons.Default.HourglassBottom
    "rizik" -> Icons.Default.Grass
    "sifa" -> Icons.Default.Healing
    "tovbe" -> Icons.Default.Replay
    "umit" -> Icons.Default.WbSunny
    "dua" -> Icons.Default.VolunteerActivism
    "sukur" -> Icons.Default.Favorite
    "tevekkul" -> Icons.Default.Anchor
    "aile" -> Icons.Default.FamilyRestroom
    "anne_baba" -> Icons.Default.Elderly
    "olum" -> Icons.Default.NightsStay
    "adalet" -> Icons.Default.Balance
    else -> Icons.Default.School
}

/** Konulu Kur'an fihristi */
@Composable
fun TopicsScreen(
    onBack: () -> Unit,
    onOpenAyah: (Int, Int) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = TopicIndex.topics.firstOrNull { it.id == selectedId }
    BackHandler { if (selectedId != null) selectedId = null else onBack() }

    SubScreenScaffold(
        title = selected?.let { t(it.title) } ?: t("Konulu Kur'an Fihristi"),
        onBack = { if (selectedId != null) selectedId = null else onBack() }
    ) { padding ->
        if (selected == null) {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding).testTag("topics_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        t("Ruh halinize ya da aradığınız konuya göre ayetler. Bir ayete dokunarak okuyucuda açabilirsiniz."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(TopicIndex.topics, key = { it.id }) { topic ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { selectedId = topic.id },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(12.dp), color = GoldChampagne.copy(alpha = 0.14f)) {
                                Icon(topicIcon(topic.id), contentDescription = null, tint = GoldChampagne, modifier = Modifier.padding(10.dp).size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(t(topic.title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    t(topic.subtitle) + " • " + String.format(t("%d ayet"), topic.refs.size),
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2, overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        } else {
            val context = LocalContext.current
            val verses by produceState<List<DailyVerse>>(emptyList(), selected.id) {
                value = withContext(Dispatchers.IO) {
                    selected.refs.mapNotNull { (s, a) -> runCatching { DailyContent.verse(context, s, a) }.getOrNull() }
                }
            }
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding).testTag("topic_detail"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(verses, key = { "${it.surah}:${it.ayah}" }) { v ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenAyah(v.surah, v.ayah) },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(v.reference, style = MaterialTheme.typography.labelLarge, color = GoldChampagne, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                IconButton(onClick = { onShare("${v.arabic}\n\n\"${v.text}\"\n\n(${v.reference} • NurVakti)") }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Share, contentDescription = t("Paylaş"), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                v.arabic,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = QuranFont, textAlign = TextAlign.Right, textDirection = TextDirection.Rtl,
                                    fontSize = 22.sp, lineHeight = 38.sp, color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(v.text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
