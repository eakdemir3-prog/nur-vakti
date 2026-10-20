package com.eavisionlabs.nurvakti.data

import com.eavisionlabs.nurvakti.model.ReligiousDayItem
import java.util.Calendar
import java.util.Locale

/**
 * Dini günler ve kandiller hicri takvimden her yıl için otomatik hesaplanır.
 * Kandiller, Diyanet takvimindeki gibi gecesi idrak edilen günün (hicri tarihten bir önceki gün) tarihiyle gösterilir.
 */
object ReligiousDaysProvider {

    private enum class Rule { ON_DATE, NIGHT_BEFORE, REGAIP, LAST_DAY_OF_RAMAZAN }

    private data class Def(
        val id: String,
        val title: String,
        val hijriLabel: String,
        val description: String,
        val hMonth: Int,
        val hDay: Int,
        val rule: Rule = Rule.ON_DATE,
        val isKandil: Boolean = false
    )

    private val defs = listOf(
        Def("regaip", "Regaip Kandili", "Recep ayının ilk Cuma gecesi", "Recep ayının ilk Cuma gecesidir. İlahi rahmet, mağfiret ve ihsan kapılarının açıldığı mübarek bir gecedir.", 7, 1, Rule.REGAIP, true),
        Def("mirac", "Miraç Kandili", "27 Recep gecesi", "Peygamber Efendimiz'in (s.a.v) Mescid-i Haram'dan Mescid-i Aksa'ya, oradan da semaya yükseltilip beş vakit namazın hediye edildiği mübarek gece.", 7, 27, Rule.NIGHT_BEFORE, true),
        Def("berat", "Berat Kandili", "15 Şaban gecesi", "Kulların günahlardan arınma, af ve ilahi beraata kavuşma gecesidir. Yıllık mukadderatın tanzim edildiği ifade edilir.", 8, 15, Rule.NIGHT_BEFORE, true),
        Def("ramazan_baslangic", "Ramazan-ı Şerif Başlangıcı", "1 Ramazan", "On bir ayın sultanı, Kur'an ayı olan mübarek Ramazan'ın ilk oruç günü.", 9, 1),
        Def("kadir", "Kadir Gecesi", "27 Ramazan gecesi", "Bin aydan daha hayırlı olan, Kur'an-ı Kerim'in Levh-i Mahfuz'dan indirilmeye başlandığı kutlu gece.", 9, 27, Rule.NIGHT_BEFORE, true),
        Def("arefe_ramazan", "Ramazan Bayramı Arefesi", "Ramazan'ın son günü", "Ramazan ayının son oruç günü ve bayrama hazırlık vakti.", 10, 1, Rule.LAST_DAY_OF_RAMAZAN),
        Def("ramazan_bayrami", "Ramazan Bayramı (1. Gün)", "1 Şevval", "Bir aylık oruç ve ibadetlerin ardından müminlerin sevinç ve muhabbetle kutladığı bayram günü.", 10, 1),
        Def("arefe_kurban", "Kurban Bayramı Arefesi", "9 Zilhicce", "Hacıların Arafat'ta vakfeye durduğu, oruç tutmanın pek faziletli olduğu af ve tövbe günü.", 12, 9),
        Def("kurban_bayrami", "Kurban Bayramı (1. Gün)", "10 Zilhicce", "Hz. İbrahim ve Hz. İsmail'in teslimiyetini anarak Allah için kurban ibadetinin eda edildiği mübarek bayram.", 12, 10),
        Def("hicri_yilbasi", "Hicri Yılbaşı", "1 Muharrem", "Peygamber Efendimiz'in Mekke'den Medine'ye hicretini esas alan İslami takvimin yeni yıl başlangıcı.", 1, 1),
        Def("asure", "Aşure Günü", "10 Muharrem", "Tarihte birçok peygamberin ilahi kurtuluşa erdiği, Hz. Hüseyin ve Kerbela şehitlerinin rahmetle anıldığı gün.", 1, 10),
        Def("mevlid", "Mevlid Kandili", "12 Rebiülevvel gecesi", "Âlemlere rahmet olarak gönderilen Sevgili Peygamberimiz Hazreti Muhammed'in (s.a.v.) dünyayı teşrif ettiği gece.", 3, 12, Rule.NIGHT_BEFORE, true)
    )

    /** Önümüzdeki ~13 ay içindeki tüm dini günler, en yakından uzağa. */
    fun getReligiousDays(from: Calendar = Calendar.getInstance()): List<ReligiousDayItem> {
        val start = (from.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        // Gün gün hicri karşılıkları (bugünden 400 gün)
        val days = (0..400).map { offset ->
            val c = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
            c to PrayerCalculationEngine.hijriParts(c)
        }
        val result = mutableListOf<ReligiousDayItem>()
        for (def in defs) {
            val idx = days.indices.firstOrNull { i -> matches(def, days, i) } ?: continue
            val (cal, h) = days[idx]
            val titleSuffix = if (def.id == "hicri_yilbasi") " (${h.third})" else ""
            result += ReligiousDayItem(
                id = def.id,
                title = def.title + titleSuffix,
                hijriDate = def.hijriLabel,
                gregorianDate = String.format(
                    Locale.ROOT, "%02d.%02d.%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)
                ),
                daysRemaining = idx,
                description = def.description
            )
        }
        return result.sortedBy { it.daysRemaining }
    }

    /** Bugün bir kandil/bayram mı? (hatırlatma bildirimi için) */
    fun todaysEvent(today: Calendar = Calendar.getInstance()): ReligiousDayItem? =
        getReligiousDays(today).firstOrNull { it.daysRemaining == 0 }

    fun isKandil(id: String): Boolean = defs.any { it.id == id && it.isKandil }

    private fun matches(def: Def, days: List<Pair<Calendar, Triple<Int, Int, Int>>>, i: Int): Boolean {
        val (cal, h) = days[i]
        val next = days.getOrNull(i + 1)?.second ?: return false
        return when (def.rule) {
            Rule.ON_DATE -> h.second == def.hMonth && h.first == def.hDay
            Rule.NIGHT_BEFORE -> next.second == def.hMonth && next.first == def.hDay
            Rule.LAST_DAY_OF_RAMAZAN -> h.second == 9 && next.second == 10
            // Regaip: Recep'in ilk Cuma gününden önceki Perşembe (gecesi Cuma gecesidir)
            Rule.REGAIP -> next.second == 7 && next.first <= 7 &&
                days[i + 1].first.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY &&
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
        }
    }
}
