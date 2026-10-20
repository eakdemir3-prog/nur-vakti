package com.eavisionlabs.nurvakti.data

import android.content.Context
import com.eavisionlabs.nurvakti.model.City
import java.util.Locale

object CityProvider {

    val defaultCity = City("istanbul", "İstanbul", "Türkiye", 41.0082, 28.9784, 3.0, true)

    val turkishCities = listOf(
        City("adana", "Adana", "Türkiye", 37.0000, 35.3213, 3.0),
        City("adiyaman", "Adıyaman", "Türkiye", 37.7648, 38.2786, 3.0),
        City("afyonkarahisar", "Afyonkarahisar", "Türkiye", 38.7507, 30.5567, 3.0),
        City("agri", "Ağrı", "Türkiye", 39.7191, 43.0503, 3.0),
        City("aksaray", "Aksaray", "Türkiye", 38.3687, 34.0370, 3.0),
        City("amasya", "Amasya", "Türkiye", 40.6501, 35.8353, 3.0),
        City("ankara", "Ankara", "Türkiye", 39.9334, 32.8597, 3.0, true),
        City("antalya", "Antalya", "Türkiye", 36.8969, 30.7133, 3.0, true),
        City("ardahan", "Ardahan", "Türkiye", 41.1105, 42.7022, 3.0),
        City("artvin", "Artvin", "Türkiye", 41.1828, 41.8183, 3.0),
        City("aydin", "Aydın", "Türkiye", 37.8560, 27.8416, 3.0),
        City("balikesir", "Balıkesir", "Türkiye", 39.6484, 27.8826, 3.0),
        City("bartin", "Bartın", "Türkiye", 41.6344, 32.3375, 3.0),
        City("batman", "Batman", "Türkiye", 37.8812, 41.1294, 3.0),
        City("bayburt", "Bayburt", "Türkiye", 40.2552, 40.2249, 3.0),
        City("bilecik", "Bilecik", "Türkiye", 40.1451, 29.9799, 3.0),
        City("bingol", "Bingöl", "Türkiye", 38.8854, 40.4983, 3.0),
        City("bitlis", "Bitlis", "Türkiye", 38.4006, 42.1095, 3.0),
        City("bolu", "Bolu", "Türkiye", 40.7358, 31.6061, 3.0),
        City("burdur", "Burdur", "Türkiye", 37.7203, 30.2908, 3.0),
        City("bursa", "Bursa", "Türkiye", 40.1885, 29.0610, 3.0, true),
        City("canakkale", "Çanakkale", "Türkiye", 40.1553, 26.4142, 3.0),
        City("cankiri", "Çankırı", "Türkiye", 40.6013, 33.6134, 3.0),
        City("corum", "Çorum", "Türkiye", 40.5506, 34.9556, 3.0),
        City("denizli", "Denizli", "Türkiye", 37.7765, 29.0864, 3.0),
        City("diyarbakir", "Diyarbakır", "Türkiye", 37.9144, 40.2306, 3.0),
        City("duzce", "Düzce", "Türkiye", 40.8438, 31.1565, 3.0),
        City("edirne", "Edirne", "Türkiye", 41.6772, 26.5557, 3.0),
        City("elazig", "Elazığ", "Türkiye", 38.6810, 39.2264, 3.0),
        City("erzincan", "Erzincan", "Türkiye", 39.7500, 39.5000, 3.0),
        City("erzurum", "Erzurum", "Türkiye", 39.9043, 41.2679, 3.0),
        City("eskisehir", "Eskişehir", "Türkiye", 39.7767, 30.5206, 3.0),
        City("gaziantep", "Gaziantep", "Türkiye", 37.0662, 37.3833, 3.0),
        City("giresun", "Giresun", "Türkiye", 40.9128, 38.3895, 3.0),
        City("gumushane", "Gümüşhane", "Türkiye", 40.4600, 39.4814, 3.0),
        City("hakkari", "Hakkâri", "Türkiye", 37.5833, 43.7333, 3.0),
        City("hatay", "Hatay", "Türkiye", 36.4018, 36.3498, 3.0),
        City("igdir", "Iğdır", "Türkiye", 39.9196, 44.0450, 3.0),
        City("isparta", "Isparta", "Türkiye", 37.7648, 30.5566, 3.0),
        City("istanbul", "İstanbul", "Türkiye", 41.0082, 28.9784, 3.0, true),
        City("izmir", "İzmir", "Türkiye", 38.4192, 27.1287, 3.0, true),
        City("kahramanmaras", "Kahramanmaraş", "Türkiye", 37.5858, 36.9371, 3.0),
        City("karabuk", "Karabük", "Türkiye", 41.2061, 32.6204, 3.0),
        City("karaman", "Karaman", "Türkiye", 37.1759, 33.2287, 3.0),
        City("kars", "Kars", "Türkiye", 40.6167, 43.1000, 3.0),
        City("kastamonu", "Kastamonu", "Türkiye", 41.3887, 33.7827, 3.0),
        City("kayseri", "Kayseri", "Türkiye", 38.7312, 35.4787, 3.0),
        City("kilis", "Kilis", "Türkiye", 36.7184, 37.1212, 3.0),
        City("kirikkale", "Kırıkkale", "Türkiye", 39.8468, 33.5153, 3.0),
        City("kirklareli", "Kırklareli", "Türkiye", 41.7333, 27.2167, 3.0),
        City("kirsehir", "Kırşehir", "Türkiye", 39.1425, 34.1709, 3.0),
        City("kocaeli", "Kocaeli", "Türkiye", 40.8533, 29.8815, 3.0),
        City("konya", "Konya", "Türkiye", 37.8667, 32.4833, 3.0, true),
        City("kutahya", "Kütahya", "Türkiye", 39.4167, 29.9833, 3.0),
        City("malatya", "Malatya", "Türkiye", 38.3552, 38.3095, 3.0),
        City("manisa", "Manisa", "Türkiye", 38.6191, 27.4289, 3.0),
        City("mardin", "Mardin", "Türkiye", 37.3212, 40.7245, 3.0),
        City("mersin", "Mersin", "Türkiye", 36.8000, 34.6333, 3.0),
        City("mugla", "Muğla", "Türkiye", 37.2153, 28.3636, 3.0),
        City("mus", "Muş", "Türkiye", 38.7432, 41.5064, 3.0),
        City("nevsehir", "Nevşehir", "Türkiye", 38.6244, 34.7144, 3.0),
        City("nigde", "Niğde", "Türkiye", 37.9667, 34.6833, 3.0),
        City("ordu", "Ordu", "Türkiye", 40.9839, 37.8764, 3.0),
        City("osmaniye", "Osmaniye", "Türkiye", 37.0742, 36.2467, 3.0),
        City("rize", "Rize", "Türkiye", 41.0201, 40.5234, 3.0),
        City("sakarya", "Sakarya", "Türkiye", 40.7569, 30.3783, 3.0),
        City("samsun", "Samsun", "Türkiye", 41.2928, 36.3313, 3.0),
        City("sanliurfa", "Şanlıurfa", "Türkiye", 37.1591, 38.7969, 3.0),
        City("siirt", "Siirt", "Türkiye", 37.9333, 41.9500, 3.0),
        City("sinop", "Sinop", "Türkiye", 42.0231, 35.1531, 3.0),
        City("sivas", "Sivas", "Türkiye", 39.7477, 37.0179, 3.0),
        City("sirnak", "Şırnak", "Türkiye", 37.5164, 42.4918, 3.0),
        City("tekirdag", "Tekirdağ", "Türkiye", 40.9833, 27.5167, 3.0),
        City("tokat", "Tokat", "Türkiye", 40.3167, 36.5500, 3.0),
        City("trabzon", "Trabzon", "Türkiye", 41.0015, 39.7178, 3.0),
        City("tunceli", "Tunceli", "Türkiye", 39.1079, 39.5401, 3.0),
        City("usak", "Uşak", "Türkiye", 38.6823, 29.4082, 3.0),
        City("van", "Van", "Türkiye", 38.4891, 43.4089, 3.0),
        City("yalova", "Yalova", "Türkiye", 40.6500, 29.2667, 3.0),
        City("yozgat", "Yozgat", "Türkiye", 39.8181, 34.8147, 3.0),
        City("zonguldak", "Zonguldak", "Türkiye", 41.4564, 31.7987, 3.0)
    )

    val internationalCities = listOf(
        City("mekke", "Mekke-i Mükerreme", "Suudi Arabistan", 21.4225, 39.8262, 3.0, true, "Asia/Riyadh"),
        City("medine", "Medine-i Münevvere", "Suudi Arabistan", 24.5247, 39.5692, 3.0, true, "Asia/Riyadh"),
        City("kudus", "Kudüs (Mescid-i Aksa)", "Filistin", 31.7683, 35.2137, 2.0, true, "Asia/Jerusalem"),
        City("kahire", "Kahire", "Mısır", 30.0444, 31.2357, 2.0, false, "Africa/Cairo"),
        City("sam", "Şam", "Suriye", 33.5138, 36.2765, 3.0, false, "Asia/Damascus"),
        City("bagdat", "Bağdat", "Irak", 33.3152, 44.3661, 3.0, false, "Asia/Baghdad"),
        City("lefkosa", "Lefkoşa", "KKTC", 35.1856, 33.3823, 3.0, false, "Asia/Nicosia"),
        City("girne", "Girne", "KKTC", 35.3364, 33.3182, 3.0, false, "Asia/Nicosia"),
        City("gazimagusa", "Gazimağusa", "KKTC", 35.1250, 33.9417, 3.0, false, "Asia/Nicosia"),
        City("baku", "Bakü", "Azerbaycan", 40.4093, 49.8671, 4.0, false, "Asia/Baku"),
        City("tiflis", "Tiflis", "Gürcistan", 41.7151, 44.8271, 4.0, false, "Asia/Tbilisi"),
        City("batum", "Batum", "Gürcistan", 41.6168, 41.6367, 4.0, false, "Asia/Tbilisi"),
        City("uskup", "Üsküp", "Kuzey Makedonya", 41.9981, 21.4254, 1.0, false, "Europe/Skopje"),
        City("saraybosna", "Saraybosna", "Bosna-Hersek", 43.8563, 18.4131, 1.0, false, "Europe/Sarajevo"),
        City("priştine", "Priştine", "Kosova", 42.6629, 21.1655, 1.0, false, "Europe/Belgrade"),
        City("sofya", "Sofya", "Bulgaristan", 42.6977, 23.3219, 2.0, false, "Europe/Sofia"),
        City("kircaali", "Kırcaali", "Bulgaristan", 41.6338, 25.3777, 2.0, false, "Europe/Sofia"),
        City("gumulcine", "Gümülcine", "Yunanistan", 41.1222, 25.4064, 2.0, false, "Europe/Athens"),
        City("berlin", "Berlin", "Almanya", 52.5200, 13.4050, 1.0, false, "Europe/Berlin"),
        City("koln", "Köln", "Almanya", 50.9375, 6.9603, 1.0, false, "Europe/Berlin"),
        City("hamburg", "Hamburg", "Almanya", 53.5511, 9.9937, 1.0, false, "Europe/Berlin"),
        City("munih", "Münih", "Almanya", 48.1351, 11.5820, 1.0, false, "Europe/Berlin"),
        City("frankfurt", "Frankfurt", "Almanya", 50.1109, 8.6821, 1.0, false, "Europe/Berlin"),
        City("stuttgart", "Stuttgart", "Almanya", 48.7758, 9.1829, 1.0, false, "Europe/Berlin"),
        City("dusseldorf", "Düsseldorf", "Almanya", 51.2277, 6.7735, 1.0, false, "Europe/Berlin"),
        City("duisburg", "Duisburg", "Almanya", 51.4344, 6.7623, 1.0, false, "Europe/Berlin"),
        City("viyana", "Viyana", "Avusturya", 48.2082, 16.3738, 1.0, false, "Europe/Vienna"),
        City("zurih", "Zürih", "İsviçre", 47.3769, 8.5417, 1.0, false, "Europe/Zurich"),
        City("amsterdam", "Amsterdam", "Hollanda", 52.3676, 4.9041, 1.0, false, "Europe/Amsterdam"),
        City("rotterdam", "Rotterdam", "Hollanda", 51.9244, 4.4777, 1.0, false, "Europe/Amsterdam"),
        City("bruksel", "Brüksel", "Belçika", 50.8503, 4.3517, 1.0, false, "Europe/Brussels"),
        City("paris", "Paris", "Fransa", 48.8566, 2.3522, 1.0, false, "Europe/Paris"),
        City("strazburg", "Strazburg", "Fransa", 48.5734, 7.7521, 1.0, false, "Europe/Paris"),
        City("lyon", "Lyon", "Fransa", 45.7640, 4.8357, 1.0, false, "Europe/Paris"),
        City("londra", "Londra", "Birleşik Krallık", 51.5074, -0.1278, 0.0, false, "Europe/London"),
        City("stockholm", "Stockholm", "İsveç", 59.3293, 18.0686, 1.0, false, "Europe/Stockholm"),
        City("kopenhag", "Kopenhag", "Danimarka", 55.6761, 12.5683, 1.0, false, "Europe/Copenhagen"),
        City("moskova", "Moskova", "Rusya", 55.7558, 37.6173, 3.0, false, "Europe/Moscow"),
        City("kazan", "Kazan", "Rusya", 55.7963, 49.1088, 3.0, false, "Europe/Moscow"),
        City("taskent", "Taşkent", "Özbekistan", 41.2995, 69.2401, 5.0, false, "Asia/Tashkent"),
        City("almati", "Almatı", "Kazakistan", 43.2220, 76.8512, 5.0, false, "Asia/Almaty"),
        City("biskek", "Bişkek", "Kırgızistan", 42.8746, 74.5698, 6.0, false, "Asia/Bishkek"),
        City("asgabat", "Aşkabat", "Türkmenistan", 37.9601, 58.3261, 5.0, false, "Asia/Ashgabat"),
        City("doha", "Doha", "Katar", 25.2854, 51.5310, 3.0, false, "Asia/Qatar"),
        City("dubai", "Dubai", "BAE", 25.2048, 55.2708, 4.0, false, "Asia/Dubai"),
        City("kuveyt", "Kuveyt", "Kuveyt", 29.3759, 47.9774, 3.0, false, "Asia/Kuwait"),
        City("tahran", "Tahran", "İran", 35.6892, 51.3890, 3.5, false, "Asia/Tehran"),
        City("amman", "Amman", "Ürdün", 31.9539, 35.9106, 3.0, false, "Asia/Amman"),
        City("beyrut", "Beyrut", "Lübnan", 33.8938, 35.5018, 2.0, false, "Asia/Beirut"),
        City("erbil", "Erbil", "Irak", 36.1911, 44.0092, 3.0, false, "Asia/Baghdad"),
        City("kerkuk", "Kerkük", "Irak", 35.4681, 44.3922, 3.0, false, "Asia/Baghdad"),
        City("tunus", "Tunus", "Tunus", 36.8065, 10.1815, 1.0, false, "Africa/Tunis"),
        City("kazablanka", "Kazablanka", "Fas", 33.5731, -7.5898, 1.0, false, "Africa/Casablanca"),
        City("islamabad", "İslamabad", "Pakistan", 33.6844, 73.0479, 5.0, false, "Asia/Karachi"),
        City("kualalumpur", "Kuala Lumpur", "Malezya", 3.1390, 101.6869, 8.0, false, "Asia/Kuala_Lumpur"),
        City("cakarta", "Cakarta", "Endonezya", -6.2088, 106.8456, 7.0, false, "Asia/Jakarta"),
        City("newyork", "New York", "ABD", 40.7128, -74.0060, -5.0, false, "America/New_York"),
        City("chicago", "Chicago", "ABD", 41.8781, -87.6298, -6.0, false, "America/Chicago"),
        City("losangeles", "Los Angeles", "ABD", 34.0522, -118.2437, -8.0, false, "America/Los_Angeles"),
        City("toronto", "Toronto", "Kanada", 43.6532, -79.3832, -5.0, false, "America/Toronto"),
        City("sidney", "Sidney", "Avustralya", -33.8688, 151.2093, 10.0, false, "Australia/Sydney")
    )

    private var appContext: Context? = null
    private val districtCache: List<City> by lazy { loadDistricts() }

    /** Uygulama açılışında bir kez çağrılır; ilçe listesi assets/districts.txt'den tembel yüklenir. */
    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    private fun loadDistricts(): List<City> {
        val ctx = appContext ?: return emptyList()
        val provinces = turkishCities.associateBy { it.id }
        return try {
            ctx.assets.open("districts.txt").bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.mapNotNull { line ->
                    val p = line.split('|')
                    if (p.size < 4) return@mapNotNull null
                    val prov = provinces[p[0]] ?: return@mapNotNull null
                    City(
                        id = "${p[0]}_${slug(p[1])}",
                        name = p[1],
                        country = "Türkiye",
                        latitude = p[2].toDouble(),
                        longitude = p[3].toDouble(),
                        province = prov.name
                    )
                }.toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun slug(s: String): String = s.lowercase(Locale.ROOT)
        .replace('ç', 'c').replace('ğ', 'g').replace('ı', 'i')
        .replace('ö', 'o').replace('ş', 's').replace('ü', 'u').replace('â', 'a').replace('î', 'i')
        .filter { it in 'a'..'z' }

    /** İl merkezleri + yurt dışı şehirler (ilçeler hariç). */
    fun getAllCities(): List<City> = turkishCities + internationalCities

    fun getDistricts(): List<City> = districtCache

    fun findById(id: String): City? =
        getAllCities().find { it.id == id } ?: districtCache.find { it.id == id }

    /** Aramada il, ilçe ve ülke adlarına bakılır; boş sorguda yalnızca il merkezleri döner. */
    fun search(query: String): List<City> {
        if (query.isBlank()) return getAllCities()
        val q = fold(query.trim())
        val all = getAllCities() + districtCache
        val starts = all.filter { fold(it.name).startsWith(q) }
        val contains = all.filter { c ->
            c !in starts && (fold(c.name).contains(q) || fold(c.province).contains(q) || fold(c.country).contains(q))
        }
        return (starts + contains).take(150)
    }

    private fun fold(s: String): String = s.lowercase(Locale("tr"))
        .replace('ç', 'c').replace('ğ', 'g').replace('ı', 'i').replace('ö', 'o')
        .replace('ş', 's').replace('ü', 'u').replace('â', 'a').replace('î', 'i')

    fun findClosestCity(lat: Double, lng: Double): City {
        val all = getAllCities() + districtCache.filter { it.latitude != 0.0 }
        return all.minByOrNull { city ->
            val dLat = city.latitude - lat
            val dLng = (city.longitude - lng) * kotlin.math.cos(Math.toRadians(lat))
            dLat * dLat + dLng * dLng
        } ?: defaultCity
    }
}
