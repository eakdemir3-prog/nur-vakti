package com.example.data

import com.example.model.City

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
        City("mekke", "Mekke-i Mükerreme", "Suudi Arabistan", 21.4225, 39.8262, 3.0, true),
        City("medine", "Medine-i Münevvere", "Suudi Arabistan", 24.5247, 39.5692, 3.0, true),
        City("kudus", "Kudüs (Mescid-i Aksa)", "Filistin", 31.7683, 35.2137, 3.0, true),
        City("kahire", "Kahire", "Mısır", 30.0444, 31.2357, 2.0),
        City("sam", "Şam", "Suriye", 33.5138, 36.2765, 3.0),
        City("bagdat", "Bağdat", "Irak", 33.3152, 44.3661, 3.0),
        City("berlin", "Berlin", "Almanya", 52.5200, 13.4050, 1.0),
        City("koln", "Köln", "Almanya", 50.9375, 6.9603, 1.0),
        City("londra", "Londra", "Birleşik Krallık", 51.5074, -0.1278, 0.0),
        City("paris", "Paris", "Fransa", 48.8566, 2.3522, 1.0),
        City("amsterdam", "Amsterdam", "Hollanda", 52.3676, 4.9041, 1.0),
        City("viyana", "Viyana", "Avusturya", 48.2082, 16.3738, 1.0),
        City("saraybosna", "Saraybosna", "Bosna-Hersek", 43.8563, 18.4131, 1.0),
        City("baku", "Bakü", "Azerbaycan", 40.4093, 49.8671, 4.0),
        City("newyork", "New York", "ABD", 40.7128, -74.0060, -5.0)
    )

    fun getAllCities(): List<City> = turkishCities + internationalCities

    fun search(query: String): List<City> {
        if (query.isBlank()) return getAllCities()
        val q = query.trim().lowercase()
        return getAllCities().filter {
            it.name.lowercase().contains(q) || it.country.lowercase().contains(q)
        }
    }

    fun findClosestCity(lat: Double, lng: Double): City {
        return getAllCities().minByOrNull { city ->
            val dLat = city.latitude - lat
            val dLng = city.longitude - lng
            dLat * dLat + dLng * dLng
        } ?: defaultCity
    }
}
