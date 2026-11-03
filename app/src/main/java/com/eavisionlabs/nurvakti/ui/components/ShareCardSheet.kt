package com.eavisionlabs.nurvakti.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.share.ShareCardRenderer
import com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest
import com.eavisionlabs.nurvakti.ui.theme.GoldChampagne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Görsel tebrik / ayet kartı oluşturucu: fon seçimi, başlık düzenleme, önizleme,
 * görsel ya da metin olarak paylaşma.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardSheet(
    initialTitle: String,
    body: String,
    arabic: String? = null,
    reference: String? = null,
    shareText: String,
    onShareImage: (Bitmap) -> Unit,
    onShareText: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        ShareCardEditor(initialTitle, body, arabic, reference, shareText, onShareImage, onShareText)
    }
}

@Composable
fun ShareCardEditor(
    initialTitle: String,
    body: String,
    arabic: String?,
    reference: String?,
    shareText: String,
    onShareImage: (Bitmap) -> Unit,
    onShareText: (String) -> Unit
) {
    val context = LocalContext.current
    var styleIndex by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf(initialTitle) }
    val style = ShareCardRenderer.styles[styleIndex]
    val bitmap by produceState<Bitmap?>(null, styleIndex, title, body, arabic, reference) {
        value = withContext(Dispatchers.Default) {
            runCatching { ShareCardRenderer.renderCard(context, style, title, body, arabic, reference) }.getOrNull()
        }
    }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            t("Görsel Kart Oluştur"),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GoldChampagne,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth(0.78f).aspectRatio(1080f / 1350f).clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val b = bitmap
            if (b != null) Image(b.asImageBitmap(), contentDescription = t("Kart önizlemesi"), modifier = Modifier.fillMaxSize())
            else CircularProgressIndicator(color = GoldChampagne)
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
            items(ShareCardRenderer.styles.size) { i ->
                val s = ShareCardRenderer.styles[i]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { styleIndex = i }) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(Brush.verticalGradient(listOf(Color(s.top), Color(s.bottom))))
                            .border(BorderStroke(if (i == styleIndex) 3.dp else 1.dp, if (i == styleIndex) GoldChampagne else Color(s.accent).copy(alpha = 0.6f)), CircleShape)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(t(s.name), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 40) title = it },
            label = { Text(t("Başlık")) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { onShareText(shareText) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.ShortText, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(t("Metin"), maxLines = 1)
            }
            Button(
                onClick = { bitmap?.let(onShareImage) },
                enabled = bitmap != null,
                modifier = Modifier.weight(1.6f).heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(t("Görsel paylaş"), fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
