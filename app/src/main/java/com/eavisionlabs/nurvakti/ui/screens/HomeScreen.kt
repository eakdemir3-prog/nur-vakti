package com.eavisionlabs.nurvakti.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.model.PrayerType
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*
import java.util.Locale
import com.eavisionlabs.nurvakti.i18n.t

@Composable
fun HomeScreen(
    uiState: UiState,
    onNavigate: (AppScreen) -> Unit,
    onTogglePrayerDone: (String) -> Unit,
    onOpenCityPicker: () -> Unit,
    onAutoDetectLocation: () -> Unit = {},
    onOpenPremium: () -> Unit = {},
    onOpenAyah: (Int, Int) -> Unit = { _, _ -> },
    onContinueReading: () -> Unit = {},
    onShare: (String) -> Unit = {},
    onFindMosques: () -> Unit = {},
    onEnableNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val schedule = uiState.prayerSchedule
    val todayEvent = uiState.religiousDays.firstOrNull { it.daysRemaining == 0 }
    val isFriday = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Mosque Atmosphere Banner with Greeting
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nurvakti_hero_1792354904566),
                    contentDescription = t("Manevi Cami Manzarası"),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay ensuring clear text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF0B211C).copy(alpha = 0.5f),
                                    Color(0xFF0B211C).copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = t("Esselâmu Aleyküm ve Rahmetullâh"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = t("Bugün kalbine ve vaktine huzurla başla."),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CreamWhite.copy(alpha = 0.9f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. Grand Prayer Card with Live Countdown
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("grand_prayer_card"),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Top: Date & City
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = schedule?.gregorianDateFormatted ?: "18 Ekim 2026 Pazar",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = schedule?.hijriDateFormatted ?: "Hicri Tarih",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Instant Auto-Detect GPS Button
                            IconButton(
                                onClick = onAutoDetectLocation,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("auto_detect_gps_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = t("Konumumu Otomatik Bul"),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                onClick = onOpenCityPicker,
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = uiState.selectedCity.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Center: Next Prayer & Countdown Timer (Protected against wrapping / overflow)
                    val next = schedule?.nextPrayer
                    val remainingMs = schedule?.remainingMillisToNext ?: 0L
                    val totalSecs = (remainingMs / 1000).coerceAtLeast(0)
                    val hours = totalSecs / 3600
                    val minutes = (totalSecs % 3600) / 60
                    val seconds = totalSecs % 60
                    val countdownStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = t("Sıradaki:"),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = next?.type?.titleTr ?: "İmsak",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )
                            }

                            Text(
                                text = "Saat: ${next?.timeFormatted ?: "--:--"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1
                            )
                        }

                        // Countdown Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = t("Kalan Süre"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = countdownStr,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 6 Prayer Times Horizontal Strip (Equally weighted columns to prevent horizontal shifting)
                    val times = schedule?.prayerTimes ?: emptyList()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        times.forEach { prayer ->
                            val isCurrent = prayer.isCurrent
                            val isNext = prayer.isNext
                            val isPassed = prayer.isPassed

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isNext -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isNext) 1.dp else 0.dp,
                                        color = if (isNext) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = prayer.type.titleTr,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = prayer.timeFormatted,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isNext) MaterialTheme.colorScheme.primary else if (isPassed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2b. Bildirim izni uyarısı
        if (!uiState.notificationsAllowed) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).clickable { onEnableNotifications() },
                    shape = RoundedCornerShape(16.dp),
                    color = GoldChampagne.copy(alpha = 0.14f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldChampagne)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(t("Vakit bildirimlerini açın"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(t("Ezan ve hatırlatmalar için bildirim iznine ihtiyaç var."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // 2c. Cuma / dini gün tebriği
        if (isFriday || todayEvent != null) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                val title = todayEvent?.let { t(it.title) } ?: t("Hayırlı Cumalar")
                val greetings = com.eavisionlabs.nurvakti.data.DailyContent.greetings(todayEvent?.title)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = GoldChampagne)
                            Spacer(Modifier.width(8.dp))
                            Text(title, fontWeight = FontWeight.Bold, color = GoldLight, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(greetings.first(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            greetings.forEachIndexed { i, g ->
                                AssistChip(
                                    onClick = { onShare("$g\n\n— NurVakti") },
                                    label = { Text(String.format(t("Mesaj %d"), i + 1)) },
                                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Action Hub
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = t("Hızlı İbadet ve Araçlar"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val quickActions = listOf(
                QuickActionItem(t("Namaz Takibi"), Icons.Default.TaskAlt, Color(0xFF26C6DA), AppScreen.PRAYER_TRACKER),
                QuickActionItem(t("Kıble Bulucu"), Icons.Default.Explore, GoldChampagne, AppScreen.QIBLA),
                QuickActionItem(t("Yakındaki Camiler"), Icons.Default.Mosque, Color(0xFF8D6E63), AppScreen.HOME),
                QuickActionItem(t("Kur'an Oku"), Icons.Default.MenuBook, Color(0xFF42A5F5), AppScreen.QURAN),
                QuickActionItem(t("Dijital Tesbih"), Icons.Default.Fingerprint, Color(0xFF66BB6A), AppScreen.TASBIH),
                QuickActionItem(t("Mübarek Dualar"), Icons.Default.AutoAwesome, Color(0xFFFFA726), AppScreen.DUAS),
                QuickActionItem(t("Esmaül Hüsna"), Icons.Default.Star, Color(0xFFAB47BC), AppScreen.ASMAUL_HUSNA),
                QuickActionItem(t("Ramazan & Hatim"), Icons.Default.NightlightRound, GoldLight, AppScreen.RAMAZAN_HATIM),
                QuickActionItem(t("Kaza Namazı"), Icons.Default.FormatListNumbered, Color(0xFFFF7043), AppScreen.KAZA_NAMAZ),
                QuickActionItem(t("Zekât Hesapla"), Icons.Default.Calculate, Color(0xFF26A69A), AppScreen.ZAKAT_CALCULATOR),
                QuickActionItem(t("Aylık Vakitler"), Icons.Default.CalendarMonth, Color(0xFF78909C), AppScreen.PRAYER_TIMES)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(quickActions) { item ->
                    Surface(
                        modifier = Modifier
                            .width(110.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { if (item.icon == Icons.Default.Mosque) onFindMosques() else onNavigate(item.targetScreen) }
                            .testTag("quick_action_${item.title.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(item.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 4. Daily Prayer Checklist
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t("Günün Namaz Takibi"),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                        val count = uiState.completedPrayersToday.size
                        Text(
                            text = "$count / 5 Kılındı",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val dailyFardhs = listOf(
                        "imsak" to "Sabah",
                        "ogle" to "Öğle",
                        "ikindi" to "İkindi",
                        "aksam" to "Akşam",
                        "yatsi" to "Yatsı"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dailyFardhs.forEach { (code, title) ->
                            val isDone = uiState.completedPrayersToday.contains(code)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onTogglePrayerDone(code) }
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                                    .testTag("prayer_check_$code")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = title,
                                        tint = if (isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. NurVakti Pro Discovery Banner (Dedicated Pro Access)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenPremium() }
                    .testTag("home_pro_banner"),
                shape = RoundedCornerShape(20.dp),
                color = if (uiState.isPremiumActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = t("Pro"),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = t("NurVakti Pro"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = if (uiState.isPremiumActive) "AKTİF" else "PRO",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (uiState.isPremiumActive)
                                    t("Pro ayrıcalıklarınız açık")
                                else
                                    t("Kâbe Örtüsü teması, hıfz modu ve aylık analizler"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = t("Detay"),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 6. Günün ayeti, hadisi ve duası
        uiState.verseOfDay?.let { v ->
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DailyCard(
                    icon = Icons.Default.AutoAwesome,
                    title = t("Günün Ayeti"),
                    reference = v.reference,
                    arabic = v.arabic,
                    text = v.text,
                    onClick = { onOpenAyah(v.surah, v.ayah) },
                    onShare = { onShare("${v.arabic}\n\n\"${v.text}\"\n\n(${v.reference} • NurVakti)") }
                )
            }
        }
        uiState.hadithOfDay?.let { h ->
            item {
                Spacer(modifier = Modifier.height(12.dp))
                DailyCard(
                    icon = Icons.Default.FormatQuote,
                    title = t("Günün Hadisi"),
                    reference = h.source,
                    arabic = null,
                    text = h.text,
                    onClick = null,
                    onShare = { onShare("\"${h.text}\"\n(${h.source})\n\n— NurVakti") }
                )
            }
        }
        uiState.duaOfDay?.let { d ->
            item {
                Spacer(modifier = Modifier.height(12.dp))
                DailyCard(
                    icon = Icons.Default.VolunteerActivism,
                    title = t("Günün Duası") + " • " + d.titleTr,
                    reference = d.source,
                    arabic = d.arabicText,
                    text = d.meaningTr,
                    onClick = { onNavigate(AppScreen.DUAS) },
                    onShare = { onShare("${d.titleTr}\n\n${d.arabicText}\n\n${d.pronunciationTr}\n\n${d.meaningTr}\n(${d.source})\n\n— NurVakti") }
                )
            }
        }
    }
}

@Composable
private fun DailyCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    reference: String,
    arabic: String?,
    text: String,
    onClick: (() -> Unit)?,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = t("Paylaş"), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
            if (!arabic.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = arabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = QuranFont, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Right,
                        lineHeight = 34.sp, fontSize = 21.sp, textDirection = androidx.compose.ui.text.style.TextDirection.Rtl
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"$text\"",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(reference, style = MaterialTheme.typography.labelSmall, color = GoldChampagne)
        }
    }
}

private data class QuickActionItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: Color,
    val targetScreen: AppScreen
)
