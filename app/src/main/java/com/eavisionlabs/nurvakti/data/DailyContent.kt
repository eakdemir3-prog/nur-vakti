package com.eavisionlabs.nurvakti.data

import android.content.Context
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.model.DuaItem
import java.util.Calendar

data class DailyVerse(val surah: Int, val ayah: Int, val arabic: String, val text: String, val reference: String)
data class Hadith(val text: String, val source: String)

/** Günün ayeti, hadisi ve duası — her gün değişir, gün içinde sabit kalır. */
object DailyContent {

    /** Seçilmiş ayetler (sure, ayet) */
    private val verseRefs = listOf(
        2 to 153, 2 to 186, 2 to 286, 3 to 139, 3 to 159, 3 to 173, 13 to 28, 94 to 5, 94 to 6, 65 to 3,
        65 to 2, 39 to 53, 2 to 152, 2 to 45, 29 to 69, 49 to 10, 49 to 13, 17 to 23, 17 to 24, 31 to 17,
        31 to 18, 16 to 90, 16 to 97, 4 to 36, 33 to 41, 3 to 200, 20 to 114, 40 to 60, 57 to 4, 50 to 16,
        6 to 162, 2 to 255, 21 to 87, 28 to 24, 9 to 51, 8 to 46, 3 to 134, 25 to 63, 41 to 34, 64 to 16,
        67 to 2, 18 to 10, 23 to 1, 24 to 35, 14 to 7, 7 to 56, 7 to 199, 10 to 62, 11 to 115, 12 to 87,
        13 to 11, 30 to 21, 42 to 43, 62 to 9, 29 to 45, 2 to 177, 3 to 92, 39 to 10, 20 to 25, 25 to 74
    )

    private val hadiths = listOf(
        Hadith("Ameller ancak niyetlere göredir. Herkese niyet ettiği şey vardır.", "Buhârî, Bed'ü'l-vahy 1; Müslim, İmâre 155"),
        Hadith("Kolaylaştırınız, zorlaştırmayınız; müjdeleyiniz, nefret ettirmeyiniz.", "Buhârî, İlim 11; Müslim, Cihâd 8"),
        Hadith("Sizin en hayırlınız Kur'an'ı öğrenen ve öğretendir.", "Buhârî, Fezâilü'l-Kur'ân 21"),
        Hadith("Sizden biriniz kendisi için istediğini kardeşi için de istemedikçe (gerçek anlamda) iman etmiş olmaz.", "Buhârî, Îmân 7; Müslim, Îmân 71"),
        Hadith("Müslüman, Müslümanların elinden ve dilinden emin olduğu kimsedir.", "Buhârî, Îmân 4; Müslim, Îmân 64"),
        Hadith("Allah'a ve âhiret gününe iman eden kimse ya hayır söylesin ya da sussun.", "Buhârî, Edeb 31; Müslim, Îmân 74"),
        Hadith("Temizlik imanın yarısıdır.", "Müslim, Tahâret 1"),
        Hadith("Hiçbir iyiliği küçük görme; kardeşini güler yüzle karşılaman bile bir iyiliktir.", "Müslim, Birr 144"),
        Hadith("Bir hayra vesile olan kimse, o hayrı yapan gibi sevap kazanır.", "Müslim, İmâre 133"),
        Hadith("Merhamet etmeyene merhamet edilmez.", "Buhârî, Edeb 18; Müslim, Fezâil 65"),
        Hadith("Allah sizin dış görünüşünüze ve mallarınıza bakmaz; kalplerinize ve amellerinize bakar.", "Müslim, Birr 34"),
        Hadith("Güçlü kimse güreşte rakibini yenen değil, öfkelendiği zaman kendine hâkim olandır.", "Buhârî, Edeb 76; Müslim, Birr 107"),
        Hadith("Sadaka malı eksiltmez.", "Müslim, Birr 69"),
        Hadith("Kişi sevdiğiyle beraberdir.", "Buhârî, Edeb 96; Müslim, Birr 165"),
        Hadith("Amellerin Allah'a en sevimlisi, az da olsa devamlı olanıdır.", "Buhârî, Rikak 18; Müslim, Müsâfirîn 218"),
        Hadith("İki nimet vardır ki insanların çoğu bunlarda aldanmıştır: Sağlık ve boş vakit.", "Buhârî, Rikak 1"),
        Hadith("Nerede olursan ol Allah'tan sakın. Kötülüğün ardından bir iyilik yap ki onu silsin. İnsanlara güzel ahlakla davran.", "Tirmizî, Birr 55"),
        Hadith("İman bakımından müminlerin en olgunu, ahlakı en güzel olanıdır.", "Tirmizî, Radâ 11; Ebû Dâvûd, Sünnet 15"),
        Hadith("Dua ibadetin ta kendisidir.", "Tirmizî, Deavât 1; Ebû Dâvûd, Vitir 23"),
        Hadith("İnsanlara teşekkür etmeyen, Allah'a da şükretmez.", "Tirmizî, Birr 35; Ebû Dâvûd, Edeb 11"),
        Hadith("Kim Ramazan orucunu inanarak ve sevabını Allah'tan umarak tutarsa geçmiş günahları bağışlanır.", "Buhârî, Îmân 28; Müslim, Müsâfirîn 175"),
        Hadith("Oruç bir kalkandır.", "Buhârî, Savm 2; Müslim, Sıyâm 162"),
        Hadith("Allah güzeldir, güzelliği sever.", "Müslim, Îmân 147"),
        Hadith("Hayâ imandandır.", "Buhârî, Îmân 16; Müslim, Îmân 57"),
        Hadith("Kim ilim öğrenmek için bir yola girerse, Allah ona cennete giden yolu kolaylaştırır.", "Müslim, Zikir 38"),
        Hadith("Müminin durumu ne hoştur! Her hâli kendisi için hayırlıdır. Sevindirici bir şeyle karşılaşınca şükreder, bu onun için hayır olur; başına bir sıkıntı gelince sabreder, bu da onun için hayır olur.", "Müslim, Zühd 64"),
        Hadith("Allah'a ve âhiret gününe iman eden kimse misafirine ikram etsin.", "Buhârî, Edeb 85; Müslim, Îmân 75"),
        Hadith("Kendisini ilgilendirmeyen şeyleri terk etmesi, kişinin Müslümanlığının güzelliğindendir.", "Tirmizî, Zühd 11; İbn Mâce, Fiten 12"),
        Hadith("Kim bir müminin dünya sıkıntılarından birini giderirse, Allah da onun kıyamet günü sıkıntılarından birini giderir.", "Müslim, Zikir 38"),
        Hadith("Müminler birbirlerini sevmede, birbirlerine merhamet etmede ve şefkat göstermede bir vücut gibidir. Vücudun bir organı rahatsızlanınca diğer organlar da bu yüzden uykusuzluğa ve ateşe tutulur.", "Buhârî, Edeb 27; Müslim, Birr 66"),
        Hadith("Birbirinize haset etmeyin, birbirinize kin tutmayın, birbirinize sırt çevirmeyin. Ey Allah'ın kulları, kardeş olun!", "Buhârî, Edeb 57; Müslim, Birr 23"),
        Hadith("Cennet annelerin ayakları altındadır.", "Nesâî, Cihâd 6 (mânen)"),
        Hadith("Sizden biriniz bir kötülük görürse onu eliyle düzeltsin; buna gücü yetmezse diliyle; buna da gücü yetmezse kalbiyle (karşı çıksın).", "Müslim, Îmân 78"),
        Hadith("Beş şey gelmeden önce beş şeyin kıymetini bil: İhtiyarlıktan önce gençliğin, hastalıktan önce sağlığın, fakirlikten önce zenginliğin, meşguliyetten önce boş vaktin ve ölümden önce hayatın.", "Hâkim, Müstedrek, IV, 341")
    )

    private val fridayHadiths = listOf(
        Hadith("Cuma gününde öyle bir saat vardır ki, Müslüman bir kul namaz kılarken o saate denk gelir de Allah'tan bir hayır dilerse, Allah onu mutlaka kendisine verir.", "Buhârî, Cuma 37; Müslim, Cuma 13"),
        Hadith("Güneşin doğduğu en hayırlı gün Cuma günüdür.", "Müslim, Cuma 17"),
        Hadith("Cuma günü bana çokça salavat getirin; çünkü salavatınız bana arz olunur.", "Ebû Dâvûd, Salât 207; Nesâî, Cuma 5")
    )

    private fun dayIndex(cal: Calendar = Calendar.getInstance()): Int =
        cal.get(Calendar.YEAR) * 400 + cal.get(Calendar.DAY_OF_YEAR)

    fun verseOfDay(context: Context, cal: Calendar = Calendar.getInstance()): DailyVerse? {
        val (s, a) = verseRefs[dayIndex(cal) % verseRefs.size]
        return verse(context, s, a)
    }

    fun verse(context: Context, s: Int, a: Int): DailyVerse? {
        QuranRepository.init(context)
        val ayah = QuranRepository.getAyah(s, a) ?: return null
        val surah = QuranRepository.surahs.getOrNull(s - 1)
        val name = surah?.nameTurkish ?: "$s"
        return DailyVerse(s, a, ayah.textArabic, ayah.translationTr, "$name, $a")
    }

    fun hadithOfDay(cal: Calendar = Calendar.getInstance()): Hadith = hadiths[(dayIndex(cal) * 7) % hadiths.size]

    fun duaOfDay(cal: Calendar = Calendar.getInstance()): DuaItem? {
        val all = DuaRepository.allDuas
        return if (all.isEmpty()) null else all[(dayIndex(cal) * 3) % all.size]
    }

    fun fridayMessage(@Suppress("UNUSED_PARAMETER") context: Context, cal: Calendar = Calendar.getInstance()): String {
        val h = fridayHadiths[(dayIndex(cal) / 7) % fridayHadiths.size]
        return if (I18n.language == "tr") "${h.text} (${h.source})" else "Jumu'ah Mubarak!"
    }

    /** Paylaşılabilir tebrik mesajları (cuma ve dini günler). */
    fun greetings(eventTitle: String?): List<String> {
        val title = eventTitle ?: "Cuma"
        return if (eventTitle == null) listOf(
            "Hayırlı Cumalar! Rabbim dualarımızı kabul, kalplerimizi huzurla doldursun.",
            "Cumanız mübarek olsun. Bu mübarek günde dualarınızın kabul olmasını dilerim.",
            "Rabbim bu Cuma'nın hürmetine gönlünüzü ferahlatsın, hayırlı işlerinizi bereketli kılsın. Hayırlı Cumalar."
        ) else listOf(
            "$title mübarek olsun. Rabbim dualarımızı kabul eylesin.",
            "$title hayırlara vesile olsun; gönlünüz huzur, haneniz bereketle dolsun.",
            "Bu mübarek gün ve gecenin hürmetine Rabbim bizleri affına mazhar eylesin. $title mübarek olsun."
        )
    }
}
