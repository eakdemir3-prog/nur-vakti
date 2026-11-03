package com.eavisionlabs.nurvakti.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.data.DailyContent
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.components.ShareCardSheet
import com.eavisionlabs.nurvakti.ui.components.SubScreenScaffold
import com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest
import com.eavisionlabs.nurvakti.ui.theme.GoldChampagne

/** Her gün açık tebrik kartı oluşturucu: kategori seç, mesajı görsel kart ya da metin olarak paylaş. */
@Composable
fun GreetingsScreen(
    onBack: () -> Unit,
    onShare: (String) -> Unit,
    onShareImage: (Bitmap) -> Unit,
    initialCategory: String = "cuma",
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var category by rememberSaveable { mutableStateOf(initialCategory) }
    var cardMessage by remember { mutableStateOf<String?>(null) }
    val title = DailyContent.greetingCategories.firstOrNull { it.first == category }?.second ?: "Hayırlı Cumalar"
    val messages = remember(category) { DailyContent.greetingsFor(category) }

    SubScreenScaffold(title = t("Tebrik Kartları"), onBack = onBack) { padding ->
        Column(modifier.fillMaxSize().padding(padding).testTag("greetings_screen")) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DailyContent.greetingCategories.forEach { (id, name) ->
                    FilterChip(
                        selected = id == category,
                        onClick = { category = id },
                        label = { Text(t(name), maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldChampagne, selectedLabelColor = EmeraldDarkest)
                    )
                }
            }
            GreetingMessagesList(
                title = t(title),
                messages = messages,
                onCreateCard = { cardMessage = it },
                onShare = onShare
            )
        }
    }

    cardMessage?.let { msg ->
        ShareCardSheet(
            initialTitle = t(title).substringBefore(" Olsun"),
            body = msg,
            shareText = "$msg\n\n— NurVakti",
            onShareImage = { bmp -> cardMessage = null; onShareImage(bmp) },
            onShareText = { txt -> cardMessage = null; onShare(txt) },
            onDismiss = { cardMessage = null }
        )
    }
}
