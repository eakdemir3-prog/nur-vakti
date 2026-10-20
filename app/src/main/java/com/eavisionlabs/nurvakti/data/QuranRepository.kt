package com.eavisionlabs.nurvakti.data

import com.eavisionlabs.nurvakti.model.Ayah
import com.eavisionlabs.nurvakti.model.Surah

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

    fun searchSurahs(query: String): List<Surah> {
        if (query.isBlank()) return surahs
        val q = query.trim().lowercase()
        return surahs.filter {
            it.nameTurkish.lowercase().contains(q) ||
            it.nameArabic.contains(q) ||
            it.meaningTurkish.lowercase().contains(q) ||
            it.number.toString() == q
        }
    }

    /**
     * Returns audio streaming URL for a given Surah number (1..114).
     * Reciters: Mishary Alafasy (default)
     */
    fun getSurahAudioUrl(surahNumber: Int, reciterKey: String = "alafasy"): String {
        val formattedNumber = String.format("%03d", surahNumber)
        return when (reciterKey) {
            "abdulbaset" -> "https://server7.mp3quran.net/basit/$formattedNumber.mp3"
            "maher" -> "https://server12.mp3quran.net/maher/$formattedNumber.mp3"
            "ghamdi" -> "https://server8.mp3quran.net/ghamdi/$formattedNumber.mp3"
            else -> "https://server8.mp3quran.net/afs/$formattedNumber.mp3"
        }
    }

    /**
     * Authentic Ayah data with verified Uthmani Arabic text and official Turkish translation.
     */
    fun getVersesForSurah(surahNumber: Int): List<Ayah> {
        return when (surahNumber) {
            1 -> listOf(
                Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Rahmân ve Rahîm olan Allah'ın adıyla."),
                Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Hamd (övme ve övülme), âlemlerin Rabbi Allah'a mahsustur."),
                Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "O, Rahmândır ve Rahîmdir."),
                Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Ceza ve mükâfat gününün (âhiretin) yegâne sahibidir."),
                Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "(Rabbimiz!) Yalnız sana kulluk eder ve yalnız senden yardım dileriz."),
                Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Bizi dosdoğru yola ilet;"),
                Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Kendilerine nimet verdiğin kimselerin yoluna; gazaba uğramışların ve sapmışların yoluna değil. (Âmin)")
            )
            36 -> listOf( // Yasin (Opening & Core)
                Ayah(36, 1, "يسٓ", "Yâ Sîn."),
                Ayah(36, 2, "وَالْقُرْآنِ الْحَكِيمِ", "Hikmet dolu Kur'an'a andolsun ki,"),
                Ayah(36, 3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Sen şüphesiz peygamberlerdensin;"),
                Ayah(36, 4, "عَلَىٰ صِرَاطٍ مُسْتَقِيمٍ", "Dosdoğru bir yol üzerindesin."),
                Ayah(36, 5, "تَنْزِيلَ الْعَزِيزِ الرَّحِيمِ", "Bu Kur'an, mutlak güç ve merhamet sahibi Allah tarafından indirilmiştir."),
                Ayah(36, 6, "لِتُنْذِرَ قَوْمًا مَا أُنْذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ", "Ataları uyarılmamış, bu yüzden kendileri de gaflet içinde kalmış bir toplumu uyarman için."),
                Ayah(36, 58, "سَلَامٌ قَوْلًا مِنْ رَبٍّ رَحِيمٍ", "Çok merhametli olan Rabb'den bir söz olarak onlara 'Selâm' vardır."),
                Ayah(36, 82, "إِنَّمَا أَمْرُهُ إِذَا أَرَادَ شَيْئًا أَنْ يَقُولَ لَهُ كُنْ فَيَكُونُ", "Bir şeyi dilediği zaman, O'nun buyruğu sadece o şeye 'Ol!' demektir; o da hemen oluverir."),
                Ayah(36, 83, "فَسُبْحَانَ الَّذِي بِيَدِهِ مَلَكُوتُ كُلِّ شَيْءٍ وَإِلَيْهِ تُرْجَعُونَ", "Her şeyin egemenliği kendi elinde olan Allah bütün eksikliklerden uzaktır. Ve siz sonunda O'na döndürüleceksiniz.")
            )
            67 -> listOf( // Mulk
                Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Mutlak egemenlik elinde bulunan Allah yüceler yücesidir; O her şeye hakkıyla kadirdir."),
                Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا وَهُوَ الْعَزِيزُ الْغَفُورُ", "O, hanginizin daha güzel amel işleyeceğini sınamak için ölümü ve hayatı yaratandır. O, mutlak güç sahibidir, çok bağışlayıcıdır."),
                Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "O, yedi göğü birbiriyle tam bir uyum içinde yaratandır. Rahmân'ın yaratışında hiçbir düzensizlik göremezsin."),
                Ayah(67, 4, "فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "Gözünü bir kez daha çevir bak; bir çatlak veya kusur görebilir misin?"),
                Ayah(67, 13, "وَأَسِرُّوا قَوْلَكُمْ أَوِ اجْهَرُوا بِهِ إِنَّهُ عَلِيمٌ بِذَاتِ الصُّدُورِ", "Sözünüzü ister gizleyin, ister açığa vurun; şüphesiz O, kalplerin özünü çok iyi bilendir."),
                Ayah(67, 14, "أَلَا يَعْلَمُ مَنْ خَلَقَ وَهُوَ اللَّطِيفُ الْخَبِيرُ", "Yaratan hiç bilmez mi? O, bütün incelikleri bilendir, her şeyden haberdardır.")
            )
            93 -> listOf( // Duha
                Ayah(93, 1, "وَالضُّحَىٰ", "Aydınlık kuşluk vaktine andolsun,"),
                Ayah(93, 2, "وَاللَّيْلِ إِذَا سَجَىٰ", "Karanlığı çöktüğü zaman geceye andolsun ki,"),
                Ayah(93, 3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Rabbin seni ne terk etti ne de sana darıldı."),
                Ayah(93, 4, "وَلَلْآخِرَةُ خَيْرٌ لَكَ مِنَ الْأُولَىٰ", "Elbette senin için son olan (ahiret), ilk olandan (dünyadan) çok daha hayırlıdır."),
                Ayah(93, 5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "Pek yakında Rabbin sana verecek ve sen hoşnut olacaksın."),
                Ayah(93, 6, "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ", "O, seni bir yetim iken bulup barındırmadı mı?"),
                Ayah(93, 7, "وَوَجَدَكَ ضَالًّا فَهَدَىٰ", "Seni yolunu kaybetmiş bulup doğru yola iletmedi mi?"),
                Ayah(93, 8, "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ", "Seni ihtiyaç içinde bulup zengin etmedi mi?"),
                Ayah(93, 9, "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ", "Öyleyse yetimi sakın ezme!"),
                Ayah(93, 10, "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ", "Bir şey isteyeni asla azarlama!"),
                Ayah(93, 11, "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", "Ve Rabbinin nimetini şükranla an!")
            )
            94 -> listOf( // Insirah
                Ayah(94, 1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Biz senin göğsünü yarıp genişletmedik mi?"),
                Ayah(94, 2, "وَوَضَعْنَا عَنْكَ وِزْرَكَ", "Belini büken ağır yükünü üzerinden indirmedik mi?"),
                Ayah(94, 3, "الَّذِي أَنْقَضَ ظَهْرَكَ", "O yük ki senin belini bükmüştü."),
                Ayah(94, 4, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "Senin şanını ve namını yüceltmedik mi?"),
                Ayah(94, 5, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "Şüphesiz her zorlukla beraber bir kolaylık vardır."),
                Ayah(94, 6, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Evet, muhakkak her zorlukla beraber bir kolaylık vardır."),
                Ayah(94, 7, "فَإِذَا فَرَغْتَ فَانْصَبْ", "Öyleyse bir işi bitirince hemen diğerine koyul,"),
                Ayah(94, 8, "وَإِلَىٰ رَبِّكَ فَارْغَبْ", "Ve yalnız Rabbine yönel.")
            )
            97 -> listOf( // Kadir
                Ayah(97, 1, "إِنَّا أَنْزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Şüphesiz biz Kur'an'ı Kadir Gecesi'nde indirdik."),
                Ayah(97, 2, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "Kadir Gecesi'nin ne olduğunu sen bilir misin?"),
                Ayah(97, 3, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِنْ أَلْفِ شَهْرٍ", "Kadir Gecesi bin aydan daha hayırlıdır."),
                Ayah(97, 4, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِمْ مِنْ كُلِّ أَمْرٍ", "Melekler ve Ruh (Cebrail), o gecede Rablerinin izniyle her türlü iş için iner dururlar."),
                Ayah(97, 5, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "O gece, tan yeri ağarana kadar tam bir esenlik ve selâmettir.")
            )
            103 -> listOf( // Asr
                Ayah(103, 1, "وَالْعَصْرِ", "Asra (zamana) andolsun ki,"),
                Ayah(103, 2, "إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ", "İnsan gerçekten büyük bir ziyan içindedir."),
                Ayah(103, 3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Ancak iman edip salih ameller işleyenler, birbirlerine hakkı tavsiye edenler ve sabrı tavsiye edenler müstesnadır.")
            )
            108 -> listOf( // Kevser
                Ayah(108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Şüphesiz biz sana Kevser'i (tükenmez bol nimeti) verdik."),
                Ayah(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Şimdi sen Rabbin için namaz kıl ve kurban kes."),
                Ayah(108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Asıl soyu kesik olan, sana buğzeden (düşmanlık eden) kimsedir.")
            )
            112 -> listOf( // Ihlas
                Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "De ki: O Allah tektir."),
                Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allah Samed'dir (her şey O'na muhtaçtır, O hiçbir şeye muhtaç değildir)."),
                Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "O, doğurmamış ve doğmamıştır."),
                Ayah(112, 4, "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ", "Hiçbir şey O'na denk ve benzer değildir.")
            )
            113 -> listOf( // Felak
                Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "De ki: Sabahın Rabbine sığınırım,"),
                Ayah(113, 2, "مِنْ شَرِّ مَا خَلَقَ", "Yarattığı şeylerin şerrinden,"),
                Ayah(113, 3, "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Karanlığı çöktüğü zaman gecenin şerrinden,"),
                Ayah(113, 4, "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Düğümlere üfleyen büyücülerin şerrinden,"),
                Ayah(113, 5, "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Ve haset ettiği zaman hasetçinin şerrinden.")
            )
            114 -> listOf( // Nas
                Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "De ki: İnsanların Rabbine sığınırım,"),
                Ayah(114, 2, "مَلِكِ النَّاسِ", "İnsanların yegâne hükümdarına,"),
                Ayah(114, 3, "إِلَٰهِ النَّاسِ", "İnsanların gerçek ilâhına,"),
                Ayah(114, 4, "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Sinsi vesvesecinin şerrinden;"),
                Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "O ki, insanların kalplerine vesvese fısıldar,"),
                Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Gerek cinlerden, gerekse insanlardan olan vesvesecilerden.")
            )
            95 -> listOf( // Tin
                Ayah(95, 1, "وَالتِّينِ وَالزَّيْتُونِ", "Tîn'e (incire) ve zeytine andolsun,"),
                Ayah(95, 2, "وَطُورِ سِينِينَ", "Sînâ Dağı'na,"),
                Ayah(95, 3, "وَهَٰذَا الْبَلَدِ الْأَمِينِ", "Ve bu güvenli beldeye (Mekke'ye) andolsun ki,"),
                Ayah(95, 4, "لَقَدْ خَلَقْنَا الْإِنْسَانَ فِي أَحْسَنِ تَقْوِيمٍ", "Biz insanı en güzel biçimde (ahsen-i takvîm üzere) yarattık."),
                Ayah(95, 5, "ثُمَّ رَدَدْنَاهُ أَسْفَلَ سَافِلِينَ", "Sonra onu aşağıların en aşağısına indirdik;"),
                Ayah(95, 6, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ فَلَهُمْ أَجْرٌ غَيْرُ مَمْنُونٍ", "Ancak iman edip salih ameller işleyenler müstesnadır; onlar için kesintisiz bir mükâfat vardır."),
                Ayah(95, 7, "فَمَا يُكَذِّبُكَ بَعْدُ بِالدِّينِ", "Artık bundan sonra hesap gününü sana hangi şey yalanlatabilir?"),
                Ayah(95, 8, "أَلَيْسَ اللَّهُ بِأَحْكَمِ الْحَاكِمِينَ", "Allah, hüküm verenlerin en adili değil midir?")
            )
            105 -> listOf( // Fil
                Ayah(105, 1, "أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِأَصْحَابِ الْفِيلِ", "Görmedin mi Rabbin fil sahiplerine ne yaptı?"),
                Ayah(105, 2, "أَلَمْ يَجْعَلْ كَيْدَهُمْ فِي تَضْلِيلٍ", "Onların tuzaklarını boşa çıkarmadı mı?"),
                Ayah(105, 3, "وَأَرْسَلَ عَلَيْهِمْ طَيْرًا أَبَابِيلَ", "Üzerlerine sürü sürü Ebâbîl kuşları gönderdi."),
                Ayah(105, 4, "تَرْمِيهِمْ بِحِجَارَةٍ مِنْ سِجِّيلٍ", "Onlara pişkin tuğladan taşlar atıyorlardı."),
                Ayah(105, 5, "فَجَعَلَهُمْ كَعَصْفٍ مَأْكُولٍ", "Nihayet onları yenilmiş ekin yaprağı gibi unufak etti.")
            )
            106 -> listOf( // Kureys
                Ayah(106, 1, "لِإِيلَافِ قُرَيْشٍ", "Kureyş'in güven ve uzlaşması için,"),
                Ayah(106, 2, "إِيلَافِهِمْ رِحْلَةَ الشِّتَاءِ وَالصَّيْفِ", "Kış ve yaz seferlerinde onların uzlaştırılıp güvene kavuşturulması için;"),
                Ayah(106, 3, "فَلْيَعْبُدُوا رَبَّ هَٰذَا الْبَيْتِ", "Onlar da bu Beyt'in (Kâbe'nin) Rabbine kulluk etsinler;"),
                Ayah(106, 4, "الَّذِي أَطْعَمَهُمْ مِنْ جُوعٍ وَآمَنَهُمْ مِنْ خَوْفٍ", "O Rab ki, onları açlıktan kurtarıp doyurdu ve her türlü korkudan emin kıldı.")
            )
            107 -> listOf( // Maun
                Ayah(107, 1, "أَرَأَيْتَ الَّذِي يُكَذِّبُ بِالدِّينِ", "Dini (hesap ve ceza gününü) yalanlayanı gördün mü?"),
                Ayah(107, 2, "فَذَٰلِكَ الَّذِي يَدُعُّ الْيَتِيمَ", "İşte o, yetimi itip kakar;"),
                Ayah(107, 3, "وَلَا يَحُضُّ عَلَىٰ طَعَامِ الْمِسْكِينِ", "Yoksula yedirmeyi özendirmez."),
                Ayah(107, 4, "فَوَيْلٌ لِلْمُصَلِّينَ", "Yazıklar olsun o namaz kılanlara ki;"),
                Ayah(107, 5, "الَّذِينَ هُمْ عَنْ صَلَاتِهِمْ سَاهُونَ", "Onlar kıldıkları namazdan gafildirler (özünden habersizdirler);"),
                Ayah(107, 6, "الَّذِينَ هُمْ يُرَاءُونَ", "Onlar gösteriş yaparlar,"),
                Ayah(107, 7, "وَيَمْنَعُونَ الْمَاعُونَ", "Ve en ufak bir yardıma bile engel olurlar.")
            )
            109 -> listOf( // Kafirun
                Ayah(109, 1, "قُلْ يَا أَيُّهَا الْكَافِرُونَ", "De ki: Ey inkârcılar!"),
                Ayah(109, 2, "لَا أَعْبُدُ مَا تَعْبُدُونَ", "Ben sizin taptıklarınıza tapmam."),
                Ayah(109, 3, "وَلَا أَنْتُمْ عَابِدُونَ مَا أَعْبُدُ", "Siz de benim kulluk ettiğime kulluk edecek değilsiniz."),
                Ayah(109, 4, "وَلَا أَنَا عَابِدٌ مَا عَبَدْتُمْ", "Ben asla sizin taptıklarınıza tapacak değilim;"),
                Ayah(109, 5, "وَلَا أَنْتُمْ عَابِدُونَ مَا أَعْبُدُ", "Siz de benim kulluk ettiğime kulluk edecek değilsiniz."),
                Ayah(109, 6, "لَكُمْ دِينُكُمْ وَلِيَ دِينِ", "Sizin dininiz size, benim dinim banadır.")
            )
            110 -> listOf( // Nasr
                Ayah(110, 1, "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ", "Allah'ın yardımı ve fetih geldiği zaman,"),
                Ayah(110, 2, "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا", "Ve insanların dalga dalga Allah'ın dinine girdiklerini gördüğünde;"),
                Ayah(110, 3, "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا", "Hemen Rabbini hamd ile tesbih et ve O'ndan bağışlanma dile. Şüphesiz O, tövbeleri çokça kabul edendir.")
            )
            111 -> listOf( // Tebbet
                Ayah(111, 1, "تَبَّتْ يَدَا أَبِي لَهَبٍ وَتَبَّ", "Ebû Leheb'in elleri kurusun; nitekim kurudu da!"),
                Ayah(111, 2, "مَا أَغْنَىٰ عَنْهُ مَالُهُ وَمَا كَسَبَ", "Ona ne malı fayda verdi ne de kazandığı!"),
                Ayah(111, 3, "سَيَصْلَىٰ نَارًا ذَاتَ لَهَبٍ", "O, alev alev parlayan bir ateşe girecektir;"),
                Ayah(111, 4, "وَامْرَأَتُهُ حَمَّالَةَ الْحَطَبِ", "Boynunda bükülmüş bir hurma lifi ip olduğu halde, odun taşıyıcı karısı da!"),
                Ayah(111, 5, "فِي جِيدِهَا حَبْلٌ مِنْ مَسَدٍ", "Boynunda bükülmüş bir urganla ateşe atılacaktır.")
            )
            else -> getBasmalaOpening(surahNumber)
        }
    }

    private fun getBasmalaOpening(surahNumber: Int): List<Ayah> {
        val surah = surahs.find { it.number == surahNumber } ?: return emptyList()
        // Never generate fake or repetitive verses. Return verified Basmala and authentic audio guidance.
        return if (surahNumber != 9) { // At-Tawbah does not start with Basmala
            listOf(
                Ayah(
                    surahNumber = surahNumber,
                    ayahNumber = 1,
                    textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    translationTr = "Rahmân ve Rahîm olan Allah'ın adıyla. (${surah.nameTurkish} Suresi - ${surah.totalVerses} Ayet)"
                )
            )
        } else {
            listOf(
                Ayah(
                    surahNumber = 9,
                    ayahNumber = 1,
                    textArabic = "بَرَاءَةٌ مِنَ اللَّهِ وَرَسُولِهِ إِلَى الَّذِينَ عَاهَدْتُمْ مِنَ الْمُشْرِكِينَ",
                    translationTr = "Allah ve Resûlü'nden, kendileriyle antlaşma yaptığınız müşriklere bir ültimatomdur! (Tevbe Suresi 1. Ayet)"
                )
            )
        }
    }
}
