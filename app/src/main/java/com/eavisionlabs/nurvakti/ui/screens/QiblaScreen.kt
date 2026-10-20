package com.eavisionlabs.nurvakti.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.UiState
import com.eavisionlabs.nurvakti.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    uiState: UiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val compass = uiState.compassData
    val smoothAzimuth by animateFloatAsState(targetValue = compass.currentAzimuthDegrees, label = "azimuth")
    val smoothQiblaBearing = compass.qiblaBearingDegrees
    val isAligned = compass.isAlignedWithQibla

    val dialRingColor by animateColorAsState(
        targetValue = if (isAligned) GoldChampagne else EmeraldBorder,
        label = "ring_color"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kıble Bulucu (Pusula)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("qibla_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // City & Qibla Angle header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${uiState.selectedCity.displayName}: Kâbe Açısı ${smoothQiblaBearing.roundToInt()}°",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isAligned) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldChampagne.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kıble Yönündesiniz!",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldChampagne
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Telefonunuzu Kâbe işaretine doğru çeviriniz",
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftGrey)
                    )
                }
            }

            // Big Luxury Compass Dial Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(EmeraldCard),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f - 16.dp.toPx()

                    // Outer circle ring
                    drawCircle(
                        color = dialRingColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = if (isAligned) 4.dp.toPx() else 2.dp.toPx())
                    )

                    // Rotating compass dial
                    rotate(degrees = -smoothAzimuth, pivot = center) {
                        // Degree tick marks
                        for (i in 0 until 360 step 30) {
                            rotate(degrees = i.toFloat(), pivot = center) {
                                val tickLength = if (i % 90 == 0) 14.dp.toPx() else 8.dp.toPx()
                                val tickColor = if (i % 90 == 0) GoldChampagne else SoftGrey.copy(alpha = 0.5f)
                                drawLine(
                                    color = tickColor,
                                    start = Offset(center.x, center.y - radius),
                                    end = Offset(center.x, center.y - radius + tickLength),
                                    strokeWidth = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                                )
                            }
                        }

                        // Kaaba Target Arrow (at bearing angle)
                        rotate(degrees = smoothQiblaBearing, pivot = center) {
                            val path = Path().apply {
                                moveTo(center.x, center.y - radius + 10.dp.toPx())
                                lineTo(center.x - 12.dp.toPx(), center.y - radius + 36.dp.toPx())
                                lineTo(center.x + 12.dp.toPx(), center.y - radius + 36.dp.toPx())
                                close()
                            }
                            drawPath(path = path, color = GoldChampagne)
                        }
                    }

                    // North Needle (Red arrow pointing to device relative North)
                    rotate(degrees = -smoothAzimuth, pivot = center) {
                        val northPath = Path().apply {
                            moveTo(center.x, center.y - 45.dp.toPx())
                            lineTo(center.x - 7.dp.toPx(), center.y)
                            lineTo(center.x + 7.dp.toPx(), center.y)
                            close()
                        }
                        drawPath(path = northPath, color = Color(0xFFE53935))

                        val southPath = Path().apply {
                            moveTo(center.x, center.y + 45.dp.toPx())
                            lineTo(center.x - 7.dp.toPx(), center.y)
                            lineTo(center.x + 7.dp.toPx(), center.y)
                            close()
                        }
                        drawPath(path = southPath, color = Color(0xFFB0BEC5))
                    }

                    // Center Pivot Pin
                    drawCircle(color = GoldChampagne, radius = 6.dp.toPx(), center = center)
                }

                // Center Kaaba Icon Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(EmeraldDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Kâbe",
                        tint = GoldChampagne,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Calibration & Sensor Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(18.dp),
                color = EmeraldCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = if (compass.hasCompassSensor) GoldChampagne else Color(0xFFFFB74D),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (compass.hasCompassSensor) "Manyetik Sensör Aktif" else "Simülasyon / Sensör Yok",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CreamWhite
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Doğru sonuç için telefonunuzu metal eşyalardan uzak tutun ve havada '8' çizecek şekilde hareket ettirerek pusulayı kalibre ediniz.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
