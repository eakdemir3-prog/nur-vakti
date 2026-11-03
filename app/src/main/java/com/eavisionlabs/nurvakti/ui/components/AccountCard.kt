package com.eavisionlabs.nurvakti.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.EmeraldDarkest
import com.eavisionlabs.nurvakti.ui.theme.GoldChampagne

/** Google hesabı işlemleri (ekranlar arasında paylaşılır) */
class AccountActions(
    val signIn: () -> Unit,
    val backup: () -> Unit,
    val restore: () -> Unit,
    val setAuto: (Boolean) -> Unit,
    val signOut: () -> Unit,
    val delete: () -> Unit
)

@Composable
fun AccountCard(uiState: UiState, actions: AccountActions, modifier: Modifier = Modifier) {
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmRestore by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (uiState.accountEmail == null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t("Google ile giriş yap"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            t("Kayıtlarınız Google Drive'a yedeklenir, Pro üyeliğiniz yeni telefonda da açılır."),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = actions.signIn,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest)
                ) {
                    Text("G", fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(10.dp))
                    Text(t("Google ile devam et"), fontWeight = FontWeight.Bold)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(42.dp).clip(CircleShape).background(GoldChampagne.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (uiState.accountName ?: uiState.accountEmail).take(1).uppercase(),
                            color = GoldChampagne, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(uiState.accountName ?: t("Google hesabı"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(uiState.accountEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (uiState.cloudBusy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = GoldChampagne)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (uiState.cloudLastBackup > 0L) String.format(
                        t("Son bulut yedeği: %s"),
                        java.text.SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(uiState.cloudLastBackup))
                    ) else t("Henüz bulut yedeği yok"),
                    style = MaterialTheme.typography.labelMedium, color = GoldChampagne
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = actions.backup, enabled = !uiState.cloudBusy, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(t("Yedekle"), maxLines = 1)
                    }
                    OutlinedButton(onClick = { confirmRestore = true }, enabled = !uiState.cloudBusy, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(t("Geri yükle"), maxLines = 1)
                    }
                }
                ToggleRow(t("Otomatik yedekleme"), t("Günde bir kez, uygulama açıldığında"), Icons.Default.Autorenew, uiState.cloudAutoBackup) { actions.setAuto(it) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = actions.signOut) { Text(t("Çıkış yap")) }
                    TextButton(onClick = { confirmDelete = true }) { Text(t("Hesabımı sil"), color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }

    if (confirmRestore) {
        AlertDialog(
            onDismissRequest = { confirmRestore = false },
            title = { Text(t("Buluttan geri yüklensin mi?")) },
            text = { Text(t("Bu telefondaki ayarlar ve kayıtlar, Google Drive'daki yedekle değiştirilecek.")) },
            confirmButton = { Button(onClick = { confirmRestore = false; actions.restore() }) { Text(t("Geri yükle")) } },
            dismissButton = { TextButton(onClick = { confirmRestore = false }) { Text(t("Vazgeç")) } }
        )
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(t("Hesabınız silinsin mi?")) },
            text = {
                Text(t("Google Drive'daki NurVakti yedeğiniz kalıcı olarak silinir ve hesabınız uygulamadan kaldırılır. Bu telefondaki kayıtlar silinmez. Pro aboneliğiniz Google Play'de devam eder."))
            },
            confirmButton = {
                Button(
                    onClick = { confirmDelete = false; actions.delete() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(t("Kalıcı olarak sil")) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(t("Vazgeç")) } }
        )
    }
}
