package com.eavisionlabs.nurvakti.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Basit uygulama içi çeviri katmanı. Anahtar, metnin Türkçe halidir; çeviri bulunamazsa Türkçe gösterilir.
 * Dil değişince Compose ekranları [language] durumunu okuduğu için otomatik yenilenir.
 */
object I18n {
    var language by mutableStateOf("tr")

    val isRtl: Boolean get() = language == "ar"

    fun t(tr: String): String = when (language) {
        "en" -> StringsEn.map[tr] ?: tr
        "ar" -> StringsAr.map[tr] ?: StringsEn.map[tr] ?: tr
        else -> tr
    }
}

/** Kısa yazım: t("Ana Sayfa") */
fun t(tr: String): String = I18n.t(tr)
