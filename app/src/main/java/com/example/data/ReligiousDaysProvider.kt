package com.example.data

import com.example.model.ReligiousDayItem
import java.util.Calendar

object ReligiousDaysProvider {

    fun getReligiousDays(): List<ReligiousDayItem> {
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)

        val rawList = listOf(
            ReligiousDayRaw(
                "regaip",
                "Regaip Kandili",
                "Regaip Gecesi (Üç Ayların Başlangıcı)",
                "Recep ayının ilk Cuma gecesidir. İlahi rahmet, mağfiret ve ihsan kapılarının açıldığı mübarek bir gecedir.",
                currentYear, 1, 15
            ),
            ReligiousDayRaw(
                "mirac",
                "Miraç Kandili",
                "27 Recep",
                "Peygamber Efendimiz'in (s.a.v) Mescid-i Haram'dan Mescid-i Aksa'ya, oradan da semaya yükseltilip beş vakit namazın hediye edildiği mübarek gece.",
                currentYear, 2, 6
            ),
            ReligiousDayRaw(
                "berat",
                "Berat Kandili",
                "15 Şaban",
                "Kulların günahlardan arınma, af ve ilahi beraata kavuşma gecesidir. Yıllık mukadderatın tanzim edildiği ifade edilir.",
                currentYear, 2, 23
            ),
            ReligiousDayRaw(
                "ramazan_baslangic",
                "Ramazan-ı Şerif Başlangıcı",
                "1 Ramazan",
                "On bir ayın sultanı, Kur'an ayı olan mübarek Ramazan'ın ilk oruç günü.",
                currentYear, 3, 11
            ),
            ReligiousDayRaw(
                "kadir",
                "Kadir Gecesi",
                "27 Ramazan",
                "Bin aydan daha hayırlı olan, Kur'an-ı Kerim'in Levh-i Mahfuz'dan indirilmeye başlandığı kutlu gece.",
                currentYear, 4, 5
            ),
            ReligiousDayRaw(
                "arefe_ramazan",
                "Ramazan Bayramı Arefesi",
                "30 Ramazan",
                "Ramazan ayının son oruç günü ve bayrama hazırlık vakti.",
                currentYear, 4, 9
            ),
            ReligiousDayRaw(
                "ramazan_bayrami",
                "Ramazan Bayramı (1. Gün)",
                "1 Şevval",
                "Bir aylık oruç ve ibadetlerin ardından müminlerin sevinç ve muhabbetle kutladığı bayram günü.",
                currentYear, 4, 10
            ),
            ReligiousDayRaw(
                "arefe_kurban",
                "Kurban Bayramı Arefesi",
                "9 Zilhicce",
                "Hacıların Arafat'ta vakfeye durduğu, oruç tutmanın pek faziletli olduğu af ve tövbe günü.",
                currentYear, 6, 15
            ),
            ReligiousDayRaw(
                "kurban_bayrami",
                "Kurban Bayramı (1. Gün)",
                "10 Zilhicce",
                "Hz. İbrahim ve Hz. İsmail'in teslimiyetini anarak Allah için kurban ibadetinin eda edildiği mübarek bayram.",
                currentYear, 6, 16
            ),
            ReligiousDayRaw(
                "hicri_yilbasi",
                "Hicri Yılbaşı (1448)",
                "1 Muharrem 1448",
                "Peygamber Efendimiz'in Mekke'den Medine'ye hicretini esas alan İslami takvimin yeni yıl başlangıcı.",
                currentYear, 7, 7
            ),
            ReligiousDayRaw(
                "asure",
                "Aşure Günü",
                "10 Muharrem",
                "Tarihte birçok peygamberin ilahi kurtuluşa erdiği, Hz. Hüseyin ve Kerbela şehitlerinin rahmetle anıldığı gün.",
                currentYear, 7, 16
            ),
            ReligiousDayRaw(
                "mevlid",
                "Mevlid Kandili",
                "12 Rebiülevvel",
                "Âlemlere rahmet olarak gönderilen Sevgili Peygamberimiz Hazreti Muhammed'in (s.a.v.) dünyayı teşrif ettiği gece.",
                currentYear, 9, 14
            )
        )

        val todayMillis = now.timeInMillis

        return rawList.map { raw ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, raw.year)
                set(Calendar.MONTH, raw.month - 1)
                set(Calendar.DAY_OF_MONTH, raw.day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var diffMillis = cal.timeInMillis - todayMillis
            var daysRemaining = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

            if (daysRemaining < 0) {
                // Next year occurrence
                cal.add(Calendar.YEAR, 1)
                diffMillis = cal.timeInMillis - todayMillis
                daysRemaining = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
            }

            val gregStr = String.format("%02d.%02d.%d", raw.day, raw.month, cal.get(Calendar.YEAR))

            ReligiousDayItem(
                id = raw.id,
                title = raw.title,
                hijriDate = raw.hijriDate,
                gregorianDate = gregStr,
                daysRemaining = daysRemaining.coerceAtLeast(0),
                description = raw.description
            )
        }.sortedBy { it.daysRemaining }
    }

    private data class ReligiousDayRaw(
        val id: String,
        val title: String,
        val hijriDate: String,
        val description: String,
        val year: Int,
        val month: Int,
        val day: Int
    )
}
