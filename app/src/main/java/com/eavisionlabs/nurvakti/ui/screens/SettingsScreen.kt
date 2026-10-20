package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eavisionlabs.nurvakti.BuildConfig
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.model.NotificationPreference
import com.eavisionlabs.nurvakti.model.PrayerType
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.MainViewModel
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.components.*
import com.eavisionlabs.nurvakti.ui.theme.*

fun notificationPrefLabel(p: NotificationPreference): String = when (p) {
    NotificationPreference.DISABLED -> t("Kapalı")
    NotificationPreference.AZAN -> t("Ezan")
    NotificationPreference.NOTIFICATION_SOUND -> t("Sesli")
    NotificationPreference.SILENT -> t("Sessiz")
    NotificationPreference.VIBRATE_ONLY -> t("Titreşim")
}

@Composable
fun SettingsScreen(
    uiState: UiState,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onRequestNotifications: () -> Unit,
    onRequestExactAlarm: () -> Unit,
    onOpenSystemNotificationSettings: () -> Unit,
    onPickAdhan: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenCityPicker: () -> Unit,
    onAutoDetectLocation: () -> Unit
) {
    var editingPrayer by remember { mutableStateOf<PrayerType?>(null) }
    var showAdjustments by remember { mutableStateOf(false) }
    var confirmImport by remember { mutableStateOf(false) }

    SubScreenScaffold(title = t("Ayarlar"), onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // --- Uyarılar -------------------------------------------------
            if (!uiState.notificationsAllowed || !uiState.exactAlarmAllowed) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                t("Vakit bildirimleri için izin gerekiyor"),
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.height(6.dp))
                            if (!uiState.notificationsAllowed) {
                                Text(t("Bildirim izni kapalı; ezan ve hatırlatmalar gösterilemez."), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                Button(onClick = onRequestNotifications) { Text(t("Bildirimlere izin ver")) }
                            }
                            if (!uiState.exactAlarmAllowed) {
                                Spacer(Modifier.height(6.dp))
                                Text(t("Tam zamanlı alarm izni kapalı; bildirimler birkaç dakika gecikebilir."), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                Button(onClick = onRequestExactAlarm) { Text(t("Tam zamanlı alarma izin ver")) }
                            }
                        }
                    }
                }
            }

            // --- Konum ---------------------------------------------------
            item { SectionTitle(t("Konum")) }
            item {
                NurCard {
                    SettingRow(
                        title = uiState.selectedCity.displayName,
                        subtitle = uiState.selectedCity.country,
                        icon = Icons.Default.LocationOn,
                        onClick = onOpenCityPicker
                    )
                    SettingRow(
                        title = t("GPS ile konumumu bul"),
                        subtitle = if (uiState.isLocating) t("Konum aranıyor…") else t("Bulunduğunuz yerin vakitleri"),
                        icon = Icons.Default.MyLocation,
                        onClick = onAutoDetectLocation
                    )
                }
            }

            // --- Vakit bildirimleri --------------------------------------
            item { SectionTitle(t("Vakit Bildirimleri")) }
            item {
                NurCard {
                    Text(t("Tümüne uygula"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    ChoiceChips(
                        options = NotificationPreference.entries.map { it to notificationPrefLabel(it) },
                        selected = uiState.notificationPrefs.filterKeys { it != PrayerType.GUNES }.values.distinct().singleOrNull()
                            ?: NotificationPreference.entries.first(),
                        onSelect = { viewModel.setAllNotificationPrefs(it) }
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    PrayerType.entries.forEach { type ->
                        val pref = uiState.notificationPrefs[type] ?: NotificationPreference.DISABLED
                        SettingRow(
                            title = t(type.titleTr),
                            subtitle = notificationPrefLabel(pref),
                            icon = when (pref) {
                                NotificationPreference.DISABLED -> Icons.Default.NotificationsOff
                                NotificationPreference.AZAN -> Icons.Default.Campaign
                                NotificationPreference.SILENT -> Icons.Default.NotificationsNone
                                NotificationPreference.VIBRATE_ONLY -> Icons.Default.Vibration
                                else -> Icons.Default.NotificationsActive
                            },
                            onClick = { editingPrayer = type }
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                NurCard {
                    Text(t("Vakitten önce hatırlat"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(6.dp))
                    ChoiceChips(
                        options = listOf(0 to t("Kapalı"), 5 to "5 dk", 10 to "10 dk", 15 to "15 dk", 30 to "30 dk", 45 to "45 dk"),
                        selected = uiState.preReminderMinutes,
                        onSelect = { viewModel.setPreReminderMinutes(it) }
                    )
                    Spacer(Modifier.height(8.dp))
                    ToggleRow(
                        title = t("Bildirim çubuğunda geri sayım"),
                        subtitle = t("Sıradaki vakte kalan süre sürekli görünür"),
                        icon = Icons.Default.Timer,
                        checked = uiState.ongoingCountdown,
                        onChange = { viewModel.setOngoingCountdown(it) }
                    )
                    SettingRow(
                        title = t("Ezan sesi"),
                        subtitle = uiState.adhanSoundName ?: t("Cihazın alarm sesi (telefonunuzdan bir ezan kaydı seçebilirsiniz)"),
                        icon = Icons.Default.MusicNote,
                        onClick = onPickAdhan
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (uiState.adhanSoundName != null) {
                            TextButton(onClick = { viewModel.setAdhanSound(null, null) }) { Text(t("Varsayılana dön")) }
                        }
                        TextButton(onClick = { viewModel.previewAdhan() }) { Text(t("Dinle")) }
                    }
                    SettingRow(
                        title = t("Sistem bildirim ayarları"),
                        subtitle = t("Kanal sesleri ve rahatsız etmeyin istisnaları"),
                        icon = Icons.Default.Tune,
                        onClick = onOpenSystemNotificationSettings
                    )
                }
            }

            // --- Diğer hatırlatmalar --------------------------------------
            item { SectionTitle(t("Diğer Hatırlatmalar")) }
            item {
                NurCard {
                    ToggleRow(t("Cuma hatırlatması"), t("Cuma namazından 1 saat önce"), Icons.Default.Mosque, uiState.fridayReminder) { viewModel.setFridayReminder(it) }
                    ToggleRow(t("Kandil ve dini günler"), t("Kandil gecesi ve bayram sabahı"), Icons.Default.NightsStay, uiState.kandilReminder) { viewModel.setKandilReminder(it) }
                    ToggleRow(t("Günün ayeti"), t("Her sabah bir ayet-i kerime"), Icons.Default.AutoStories, uiState.dailyVerseNotification) { viewModel.setDailyVerseNotification(it) }
                }
            }

            // --- Vakit hesaplama ------------------------------------------
            item { SectionTitle(t("Vakit Hesaplama")) }
            item {
                NurCard {
                    SettingRow(
                        title = t("Hesaplama yöntemi"),
                        subtitle = t("Diyanet İşleri Başkanlığı (İmsak 18°, Yatsı 17°, temkinli)"),
                        icon = Icons.Default.Calculate
                    )
                    SettingRow(
                        title = t("Vakitlere dakika ekle/çıkar"),
                        subtitle = if (uiState.prayerAdjustments.all { it == 0 }) t("Düzeltme yok")
                        else uiState.prayerAdjustments.mapIndexed { i, m -> if (m == 0) null else "${t(PrayerType.entries[i].titleTr)} ${if (m > 0) "+" else ""}$m" }.filterNotNull().joinToString(", "),
                        icon = Icons.Default.Schedule,
                        onClick = { showAdjustments = true }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(t("Hicri takvim düzeltmesi"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        t("Hicri tarih Diyanet takviminden farklıysa gün ekleyip çıkarın."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    ChoiceChips(
                        options = listOf(-2 to "−2", -1 to "−1", 0 to "0", 1 to "+1", 2 to "+2"),
                        selected = uiState.hijriOffset,
                        onSelect = { viewModel.setHijriOffset(it) }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(uiState.prayerSchedule?.hijriDateFormatted ?: "", color = GoldChampagne, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // --- Görünüm ---------------------------------------------------
            item { SectionTitle(t("Görünüm")) }
            item {
                NurCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(t("Tema"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(8.dp))
                        if (!uiState.isPremiumActive) ProBadge()
                    }
                    Spacer(Modifier.height(6.dp))
                    ChoiceChips(
                        options = listOf(
                            "system" to t("Sistem"), "dark" to t("Gece Zümrüdü"), "light" to t("Fildişi (Açık)"), "kiswa" to t("Kâbe Örtüsü")
                        ),
                        selected = uiState.themeMode,
                        onSelect = { viewModel.setThemeMode(it) }
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(t("Yazı boyutu"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    ChoiceChips(
                        options = listOf(0.9f to t("Küçük"), 1.0f to t("Normal"), 1.15f to t("Büyük"), 1.3f to t("Çok büyük")),
                        selected = listOf(0.9f, 1.0f, 1.15f, 1.3f).minByOrNull { kotlin.math.abs(it - uiState.fontScale) } ?: 1.0f,
                        onSelect = { viewModel.setFontScale(it) }
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(t("Dil"), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    ChoiceChips(
                        options = listOf("tr" to "Türkçe", "en" to "English", "ar" to "العربية"),
                        selected = uiState.language,
                        onSelect = { viewModel.setLanguage(it) }
                    )
                    if (uiState.language != "tr") {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            t("Dualar ve açıklamalar Türkçe kalır; Kur'an meali İngilizce gösterilir."),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- Yedekleme --------------------------------------------------
            item { SectionTitle(t("Yedekleme")) }
            item {
                NurCard {
                    Text(
                        t("Ayarlarınızı, namaz ve zikir kayıtlarınızı, yer imlerinizi ve kaza sayılarınızı bir dosyaya kaydedip yeni telefonda geri yükleyebilirsiniz."),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    SettingRow(t("Yedeği dışa aktar"), t("JSON dosyası olarak kaydet"), Icons.Default.Upload, onClick = onExport)
                    SettingRow(t("Yedeği geri yükle"), t("Daha önce kaydedilen dosyayı seç"), Icons.Default.Download, onClick = { confirmImport = true })
                }
            }

            // --- Hakkında ---------------------------------------------------
            item { SectionTitle(t("Hakkında")) }
            item {
                NurCard {
                    SettingRow(t("NurVakti Pro"), if (uiState.isPremiumActive) t("Aktif") else t("Ayrıcalıkları keşfedin"), Icons.Default.WorkspacePremium,
                        onClick = { viewModel.navigateTo(AppScreen.PREMIUM) })
                    SettingRow(t("Gizlilik Politikası"), null, Icons.Default.PrivacyTip, onClick = onOpenPrivacy)
                    SettingRow(
                        t("Kaynaklar"),
                        t("Meal: Diyanet İşleri Başkanlığı (tanzil.net) • Arapça metin: Kur'an Ansiklopedisi • Yazı tipi: Amiri Quran (SIL OFL) • Ses: mp3quran.net, everyayah.com"),
                        Icons.Default.Info
                    )
                    Text(
                        "NurVakti ${BuildConfig.VERSION_NAME} • EA Vision Labs",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }
            }
        }
    }

    // Vakit başına bildirim seçimi
    editingPrayer?.let { type ->
        AlertDialog(
            onDismissRequest = { editingPrayer = null },
            title = { Text(String.format(t("%s bildirimi"), t(type.titleTr))) },
            text = {
                Column {
                    NotificationPreference.entries.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.notificationPrefs[type] == p,
                                onClick = { viewModel.setNotificationPref(type, p); editingPrayer = null }
                            )
                            Text(notificationPrefLabel(p))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { editingPrayer = null }) { Text(t("Kapat")) } }
        )
    }

    if (showAdjustments) {
        AlertDialog(
            onDismissRequest = { showAdjustments = false },
            title = { Text(t("Vakit düzeltmeleri (dakika)")) },
            text = {
                Column {
                    PrayerType.entries.forEachIndexed { i, type ->
                        val v = uiState.prayerAdjustments.getOrElse(i) { 0 }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(t(type.titleTr), modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.setPrayerAdjustment(i, v - 1) }) { Icon(Icons.Default.Remove, contentDescription = "−") }
                            Text(if (v > 0) "+$v" else "$v", modifier = Modifier.width(36.dp), fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.setPrayerAdjustment(i, v + 1) }) { Icon(Icons.Default.Add, contentDescription = "+") }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAdjustments = false }) { Text(t("Tamam")) } },
            dismissButton = {
                TextButton(onClick = { (0 until 6).forEach { viewModel.setPrayerAdjustment(it, 0) } }) { Text(t("Sıfırla")) }
            }
        )
    }

    if (confirmImport) {
        AlertDialog(
            onDismissRequest = { confirmImport = false },
            title = { Text(t("Yedeği geri yükle")) },
            text = { Text(t("Mevcut ayarlarınız ve kayıtlarınız yedekteki bilgilerle değiştirilecek. Devam edilsin mi?")) },
            confirmButton = { Button(onClick = { confirmImport = false; onImport() }) { Text(t("Devam")) } },
            dismissButton = { TextButton(onClick = { confirmImport = false }) { Text(t("Vazgeç")) } }
        )
    }
}
