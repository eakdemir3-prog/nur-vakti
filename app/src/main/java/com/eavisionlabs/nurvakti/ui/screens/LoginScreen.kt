package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.theme.*

/** Giriş ekranı: Google ile giriş ya da hesapsız devam */
@Composable
fun LoginScreen(
    isBusy: Boolean,
    onGoogle: () -> Unit,
    onGuest: () -> Unit,
    onOpenPrivacy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(EmeraldDeep, EmeraldDarkest)))
            .testTag("login_screen")
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).safeDrawingPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Image(
                painter = painterResource(R.drawable.nurvakti_logo),
                contentDescription = null,
                modifier = Modifier.size(104.dp).clip(RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(18.dp))
            Text("NurVakti", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = GoldLight)
            Text(
                t("Namaz vakitleri, Kur'an-ı Kerim ve günlük ibadet arkadaşınız"),
                style = MaterialTheme.typography.bodyMedium, color = SoftGrey, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(32.dp))

            Benefit(Icons.Default.CloudDone, t("Kayıtlarınız Google Drive'da güvende"), t("Namaz takibi, zikir, kaza ve hatim yedeklenir."))
            Benefit(Icons.Default.PhonelinkSetup, t("Telefon değişse de kaybolmaz"), t("Yeni cihazda giriş yapın, her şey geri gelsin."))
            Benefit(Icons.Default.WorkspacePremium, t("Pro üyeliğiniz hesabınızda"), t("Satın aldığınız Pro her cihazda açılır."))

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onGoogle,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("login_google"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = EmeraldDarkest)
            ) {
                if (isBusy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = EmeraldDarkest)
                else {
                    Text("G", fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(10.dp))
                    Text(t("Google ile devam et"), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onGuest,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("login_guest"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CreamWhite)
            ) { Text(t("Hesapsız devam et")) }
            Spacer(Modifier.height(12.dp))
            Text(
                t("Hesapsız kullanımda tüm özellikler açıktır; kayıtlarınız yalnızca bu telefonda tutulur. İstediğiniz zaman Profil'den giriş yapabilirsiniz."),
                style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center
            )
            TextButton(onClick = onOpenPrivacy) { Text(t("Gizlilik Politikası"), color = GoldMuted) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Benefit(icon: ImageVector, title: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(12.dp), color = GoldChampagne.copy(alpha = 0.14f)) {
            Icon(icon, contentDescription = null, tint = GoldChampagne, modifier = Modifier.padding(10.dp).size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = CreamWhite)
            Text(text, style = MaterialTheme.typography.bodySmall, color = SoftGrey)
        }
    }
}
