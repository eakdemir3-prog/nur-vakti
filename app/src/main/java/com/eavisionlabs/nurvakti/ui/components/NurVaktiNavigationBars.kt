package com.eavisionlabs.nurvakti.ui.components

import com.eavisionlabs.nurvakti.i18n.t
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.model.City
import com.eavisionlabs.nurvakti.ui.AppScreen

/**
 * Üst çubuk: logo + şehir (tek satır, gerekirse kısaltılır) + Pro + Ayarlar.
 * Büyük yazı boyutlarında da taşmaması için başlık yazısı yer kalmadığında gizlenir.
 */
@Composable
fun NurVaktiTopBar(
    selectedCity: City,
    onCityClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOpenPremium: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(60.dp)
                .padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.nurvakti_logo),
                contentDescription = t("NurVakti Logo"),
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Surface(
                onClick = onCityClick,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("topbar_city_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = t("Şehir Seç"),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedCity.displayName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            }
            IconButton(onClick = onOpenPremium, modifier = Modifier.testTag("topbar_pro_button")) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = t("NurVakti Pro"), tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onSettingsClick, modifier = Modifier.testTag("topbar_settings_button")) {
                Icon(Icons.Outlined.Settings, contentDescription = t("Ayarlar"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun NurVaktiBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("bottom_nav_bar")
    ) {
        val navItems = listOf(
            Triple(AppScreen.HOME, "Ana Sayfa", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
            Triple(AppScreen.QURAN, "Kur'an", Pair(Icons.Filled.MenuBook, Icons.Outlined.MenuBook)),
            Triple(AppScreen.PRAYER_TIMES, "Vakitler", Pair(Icons.Filled.AccessTimeFilled, Icons.Outlined.AccessTime)),
            Triple(AppScreen.EXPLORE, "Keşfet", Pair(Icons.Filled.Explore, Icons.Outlined.Explore)),
            Triple(AppScreen.PROFILE, "Profil", Pair(Icons.Filled.Person, Icons.Outlined.Person))
        )

        navItems.forEach { (screen, label, icons) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = t(label),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    // Tek satır; büyük yazı boyutunda da kırılmaması için yazı kendini küçültür
                    val base = MaterialTheme.typography.labelSmall
                    val density = LocalDensity.current
                    val size = (12f / density.fontScale.coerceAtLeast(1f) * 1.1f).coerceIn(9f, 12f)
                    Text(
                        text = t(label),
                        style = base.copy(
                            fontSize = size.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}
