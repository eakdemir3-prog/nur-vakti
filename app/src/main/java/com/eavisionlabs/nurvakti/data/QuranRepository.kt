package com.eavisionlabs.nurvakti.data

import android.content.Context
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.model.Ayah
import com.eavisionlabs.nurvakti.model.Surah
import java.util.Locale

/**
 * Kur'an-ı Kerim'in tamamı (114 sure, 6236 ayet) cihazda, internetsiz.
 * Arapça metin: Kur'an Ansiklopedisi (quranenc.com) Osmanlı hattı.
 * Türkçe meal: Diyanet İşleri Başkanlığı (tanzil.net), İngilizce: Saheeh International (tanzil.net).
 * Sayfa/cüz bilgisi Medine Mushafı (604 sayfa) esas alınarak hesaplanmıştır.
 */
object QuranRepository {

    val surahs: List<Surah> = listOf(
        Surah(1, "الفَاتِحَة", "Fâtiha", "Açılış, Başlangıç", 7, "Mekki", 5, 1, 1),
        Surah(2, "البَقَرَة", "Bakara", "Boğa, İnek", 286, "Medeni", 87, 2, 1),
        Surah(3, "آل عِمْرَان", "Âl-i İmrân", "İmran Ailesi", 200, "Medeni", 89, 50, 3),
        Surah(4, "النِّسَاء", "Nisâ", "Kadınlar", 176, "Medeni", 92, 77, 4),
        Surah(5, "المَائِدَة", "Mâide", "Sofra", 120, "Medeni", 112, 106, 6),
        Surah(6, "الأَنْعَام", "En'âm", "Evcil Hayvanlar", 165, "Mekki", 55, 128, 7),
        Surah(7, "الأَعْرَاف", "A'râf", "Yüksek Yerler", 206, "Mekki", 39, 151, 8),
        Surah(8, "الأَنْفَال", "Enfâl", "Savaş Ganimetleri", 75, "Medeni", 88, 177, 9),
        Surah(9, "التَّوْبَة", "Tevbe", "Tövbe", 129, "Medeni", 113, 187, 10),
        Surah(10, "يُونُس", "Yûnus", "Yunus Peygamber", 109, "Mekki", 51, 208, 11),
        Surah(11, "هُود", "Hûd", "Hud Peygamber", 123, "Mekki", 52, 221, 11),
        Surah(12, "يُوسُف", "Yûsuf", "Yusuf Peygamber", 111, "Mekki", 53, 235, 12),
        Surah(13, "الرَّعْد", "Ra'd", "Gök Gürültüsü", 43, "Medeni", 96, 249, 13),
        Surah(14, "إِبْرَاهِيم", "İbrâhîm", "İbrahim Peygamber", 52, "Mekki", 72, 255, 13),
        Surah(15, "الحِجْر", "Hicr", "Hicr Vadisi", 99, "Mekki", 54, 262, 14),
        Surah(16, "النَّحْل", "Nahl", "Bal Arısı", 128, "Mekki", 70, 267, 14),
        Surah(17, "الإِسْرَاء", "İsrâ", "Gece Yürüyüşü", 111, "Mekki", 50, 282, 15),
        Surah(18, "الكَهْف", "Kehf", "Mağara", 110, "Mekki", 69, 293, 15),
        Surah(19, "مَرْيَم", "Meryem", "Hz. Meryem", 98, "Mekki", 44, 305, 16),
        Surah(20, "طه", "Tâhâ", "Tâhâ Harfleri", 135, "Mekki", 45, 312, 16),
        Surah(21, "الأَنْبِيَاء", "Enbiyâ", "Peygamberler", 112, "Mekki", 73, 322, 17),
        Surah(22, "الحَجّ", "Hacc", "Hac İbadeti", 78, "Medeni", 103, 332, 17),
        Surah(23, "المُؤْمِنُون", "Mü'minûn", "Müminler", 118, "Mekki", 74, 342, 18),
        Surah(24, "النُّور", "Nûr", "Işık, Aydınlık", 64, "Medeni", 102, 350, 18),
        Surah(25, "الفُرْقَان", "Furkân", "Hak ile Batılı Ayıran", 77, "Mekki", 42, 359, 18),
        Surah(26, "الشُّعَرَاء", "Şuarâ", "Şairler", 227, "Mekki", 47, 367, 19),
        Surah(27, "النَّمْل", "Neml", "Karınca", 93, "Mekki", 48, 377, 19),
        Surah(28, "القَصَص", "Kasas", "Kıssalar, Olaylar", 88, "Mekki", 49, 385, 20),
        Surah(29, "العَنْكَبُوت", "Ankebût", "Örümcek", 69, "Mekki", 85, 396, 20),
        Surah(30, "الرُّوم", "Rûm", "Romalılar (Bizans)", 60, "Mekki", 84, 404, 21),
        Surah(31, "لُقْمَان", "Lokmân", "Hz. Lokman", 34, "Mekki", 57, 411, 21),
        Surah(32, "السَّجْدَة", "Secde", "Secde", 30, "Mekki", 75, 415, 21),
        Surah(33, "الأَحْزَاب", "Ahzâb", "Gruplar, Birlikler", 73, "Medeni", 90, 418, 21),
        Surah(34, "سَبَأ", "Sebe'", "Sebe Halkı", 54, "Mekki", 58, 428, 22),
        Surah(35, "فَاطِر", "Fâtır", "Yaratan, Vareden", 45, "Mekki", 43, 434, 22),
        Surah(36, "يس", "Yâsîn", "Yasin Harfleri", 83, "Mekki", 41, 440, 22),
        Surah(37, "الصَّافَّات", "Sâffât", "Sıra Sıra Duranlar", 182, "Mekki", 56, 446, 23),
        Surah(38, "ص", "Sâd", "Sâd Harfi", 88, "Mekki", 38, 453, 23),
        Surah(39, "الزُّمَر", "Zümer", "Zümreler, Gruplar", 75, "Mekki", 59, 458, 23),
        Surah(40, "غَافِر", "Mü'min (Gâfir)", "Bağışlayan", 85, "Mekki", 60, 467, 24),
        Surah(41, "فُصِّلَت", "Fussilet", "Açıklanmış", 54, "Mekki", 61, 477, 24),
        Surah(42, "الشُّورَى", "Şûrâ", "Danışma, İstişare", 53, "Mekki", 62, 483, 25),
        Surah(43, "الزُّخْرُف", "Zuhruf", "Altın Süsler", 89, "Mekki", 63, 489, 25),
        Surah(44, "الدُّخَان", "Duhân", "Duman", 59, "Mekki", 64, 496, 25),
        Surah(45, "الجَاثِيَة", "Câsiye", "Diz Üstü Çökenler", 37, "Mekki", 65, 499, 25),
        Surah(46, "الأَحْقَاف", "Ahkâf", "Kum Tepeleri", 35, "Mekki", 66, 502, 26),
        Surah(47, "مُحَمَّد", "Muhammed", "Hz. Muhammed (s.a.v)", 38, "Medeni", 95, 507, 26),
        Surah(48, "الفَتْح", "Fetih", "Zafer, Açılış", 29, "Medeni", 111, 511, 26),
        Surah(49, "الحُجُرَات", "Hucurât", "Odalar", 18, "Medeni", 106, 515, 26),
        Surah(50, "ق", "Kâf", "Kâf Harfi", 45, "Mekki", 34, 518, 26),
        Surah(51, "الذَّارِيَات", "Zâriyât", "Tozutup Savuranlar", 60, "Mekki", 67, 520, 26),
        Surah(52, "الطُّور", "Tûr", "Tur Dağı", 49, "Mekki", 76, 523, 27),
        Surah(53, "النَّجْم", "Necm", "Yıldız", 62, "Mekki", 23, 526, 27),
        Surah(54, "القَمَر", "Kamer", "Ay", 55, "Mekki", 37, 528, 27),
        Surah(55, "الرَّحْمَن", "Rahmân", "Çok Merhametli", 78, "Medeni", 97, 531, 27),
        Surah(56, "الوَاقِعَة", "Vâkıa", "Kıyamet Olayı", 96, "Mekki", 46, 534, 27),
        Surah(57, "الحَدِيد", "Hadîd", "Demir", 29, "Medeni", 94, 537, 27),
        Surah(58, "المُجَادَلَة", "Mücâdele", "Hakkını Savunan Kadın", 22, "Medeni", 105, 542, 28),
        Surah(59, "الحَشْر", "Haşr", "Toplanma, Sürgün", 24, "Medeni", 101, 545, 28),
        Surah(60, "المُمْتَحَنَة", "Mümtehine", "İmtihan Edilen Kadın", 13, "Medeni", 91, 549, 28),
        Surah(61, "الصَّفّ", "Saff", "Sıra, Dizi", 14, "Medeni", 109, 551, 28),
        Surah(62, "الجُمُعَة", "Cuma", "Cuma Günü ve Namazı", 11, "Medeni", 110, 553, 28),
        Surah(63, "المُنَافِقُون", "Münâfikûn", "İkiyüzlüler", 11, "Medeni", 104, 554, 28),
        Surah(64, "التَّغَابُن", "Teğâbün", "Aldanma ve Kâr Günü", 18, "Medeni", 108, 556, 28),
        Surah(65, "الطَّلَاق", "Talâk", "Boşanma Hükümleri", 12, "Medeni", 99, 558, 28),
        Surah(66, "التَّحْرِيم", "Tahrîm", "Haram Kılma", 12, "Medeni", 107, 560, 28),
        Surah(67, "المُلْك", "Mülk (Tebâreke)", "Hükümranlık, Egemenlik", 30, "Mekki", 77, 562, 29),
        Surah(68, "القَلَم", "Kalem", "Kalem", 52, "Mekki", 2, 564, 29),
        Surah(69, "الحَاقَّة", "Hâkka", "Gerçekleşecek Kıyamet", 52, "Mekki", 78, 566, 29),
        Surah(70, "المَعَارِج", "Meâric", "Yükselme Dereceleri", 44, "Mekki", 79, 568, 29),
        Surah(71, "نُوح", "Nûh", "Nuh Peygamber", 28, "Mekki", 71, 570, 29),
        Surah(72, "الجِنّ", "Cin", "Cinler", 28, "Mekki", 40, 572, 29),
        Surah(73, "المُزَّمِّل", "Müzzemmil", "Örtüsüne Bürünen", 20, "Mekki", 3, 574, 29),
        Surah(74, "المُدَّثِّر", "Müddessir", "Örtüsüne Sarınan", 56, "Mekki", 4, 575, 29),
        Surah(75, "القِيَامَة", "Kıyâme", "Diriliş Günü", 40, "Mekki", 31, 577, 29),
        Surah(76, "الإِنْسَان", "İnsân (Dehr)", "İnsan", 31, "Medeni", 98, 578, 29),
        Surah(77, "المُرْسَلَات", "Mürselât", "Gönderilen Melekler", 50, "Mekki", 33, 580, 29),
        Surah(78, "النَّبَأ", "Nebe' (Amme)", "Büyük Haber", 40, "Mekki", 80, 582, 30),
        Surah(79, "النَّازِعَات", "Nâziât", "Söküp Çıkaran Melekler", 46, "Mekki", 81, 583, 30),
        Surah(80, "عَبَسَ", "Abese", "Yüzünü Ekşitti", 42, "Mekki", 24, 585, 30),
        Surah(81, "التَّكْوِير", "Tekvîr", "Güneşin Dürülmesi", 29, "Mekki", 7, 586, 30),
        Surah(82, "الانْفِطَار", "İnfitâr", "Göklerin Yarılması", 19, "Mekki", 82, 587, 30),
        Surah(83, "المُطَفِّفِين", "Mutaffifîn", "Ölçüde Hile Yapanlar", 36, "Mekki", 86, 587, 30),
        Surah(84, "الانْشِقَاق", "İnşikâk", "Göğün Parçalanması", 25, "Mekki", 83, 589, 30),
        Surah(85, "البُرُوج", "Bürûc", "Burçlar ve Yıldızlar", 22, "Mekki", 27, 590, 30),
        Surah(86, "الطَّارِق", "Târık", "Gece Çakan Yıldız", 17, "Mekki", 36, 591, 30),
        Surah(87, "الأَعْلَى", "A'lâ", "En Yüce Olan Allah", 19, "Mekki", 8, 591, 30),
        Surah(88, "الغَاشِيَة", "Gâşiye", "Her Şeyi Kaplayan Kıyamet", 26, "Mekki", 68, 592, 30),
        Surah(89, "الفَجْر", "Fecr", "Tan Yeri, Sabah Vakti", 30, "Mekki", 10, 593, 30),
        Surah(90, "البَلَد", "Beled", "Mekke Şehri", 20, "Mekki", 35, 594, 30),
        Surah(91, "الشَّمْس", "Şems", "Güneş", 15, "Mekki", 26, 595, 30),
        Surah(92, "اللَّيْل", "Leyl", "Gece", 21, "Mekki", 9, 595, 30),
        Surah(93, "الضُّحَى", "Duhâ", "Kuşluk Vakti", 11, "Mekki", 11, 596, 30),
        Surah(94, "الشَّرْح", "İnşirâh", "Gönül Ferahlığı", 8, "Mekki", 12, 596, 30),
        Surah(95, "التِّين", "Tîn", "İncir Ağacı", 8, "Mekki", 28, 597, 30),
        Surah(96, "العَلَق", "Alak", "İlk Ayetler, Aşılanmış Hücre", 19, "Mekki", 1, 597, 30),
        Surah(97, "القَدْر", "Kadir", "Kadir Gecesi", 5, "Mekki", 25, 598, 30),
        Surah(98, "البَيِّنَة", "Beyyine", "Açık Delil, Belge", 8, "Medeni", 100, 598, 30),
        Surah(99, "الزَّلْزَلَة", "Zilzâl", "Büyük Deprem", 8, "Medeni", 93, 599, 30),
        Surah(100, "العَادِيَات", "Âdiyât", "Hızlı Koşan Atlar", 11, "Mekki", 14, 599, 30),
        Surah(101, "القَارِعَة", "Kâria", "Kapı Çalan Büyük Kıyamet", 11, "Mekki", 30, 600, 30),
        Surah(102, "التَّكَاثُر", "Tekâsür", "Çoklukla Övünme Yarışı", 8, "Mekki", 16, 600, 30),
        Surah(103, "العَصْر", "Asr", "Zaman, İkindi Vakti", 3, "Mekki", 13, 601, 30),
        Surah(104, "الهُمَزَة", "Hümeze", "Dedikoducu, Kusur Arayan", 9, "Mekki", 32, 601, 30),
        Surah(105, "الفِيل", "Fîl", "Fil Ordusu Olayı", 5, "Mekki", 19, 601, 30),
        Surah(106, "قُرَيْش", "Kureyş", "Kureyş Kabilesi", 4, "Mekki", 29, 602, 30),
        Surah(107, "المَاعُون", "Mâûn", "Yardım, Zekât", 7, "Mekki", 17, 602, 30),
        Surah(108, "الكَوْثَر", "Kevser", "Bitmeyen Bol Nimet", 3, "Mekki", 15, 602, 30),
        Surah(109, "الكَافِرُون", "Kâfirûn", "İnkarcılar", 6, "Mekki", 18, 603, 30),
        Surah(110, "النَّصْر", "Nasr", "Yardım ve Zafer", 3, "Medeni", 114, 603, 30),
        Surah(111, "المَسَد", "Tebbet (Mesed)", "Hurma Lifi, Alev", 5, "Mekki", 6, 603, 30),
        Surah(112, "الإِخْلَاص", "İhlâs", "Samimi Tevhid İnancı", 4, "Mekki", 22, 604, 30),
        Surah(113, "الفَلَق", "Felak", "Sabahın Aydınlığı", 5, "Mekki", 20, 604, 30),
        Surah(114, "النَّاس", "Nâs", "İnsanlar", 6, "Mekki", 21, 604, 30)
    )

    const val TOTAL_AYAHS = 6236
    const val TOTAL_PAGES = 604

    private var appContext: Context? = null
    @Volatile private var loaded = false
    private lateinit var surahOf: IntArray
    private lateinit var ayahOf: IntArray
    private lateinit var pageOf: IntArray
    private lateinit var juzOf: IntArray
    private lateinit var arabic: Array<String>
    private var mealLang: String = ""
    private var meal: Array<String> = emptyArray()
    /** surahStart[n] = sure n'nin ilk ayetinin genel sırası (0 tabanlı) */
    private val surahStart = IntArray(116)

    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    @Synchronized
    private fun ensureLoaded() {
        if (!loaded) {
            val ctx = appContext ?: throw IllegalStateException("QuranRepository.init çağrılmadı")
            val s = IntArray(TOTAL_AYAHS); val a = IntArray(TOTAL_AYAHS)
            val p = IntArray(TOTAL_AYAHS); val j = IntArray(TOTAL_AYAHS)
            val ar = arrayOfNulls<String>(TOTAL_AYAHS)
            var i = 0
            ctx.assets.open("quran/arabic.txt").bufferedReader(Charsets.UTF_8).forEachLine { line ->
                if (i >= TOTAL_AYAHS || line.isBlank()) return@forEachLine
                val parts = line.split('|', limit = 5)
                s[i] = parts[0].toInt(); a[i] = parts[1].toInt(); p[i] = parts[2].toInt(); j[i] = parts[3].toInt()
                ar[i] = parts[4]
                i++
            }
            surahOf = s; ayahOf = a; pageOf = p; juzOf = j
            @Suppress("UNCHECKED_CAST")
            arabic = ar as Array<String>
            var last = 0
            for (k in 0 until TOTAL_AYAHS) if (s[k] != last) { last = s[k]; surahStart[last] = k }
            surahStart[115] = TOTAL_AYAHS
            loaded = true
        }
        val wanted = if (I18n.language == "tr") "tr" else "en"
        if (mealLang != wanted) {
            val ctx = appContext!!
            meal = ctx.assets.open("quran/meal_$wanted.txt").bufferedReader(Charsets.UTF_8).readLines().toTypedArray()
            mealLang = wanted
        }
    }

    private fun ayahAt(idx: Int) = Ayah(
        surahNumber = surahOf[idx],
        ayahNumber = ayahOf[idx],
        textArabic = arabic[idx],
        translationTr = meal.getOrElse(idx) { "" },
        page = pageOf[idx],
        juz = juzOf[idx]
    )

    fun getVersesForSurah(surahNumber: Int): List<Ayah> {
        if (surahNumber !in 1..114) return emptyList()
        ensureLoaded()
        return (surahStart[surahNumber] until surahStart[surahNumber + 1]).map { ayahAt(it) }
    }

    fun getAyah(surah: Int, ayah: Int): Ayah? {
        if (surah !in 1..114) return null
        ensureLoaded()
        val idx = surahStart[surah] + ayah - 1
        return if (idx < surahStart[surah + 1] && ayah >= 1) ayahAt(idx) else null
    }

    fun versesForPage(page: Int): List<Ayah> {
        ensureLoaded()
        return (0 until TOTAL_AYAHS).filter { pageOf[it] == page }.map { ayahAt(it) }
    }

    fun versesForJuz(juz: Int): List<Ayah> {
        ensureLoaded()
        return (0 until TOTAL_AYAHS).filter { juzOf[it] == juz }.map { ayahAt(it) }
    }

    /** Cüzün başladığı (sure, ayet) */
    fun juzStart(juz: Int): Pair<Int, Int> {
        ensureLoaded()
        val idx = (0 until TOTAL_AYAHS).first { juzOf[it] >= juz }
        return surahOf[idx] to ayahOf[idx]
    }

    /** Sayfanın başladığı (sure, ayet) */
    fun pageStart(page: Int): Pair<Int, Int> {
        ensureLoaded()
        val idx = (0 until TOTAL_AYAHS).firstOrNull { pageOf[it] >= page } ?: 0
        return surahOf[idx] to ayahOf[idx]
    }

    fun pagesOfJuz(juz: Int): IntRange {
        ensureLoaded()
        val pages = (0 until TOTAL_AYAHS).filter { juzOf[it] == juz }.map { pageOf[it] }
        return pages.minOrNull()!!..pages.maxOrNull()!!
    }

    fun juzOfPage(page: Int): Int {
        ensureLoaded()
        val idx = (0 until TOTAL_AYAHS).firstOrNull { pageOf[it] == page } ?: return 1
        return juzOf[idx]
    }

    /** Mealde ve sure adlarında arama (en fazla 100 sonuç). */
    fun searchVerses(query: String, limit: Int = 100): List<Ayah> {
        val q = query.trim().lowercase(Locale("tr"))
        if (q.length < 3) return emptyList()
        ensureLoaded()
        val out = ArrayList<Ayah>()
        for (i in 0 until TOTAL_AYAHS) {
            if (meal.getOrElse(i) { "" }.lowercase(Locale("tr")).contains(q)) {
                out += ayahAt(i)
                if (out.size >= limit) break
            }
        }
        return out
    }

    fun searchSurahs(query: String): List<Surah> {
        if (query.isBlank()) return surahs
        val q = query.trim().lowercase(Locale("tr"))
        return surahs.filter {
            it.nameTurkish.lowercase(Locale("tr")).contains(q) ||
                it.nameArabic.contains(q) ||
                it.meaningTurkish.lowercase(Locale("tr")).contains(q) ||
                it.number.toString() == q
        }
    }

    val surahReciters = listOf(
        "alafasy" to "Mishary Rashid Alafasy",
        "abdulbaset" to "Abdulbasit Abdussamed",
        "maher" to "Maher el-Muaykılî",
        "ghamdi" to "Sa'd el-Ğâmidî"
    )

    fun getSurahAudioUrl(surahNumber: Int, reciterKey: String = "alafasy"): String {
        val n = String.format(Locale.ROOT, "%03d", surahNumber)
        return when (reciterKey) {
            "abdulbaset" -> "https://server7.mp3quran.net/basit/$n.mp3"
            "maher" -> "https://server12.mp3quran.net/maher/$n.mp3"
            "ghamdi" -> "https://server8.mp3quran.net/ghamdi/$n.mp3"
            else -> "https://server8.mp3quran.net/afs/$n.mp3"
        }
    }

    /** Ayet ayet ses (everyayah.com) */
    fun getAyahAudioUrl(surah: Int, ayah: Int, reciterKey: String = "alafasy"): String {
        val folder = when (reciterKey) {
            "abdulbaset" -> "Abdul_Basit_Murattal_192kbps"
            "maher" -> "MaherAlMuaiqly128kbps"
            "ghamdi" -> "Ghamadi_40kbps"
            "husary" -> "Husary_128kbps"
            else -> "Alafasy_128kbps"
        }
        return String.format(Locale.ROOT, "https://everyayah.com/data/%s/%03d%03d.mp3", folder, surah, ayah)
    }
}
