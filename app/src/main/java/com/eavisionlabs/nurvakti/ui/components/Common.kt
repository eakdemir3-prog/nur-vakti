package com.eavisionlabs.nurvakti.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.ui.DayStat
import com.eavisionlabs.nurvakti.ui.theme.*

/** Geri tuşlu alt ekran iskeleti */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, color = GoldLight, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("Geri"), tint = GoldChampagne)
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDarkest)
            )
        },
        containerColor = EmeraldDarkest,
        content = content
    )
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldChampagne, letterSpacing = 0.5.sp),
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun NurCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(GoldChampagne.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        } else if (onClick != null) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ToggleRow(title: String, subtitle: String? = null, icon: ImageVector? = null, checked: Boolean, onChange: (Boolean) -> Unit) {
    SettingRow(title = title, subtitle = subtitle, icon = icon, onClick = { onChange(!checked) }) {
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDarkest, checkedTrackColor = GoldChampagne)
        )
    }
}

/** Yatay seçenek çipleri */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChoiceChips(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = value == selected,
                onClick = { onSelect(value) },
                label = { Text(label, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldChampagne,
                    selectedLabelColor = EmeraldDarkest
                )
            )
        }
    }
}

/** Basit çubuk grafik */
@Composable
fun BarChart(data: List<DayStat>, maxValue: Int, modifier: Modifier = Modifier, height: Int = 120, showValues: Boolean = true) {
    val max = maxOf(maxValue, data.maxOfOrNull { it.value } ?: 1, 1)
    Row(
        modifier = modifier.fillMaxWidth().height(height.dp),
        horizontalArrangement = Arrangement.spacedBy(if (data.size > 10) 2.dp else 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { d ->
            Column(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                if (showValues && data.size <= 10) {
                    Text(d.value.toString(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                val fraction = (d.value.toFloat() / max).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = (fraction * 0.78f).coerceAtLeast(0.02f))
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (d.isToday) GoldChampagne else GoldChampagne.copy(alpha = 0.45f))
                )
                if (data.size <= 10) {
                    Text(
                        d.label, fontSize = 10.sp, color = if (d.isToday) GoldChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ProBadge() {
    Surface(shape = RoundedCornerShape(6.dp), color = GoldChampagne) {
        Text(
            "PRO", modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldDarkest
        )
    }
}

@Composable
fun StatPill(value: String, label: String, modifier: Modifier = Modifier, accent: Color = GoldChampagne) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = accent.copy(alpha = 0.12f)) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = accent, maxLines = 1)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, textAlign = TextAlign.Center)
        }
    }
}
