package com.eavisionlabs.nurvakti.ui.components

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.i18n.t
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

private val Emerald = Color(0xFF0B211C)
private val EmeraldGlow = Color(0xFF1B5444)
private val GoldHi = Color(0xFFF6E3A6)
private val GoldMid = Color(0xFFD8B670)
private val GoldLo = Color(0xFFA47E33)
private val Smooth = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

/**
 * Açılış animasyonu (simgedeki Hilal + Saat):
 * hilal dönerek belirir → saat noktaları sırayla yanar → akrep ve yelkovan o anki saate döner →
 * "NurVakti" yazısı gelir → ekran uygulamaya yumuşakça geçer. Dokununca atlanır.
 * Sistemde animasyonlar kapalıysa hiç gösterilmez.
 */
@Composable
fun IntroAnimation(onFinished: () -> Unit) {
    val context = LocalContext.current
    val animationsOff = remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }
    if (animationsOff) {
        LaunchedEffect(Unit) { onFinished() }
        return
    }

    val crescent = remember { Animatable(0f) }
    val dots = remember { Animatable(0f) }
    val hands = remember { Animatable(0f) }
    val glow = remember { Animatable(0f) }
    val title = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    var finished by remember { mutableStateOf(false) }

    val now = remember { Calendar.getInstance() }
    // Hedef açılar (saat 12 yönü = 0°)
    val minuteTarget = remember { now.get(Calendar.MINUTE) * 6f }
    val hourTarget = remember { (now.get(Calendar.HOUR) + now.get(Calendar.MINUTE) / 60f) * 30f }

    fun finish() {
        if (!finished) { finished = true; onFinished() }
    }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch { glow.animateTo(1f, tween(900, easing = LinearEasing)) }
            launch { crescent.animateTo(1f, tween(750, easing = Smooth)) }
            launch { delay(300); dots.animateTo(1f, tween(650, easing = LinearEasing)) }
            launch { delay(450); hands.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
            launch { delay(850); title.animateTo(1f, tween(600, easing = Smooth)) }
        }
        delay(350)
        exit.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        finish()
    }

    val exitP = exit.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - exitP }
            .background(
                Brush.radialGradient(
                    listOf(EmeraldGlow.copy(alpha = 0.35f + 0.65f * glow.value), Emerald),
                    radius = 1400f
                )
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { finish() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        val s = 1f + 0.08f * exitP
                        scaleX = s; scaleY = s
                    }
            ) {
                drawIntroIcon(
                    crescentP = crescent.value,
                    dotsP = dots.value,
                    handsP = hands.value,
                    glowP = glow.value,
                    hourAngle = hourTarget,
                    minuteAngle = minuteTarget
                )
            }
            Spacer(Modifier.height(18.dp))
            val tp = title.value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = tp
                    translationY = (1f - tp) * 24.dp.toPx()
                }
            ) {
                Text(
                    "NurVakti",
                    color = GoldHi,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t("Namaz Vakitleri & Kur'an-ı Kerim"),
                    color = Color(0xFFA9B5AF),
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/** Simgenin çizimi (1080 birimlik tasarım alanından, görünen 180..900 bölgesi çizilir). */
private fun DrawScope.drawIntroIcon(
    crescentP: Float,
    dotsP: Float,
    handsP: Float,
    glowP: Float,
    hourAngle: Float,
    minuteAngle: Float
) {
    val k = size.width / 720f
    fun p(x: Float, y: Float) = Offset((x - 180f) * k, (y - 180f) * k)
    val gold = Brush.linearGradient(listOf(GoldHi, GoldMid, GoldLo), start = p(250f, 250f), end = p(830f, 830f))

    // Arka ışıltı
    drawCircle(
        brush = Brush.radialGradient(
            listOf(GoldHi.copy(alpha = 0.32f * glowP), Color.Transparent),
            center = p(540f, 540f), radius = 330f * k
        ),
        radius = 330f * k, center = p(540f, 540f)
    )

    // Hilal: büyük daire − kaydırılmış daire
    if (crescentP > 0f) {
        val outer = Path().apply { addOval(androidx.compose.ui.geometry.Rect(p(520f, 540f), 255f * k)) }
        val inner = Path().apply { addOval(androidx.compose.ui.geometry.Rect(p(625f, 502f), 220f * k)) }
        val moon = Path().apply { op(outer, inner, PathOperation.Difference) }
        val s = 0.55f + 0.45f * crescentP
        withTransform({
            rotate(degrees = -55f * (1f - crescentP), pivot = p(540f, 540f))
            scale(s, s, pivot = p(540f, 540f))
        }) {
            drawPath(moon, brush = gold, alpha = crescentP.coerceIn(0f, 1f))
        }
    }

    // Saat noktaları (hilalin kapattığı sol taraf hariç), sırayla belirir
    val center = p(620f, 510f)
    val visible = (0 until 12).filter { it !in 5..7 }
    visible.forEachIndexed { i, a ->
        val start = i / visible.size.toFloat() * 0.75f
        val local = ((dotsP - start) / 0.25f).coerceIn(0f, 1f)
        if (local > 0f) {
            val ang = Math.toRadians(a * 30.0)
            val c = Offset(center.x + (150f * k * cos(ang)).toFloat(), center.y + (150f * k * sin(ang)).toFloat())
            val r = (if (a % 3 == 0) 12f else 7f) * k * (0.4f + 0.6f * local)
            drawCircle(brush = gold, radius = r, center = c, alpha = local)
        }
    }

    // Akrep ve yelkovan: 12 yönünden o anki saate döner
    if (handsP > 0f) {
        val a = handsP.coerceIn(0f, 1f)
        val stroke = 26f * k
        rotate(degrees = minuteAngle * handsP, pivot = center) {
            drawLine(gold, center, Offset(center.x, center.y - 115f * k * a), strokeWidth = stroke, cap = StrokeCap.Round, alpha = a)
        }
        rotate(degrees = hourAngle * handsP, pivot = center) {
            drawLine(gold, center, Offset(center.x, center.y - 80f * k * a), strokeWidth = stroke, cap = StrokeCap.Round, alpha = a)
        }
        drawCircle(brush = gold, radius = 26f * k, center = center, alpha = a)
    }
}
