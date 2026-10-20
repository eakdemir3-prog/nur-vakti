package com.eavisionlabs.nurvakti.data

import com.eavisionlabs.nurvakti.model.DuaCategory
import com.eavisionlabs.nurvakti.model.DuaItem

object DuaRepository {

    val categories: List<DuaCategory> = listOf(
        DuaCategory("sabah", "Sabah Duaları", "wb_sunny"),
        DuaCategory("aksam", "Akşam Duaları", "nights_stay"),
        DuaCategory("yemek", "Yemek Duaları", "restaurant"),
        DuaCategory("yolculuk", "Yolculuk Duaları", "directions_car"),
        DuaCategory("sifa", "Şifa Duaları", "healing"),
        DuaCategory("huzur", "Huzur ve Sabır Duaları", "spa"),
        DuaCategory("namaz", "Namaz Sonrası Dualar", "mosque"),
        DuaCategory("kuran", "Kur'an'da Geçen Dualar", "menu_book"),
        DuaCategory("peygamber", "Peygamberlerin Duaları", "auto_awesome"),
        DuaCategory("gunluk", "Günlük Hayat Duaları", "calendar_today")
    )

    val allDuas: List<DuaItem> = listOf(
        // Sabah
        DuaItem(
            id = "s1",
            categoryId = "sabah",
            titleTr = "Sabaha Ulaşma Duası",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            pronunciationTr = "Asbahnâ ve asbahal mülkü lillâhi vel hamdü lillâh, lâ ilâhe illallâhu vahdehû lâ şerîke leh.",
            meaningTr = "Biz de sabaha erdik, bütün mülk de Allah için sabaha erdi. Hamd Allah'a mahsustur. Allah'tan başka ilâh yoktur; O tektir, ortağı yoktur.",
            source = "Müslim, Zikir 75; Ebû Dâvûd, Edeb 101"
        ),
        DuaItem(
            id = "s2",
            categoryId = "sabah",
            titleTr = "Seyyidü'l-İstiğfar (Tevbelerin Efendisi)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ",
            pronunciationTr = "Allâhümme ente Rabbî lâ ilâhe illâ ente, halaktenî ve ene abdüke ve ene alâ ahdike ve va'dike mesteta'tü, eûzü bike min şerri mâ sana'tü.",
            meaningTr = "Allah'ım! Sen benim Rabbimsin; Sen'den başka ilâh yoktur. Beni Sen yarattın, ben Sen'in kulunum. Gücüm yettiğince ahdine ve vadine bağlıyım. Yaptıklarımın şerrinden Sana sığınırım.",
            source = "Buhârî, Deavât 2; Tirmizî, Deavât 15"
        ),

        // Akşam
        DuaItem(
            id = "a1",
            categoryId = "aksam",
            titleTr = "Akşam Emniyet Duası",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            pronunciationTr = "Emseynâ ve emsel mülkü lillâhi velhamdü lillâh, lâ ilâhe illallâhu vahdehû lâ şerîke leh.",
            meaningTr = "Biz akşama erdik, mülk de Allah'a ait olarak akşama erdi. Hamd Allah'adır, O'ndan başka ilâh yoktur.",
            source = "Müslim, Zikir 76"
        ),
        DuaItem(
            id = "a2",
            categoryId = "aksam",
            titleTr = "Her Türlü Kötülükten Korunma",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            pronunciationTr = "Bismillâhillezî lâ yedurru measmihî şey'ün fil ardı velâ fissemâi ve hüves-semîul alîm.",
            meaningTr = "İsmiyle yerde ve gökte hiçbir şeyin zarar veremeyeceği Allah'ın adıyla. O hakkıyla işiten ve bilendir.",
            source = "Ebû Dâvûd, Edeb 101; Tirmizî, Deavât 8"
        ),

        // Yemek
        DuaItem(
            id = "y1",
            categoryId = "yemek",
            titleTr = "Yemekten Sonra Şükür Duası",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            pronunciationTr = "Elhamdü lillâhillezî et'amenâ ve sekânâ ve cealenâ müslimîn.",
            meaningTr = "Bizi doyuran, bizi içiren ve bizi Müslümanlardan kılan Allah'a hamdolsun.",
            source = "Ebû Dâvûd, Et'ime 52; Tirmizî, Deavât 55"
        ),

        // Yolculuk
        DuaItem(
            id = "yo1",
            categoryId = "yolculuk",
            titleTr = "Vasat ve Yolculuk Duası",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
            pronunciationTr = "Sübhânellezî sahhara lenâ hâzâ ve mâ künnâ lehû mukrinîn. Ve innâ ilâ Rabbinâ lemünkalibûn.",
            meaningTr = "Bunu bizim hizmetimize veren Allah'ın şanı ne yücedir! Yoksa bizim buna gücümüz yetmezdi. Şüphesiz biz Rabbimize döneceğiz.",
            source = "Zuhruf Suresi 13-14; Müslim, Hacc 425"
        ),

        // Şifa
        DuaItem(
            id = "si1",
            categoryId = "sifa",
            titleTr = "Hz. Peygamber'in Hastalara Okuduğu Şifa Duası",
            arabicText = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَأْسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            pronunciationTr = "Allâhümme Rabben-nâsi ezhibil-be's, işfi ente'ş-şâfî, lâ şifâe illâ şifâüke, şifâen lâ yüğâdiru sekamâ.",
            meaningTr = "Ey insanların Rabbi olan Allah'ım! Bu hastalığı gider, şifa ver! Şifa veren yalnız Sen'sin. Sen'in şifandan başka şifa yoktur; öyle bir şifa ver ki hiçbir hastalık bırakmasın.",
            source = "Buhârî, Tıb 38; Müslim, Selâm 46"
        ),

        // Huzur ve Sabır
        DuaItem(
            id = "hu1",
            categoryId = "huzur",
            titleTr = "Keder, Hüzün ve Borçtan Kurtulma",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
            pronunciationTr = "Allâhümme innî eûzü bike minel-hemmi vel-hazen, ve eûzü bike minel-aczi vel-kesel, ve eûzü bike minel-cübni vel-buhl, ve eûzü bike min ğalebetid-deyni ve kahrir-ricâl.",
            meaningTr = "Allah'ım! Gam ve kederden, acizlik ve tembellikten, korkaklık ve cimrilikten, borç altında ezilmekten ve insanların haksız baskısından Sana sığınırım.",
            source = "Ebû Dâvûd, Salât 367; Buhârî, Cihâd 74"
        ),

        // Namaz Sonrası
        DuaItem(
            id = "na1",
            categoryId = "namaz",
            titleTr = "Selam Sonrası Tesbihat Duası",
            arabicText = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالإِكْرَامِ",
            pronunciationTr = "Allâhümme ente's-selâmü ve minke's-selâm, tebârakte yâ zel-celâli vel-ikrâm.",
            meaningTr = "Allah'ım! Sen es-Selâm'sın (bütün noksanlıklardan uzaksın), selâmet ve huzur ancak Sen'dendir. Ey celâl ve ikram sahibi Rabbim! Sen yücelerden yücesin.",
            source = "Müslim, Mesâcid 135; Tirmizî, Salât 108"
        ),

        // Kur'an'da Geçen
        DuaItem(
            id = "ku1",
            categoryId = "kuran",
            titleTr = "Rabbena Âtina Duası",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            pronunciationTr = "Rabbenâ âtinâ fid-dünyâ haseneten ve fil-âhireti haseneten ve kınâ azâben-nâr.",
            meaningTr = "Rabbimiz! Bize dünyada da güzellik ver, ahirette de güzellik ver ve bizi cehennem ateşinin azabından koru.",
            source = "Bakara Suresi 201; Buhârî, Deavât 55"
        ),
        DuaItem(
            id = "ku2",
            categoryId = "kuran",
            titleTr = "Rabbenağfirlî Duası",
            arabicText = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
            pronunciationTr = "Rabbenâğfirlî ve livâlideyye ve lil-mü'minîne yevme yekûmul-hisâb.",
            meaningTr = "Rabbimiz! Hesabın görüleceği gün beni, anne-babamı ve bütün inananları bağışla.",
            source = "İbrahim Suresi 41"
        ),

        // Peygamber Duaları
        DuaItem(
            id = "pe1",
            categoryId = "peygamber",
            titleTr = "Hz. Yunus'un (a.s.) Sıkıntı Anındaki Duası",
            arabicText = "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            pronunciationTr = "Lâ ilâhe illâ ente sübhâneke innî küntü minez-zâlimîn.",
            meaningTr = "Sen'den başka hiçbir ilâh yoktur. Seni her türlü eksiklikten tenzih ederim. Gerçekten ben haksızlık edenlerden oldum.",
            source = "Enbiyâ Suresi 87; Tirmizî, Deavât 82"
        ),
        DuaItem(
            id = "pe2",
            categoryId = "peygamber",
            titleTr = "Hz. Eyyub'un (a.s.) Şifa Duası",
            arabicText = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنْتَ أَرْحَمُ الرَّاحِمِينَ",
            pronunciationTr = "Ennî messeniyez-durru ve ente erhamür-râhimîn.",
            meaningTr = "Başıma bir dert geldi; Sen ise merhametlilerin en merhametlisisin.",
            source = "Enbiyâ Suresi 83"
        ),

        // Günlük Hayat
        DuaItem(
            id = "gu1",
            categoryId = "gunluk",
            titleTr = "Evden Çıkarken Okunacak Dua",
            arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            pronunciationTr = "Bismillâhi tevekkeltü alallâh, lâ havle velâ kuvvete illâ billâh.",
            meaningTr = "Allah'ın adıyla, Allah'a tevekkül ettim. Güç ve kuvvet ancak Allah'ın yardımıyladır.",
            source = "Ebû Dâvûd, Edeb 102; Tirmizî, Deavât 34"
        )
    )

    fun getDuasByCategory(categoryId: String): List<DuaItem> {
        return allDuas.filter { it.categoryId == categoryId }
    }

    fun searchDuas(query: String): List<DuaItem> {
        if (query.isBlank()) return allDuas
        val q = query.trim().lowercase()
        return allDuas.filter {
            it.titleTr.lowercase().contains(q) ||
            it.meaningTr.lowercase().contains(q) ||
            it.pronunciationTr.lowercase().contains(q)
        }
    }
}
