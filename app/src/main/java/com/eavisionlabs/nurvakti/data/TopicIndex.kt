package com.eavisionlabs.nurvakti.data

/** Konulu Kur'an fihristi: ruh haline ya da konuya göre seçilmiş ayetler (Diyanet meali cihazdan okunur). */
object TopicIndex {

    data class Topic(
        val id: String,
        val title: String,
        val subtitle: String,
        val refs: List<Pair<Int, Int>>
    )

    val topics = listOf(
        Topic("huzur", "Huzur", "Kalbin sükûnet bulması", listOf(13 to 28, 94 to 5, 94 to 6, 2 to 286, 48 to 4, 89 to 27, 89 to 28, 20 to 46)),
        Topic("sabir", "Sabır", "Zorluk ve imtihan anlarında", listOf(2 to 153, 2 to 155, 2 to 156, 3 to 200, 39 to 10, 16 to 127, 8 to 46, 103 to 3)),
        Topic("rizik", "Rızık ve Bereket", "Geçim, emek ve infak", listOf(11 to 6, 51 to 22, 51 to 58, 29 to 60, 14 to 7, 2 to 261)),
        Topic("sifa", "Şifa", "Hastalık ve iyileşme", listOf(17 to 82, 10 to 57, 26 to 80, 41 to 44, 16 to 69)),
        Topic("tovbe", "Tövbe ve Bağışlanma", "Allah'a yeniden yönelmek", listOf(39 to 53, 66 to 8, 4 to 110, 25 to 70, 3 to 135)),
        Topic("umit", "Ümit ve Rahmet", "Umutsuzluğa düştüğünüzde", listOf(39 to 53, 12 to 87, 7 to 156, 93 to 3, 93 to 5, 94 to 5)),
        Topic("dua", "Dua", "Allah'a yakarış", listOf(2 to 186, 40 to 60, 7 to 55, 27 to 62, 2 to 201, 14 to 40, 14 to 41)),
        Topic("sukur", "Şükür", "Nimetlerin farkında olmak", listOf(14 to 7, 2 to 152, 31 to 12, 16 to 18)),
        Topic("tevekkul", "Tevekkül", "Allah'a güvenip dayanmak", listOf(33 to 3, 9 to 51, 3 to 159, 3 to 173)),
        Topic("aile", "Aile, Eş ve Çocuk", "Sevgi, merhamet ve sorumluluk", listOf(30 to 21, 25 to 74, 4 to 1, 66 to 6, 14 to 40)),
        Topic("anne_baba", "Anne ve Baba", "Anne babaya iyilik", listOf(17 to 23, 17 to 24, 31 to 14, 46 to 15, 14 to 41)),
        Topic("olum", "Ölüm ve Ahiret", "Hayatın geçiciliği", listOf(3 to 185, 29 to 57, 21 to 35, 62 to 8, 67 to 2)),
        Topic("adalet", "Adalet", "Hakkı gözetmek", listOf(4 to 135, 5 to 8, 16 to 90)),
        Topic("ilim", "İlim", "Okumak ve öğrenmek", listOf(96 to 1, 20 to 114, 39 to 9, 58 to 11))
    )
}
