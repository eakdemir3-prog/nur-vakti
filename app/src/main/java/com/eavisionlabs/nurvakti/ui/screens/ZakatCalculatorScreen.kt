package com.eavisionlabs.nurvakti.ui.screens

import com.eavisionlabs.nurvakti.i18n.t
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.ui.UiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onCalculate: (goldGrams: Double, cashTry: Double, goodsTry: Double, debtsTry: Double, customGoldPrice: Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var goldGramPriceText by remember { mutableStateOf("3450") }
    var goldGramsText by remember { mutableStateOf("0") }
    var cashTryText by remember { mutableStateOf("0") }
    var goodsTryText by remember { mutableStateOf("0") }
    var debtsTryText by remember { mutableStateOf("0") }

    val turkishCurrencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    }

    val recalculate = {
        val price = goldGramPriceText.toDoubleOrNull() ?: 3450.0
        val gold = goldGramsText.toDoubleOrNull() ?: 0.0
        val cash = cashTryText.toDoubleOrNull() ?: 0.0
        val goods = goodsTryText.toDoubleOrNull() ?: 0.0
        val debts = debtsTryText.toDoubleOrNull() ?: 0.0
        onCalculate(gold, cash, goods, debts, price)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = t("Zekât Hesaplayıcı"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = t("Geri"),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("zakat_calculator_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Nisab Info Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = t("Nisap Miktarı: Diyanet ve fıkıh kriterlerine göre 80.18 gram altın veya dengi servettir. 1 kamerî yıl boyunca nisap üstünde kalan birikimden %2.5 (1/40) zekât verilir."),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Calculation Result Banner
            item {
                val res = uiState.zakatResult
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (res.isZakatObligatory) 1.5.dp else 1.dp,
                        color = if (res.isZakatObligatory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (res.isZakatObligatory) "Hesaplanan Zekât Miktarı (%2.5)" else "Nisap Eşiğine Ulaşılmadı",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (res.isZakatObligatory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (res.isZakatObligatory) turkishCurrencyFormat.format(res.zakatAmountDueTry) else "₺0,00",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (res.isZakatObligatory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Toplam Net Servet: ${turkishCurrencyFormat.format(res.totalWealthTry)} (Nisap: ${turkishCurrencyFormat.format(res.nisabThresholdTry)})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Fields
            item {
                Text(
                    text = t("Varlık ve Borç Bilgileriniz"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Gold Gram Price (Editable as prescribed by prompt section 6)
                OutlinedTextField(
                    value = goldGramPriceText,
                    onValueChange = {
                        goldGramPriceText = it
                        recalculate()
                    },
                    label = { Text(t("Altın Gram Fiyatı (₺/gram) - Düzenlenebilir")) },
                    supportingText = { Text(t("Kullanıcı tarafından serbestçe güncellenebilir")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = goldGramsText,
                    onValueChange = {
                        goldGramsText = it
                        recalculate()
                    },
                    label = { Text(t("Altın ve Ziynet Miktarı (Gram)")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = cashTryText,
                    onValueChange = {
                        cashTryText = it
                        recalculate()
                    },
                    label = { Text(t("Nakit Para ve Banka Mevduatı (₺)")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = goodsTryText,
                    onValueChange = {
                        goodsTryText = it
                        recalculate()
                    },
                    label = { Text(t("Ticari Mallar ve Alacaklar (₺)")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = debtsTryText,
                    onValueChange = {
                        debtsTryText = it
                        recalculate()
                    },
                    label = { Text(t("Vadesi Gelmiş Borçlar (₺ - Düşülecek)")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = recalculate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(t("Zekâtı Hesapla"), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = t("Bilgilendirme Notu: Zekât yükümlülüğü için servetin temel ihtiyaçlar (havâic-i asliyye) dışında nisap miktarını aşması ve üzerinden bir hicrî yıl geçmesi gerekir. Karmaşık ortaklıklar ve özel fıkhî durumlar için il müftülüklerine danışınız."),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
