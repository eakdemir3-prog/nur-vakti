package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            title = "Namaz Vakitlerini Takip Et",
            description = "Bulunduğunuz şehre ve koordinatlara göre hassas Diyanet uyumlu vakitler, geri sayım ve ezan bildirimleri.",
            icon = Icons.Default.AccessTime
        ),
        OnboardingPageData(
            title = "Kur'an-ı Kerim'i Oku ve Dinle",
            description = "Eksiksiz 114 Sure, doğrulanmış Uthmani hattı, Diyanet meali ve Alafasy gibi seçkin kârilerden sesli tilavet.",
            icon = Icons.Default.MenuBook
        ),
        OnboardingPageData(
            title = "Kıbleyi Bul ve İbadetlerini Yaşa",
            description = "Gerçek pusula sensörüyle Kâbe istikameti, dokunsal dijital tesbih, mübarek dualar ve 30 cüz hatim takibi.",
            icon = Icons.Default.Explore
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkest)
            .padding(24.dp)
            .navigationBarsPadding()
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Logo & Brand Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GoldChampagne.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nurvakti_icon_1792354892507),
                    contentDescription = "NurVakti Logo",
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "NurVakti",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "Premium Kur'an ve İslami Yaşam",
                style = MaterialTheme.typography.bodySmall.copy(color = SoftGrey)
            )
        }

        // Pager Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(EmeraldCardElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SoftGrey,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        // Location permission explanation note
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(14.dp),
            color = EmeraldCardElevated
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Konum izni: Vakitleri ve kıbleyi tam hesaplamak içindir. İzin vermezseniz manuel şehir seçimiyle devam edebilirsiniz.",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftGrey, fontSize = 11.sp)
                )
            }
        }

        // Bottom Controls: Page Indicator & Action Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                for (i in 0 until 3) {
                    val isCurrent = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isCurrent) 24.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) GoldChampagne else EmeraldBorder)
                    )
                }
            }

            if (pagerState.currentPage == 2) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_start_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldChampagne,
                        contentColor = EmeraldDarkest
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Başlayalım",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldCardElevated,
                        contentColor = GoldChampagne
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "İleri",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector
)
