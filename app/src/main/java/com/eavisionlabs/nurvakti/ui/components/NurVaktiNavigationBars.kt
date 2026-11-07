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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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

/**
 * ZihinPark tarzı yüzen alt menü: seçili sekme ikon + yazılı altın bir hapa dönüşür,
 * diğerleri yalnızca ikon kalır. Genişleme yaylı animasyonla olur. Renkler temadan gelir.
 */
@Composable
fun NurVaktiBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Triple(AppScreen.HOME, "Ana Sayfa", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
        Triple(AppScreen.QURAN, "Kur'an", Pair(Icons.Filled.MenuBook, Icons.Outlined.MenuBook)),
        Triple(AppScreen.PRAYER_TIMES, "Vakitler", Pair(Icons.Filled.AccessTimeFilled, Icons.Outlined.AccessTime)),
        Triple(AppScreen.EXPLORE, "Keşfet", Pair(Icons.Filled.GridView, Icons.Outlined.GridView)),
        Triple(AppScreen.PROFILE, "Profil", Pair(Icons.Filled.Person, Icons.Outlined.Person))
    )
    val cs = MaterialTheme.colorScheme
    val gold = cs.primary
    // Koyu temada açığa, açık temada (yazı beyaz) koyuya doğru geçiş: yazı her durumda okunur
    val lightContent = cs.onPrimary == androidx.compose.ui.graphics.Color.White
    val goldLight = androidx.compose.ui.graphics.lerp(
        gold, if (lightContent) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White,
        if (lightContent) 0.18f else 0.35f
    )
    val barShape = RoundedCornerShape(34.dp)
    // Yazı boyutu kullanıcı ayarından bağımsız: büyük yazıda da bar taşmaz
    val density = LocalDensity.current
    val labelSize = (14f / density.fontScale.coerceAtLeast(1f)).sp

    Box(
        modifier
            .fillMaxWidth()
            .background(cs.background)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("bottom_nav_bar")
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(14.dp, barShape, ambientColor = gold.copy(alpha = 0.35f), spotColor = gold.copy(alpha = 0.35f))
                .clip(barShape)
                .background(cs.surface)
                .border(1.dp, gold.copy(alpha = 0.45f), barShape)
                .padding(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { (screen, label, icons) ->
                val isSel = currentScreen == screen
                val weight by animateFloatAsState(
                    if (isSel) 2.6f else 1f,
                    spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow),
                    label = "w_$label"
                )
                val pill by animateFloatAsState(if (isSel) 1f else 0f, tween(260), label = "p_$label")
                val iconScale by animateFloatAsState(if (isSel) 1.08f else 1f, spring(dampingRatio = 0.45f), label = "s_$label")
                val press = remember { MutableInteractionSource() }
                val pressed by press.collectIsPressedAsState()
                val pressScale by animateFloatAsState(if (pressed) 0.9f else 1f, spring(dampingRatio = 0.5f), label = "b_$label")
                val content = androidx.compose.ui.graphics.lerp(cs.onSurfaceVariant, cs.onPrimary, pill)
                Box(
                    Modifier
                        .weight(weight)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp)
                        .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(listOf(gold.copy(alpha = pill), goldLight.copy(alpha = pill)))
                        )
                        .clickable(interactionSource = press, indication = null) { if (!isSel) onNavigate(screen) }
                        .semantics { role = Role.Tab; selected = isSel }
                        .testTag("nav_item_${screen.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(
                            if (isSel) icons.first else icons.second,
                            contentDescription = t(label),
                            tint = content,
                            modifier = Modifier.size(24.dp).graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                        )
                        AnimatedVisibility(
                            visible = isSel,
                            enter = expandHorizontally(spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(tween(220, delayMillis = 60)),
                            exit = shrinkHorizontally(tween(180)) + fadeOut(tween(120))
                        ) {
                            Text(
                                t(label),
                                modifier = Modifier.padding(start = 8.dp),
                                fontSize = labelSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = content,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }
    }
}
