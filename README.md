# NurVakti

Kur'an-ı Kerim, namaz vakitleri ve İslami yaşam uygulaması — EA Vision Labs.

## Özellikler (v2.0.0)

- **Namaz vakitleri** — Diyanet yöntemi (İmsak 18°, Yatsı 17°, asr-ı evvel, temkinler: Güneş −7, Öğle +5, İkindi +4, Akşam +7). Cihazda hesaplanır, yaz saati dahil. 81 il + ~920 ilçe + 60 yurt dışı şehir, GPS ile konum. Vakit başına dakika düzeltmesi ve hicri takvim düzeltmesi.
- **Ezan ve bildirimler** — her vakit için Kapalı / Ezan / Sesli / Sessiz / Titreşim; vakitten önce hatırlatma (5–45 dk); telefon yeniden başlasa da çalışır. Ezan sesi olarak telefondan bir ses dosyası seçilebilir (yoksa alarm sesi).
- **Bildirim çubuğunda kalıcı geri sayım** ve **ana ekran widget'ı** (sıradaki vakit + 6 vakit).
- **Kur'an-ı Kerim'in tamamı** (114 sure, 6236 ayet) internetsiz: Arapça metin (Amiri Quran yazı tipi), Diyanet meali, İngilizce meal. Cüz cüz okuma, yer imleri, kaldığın yer, mealde arama, günlük sayfa hedefi, hatim ilerlemesi.
- **Ayet ayet dinleme** (5 hafız) ve okuyucuda takip; sure dinleme (4 hafız).
- **Namaz takibi** — günlük işaretleme, 7/30 günlük grafik, seri.
- **Ramazan** — iftar/sahur geri sayımı, şehre göre imsakiye; Ramazan dışında Ramazan'a kalan gün.
- **Cuma ve kandil hatırlatmaları** (dini günler hicri takvimden her yıl otomatik hesaplanır), paylaşılabilir tebrik mesajları.
- **Günün ayeti, hadisi ve duası** (kaynaklarıyla).
- **Tesbih** — hazır ve özel zikirler, hedef, titreşim, 7 günlük geçmiş.
- **Yakındaki camiler** (harita uygulamasında), kıble pusulası, dualar, Esmaül Hüsna, kaza namazı, zekât.
- **Tema** (Sistem / Gece Zümrüdü / Fildişi / Kâbe Örtüsü*), yazı boyutu, **dil** (Türkçe / English / العربية, sağdan sola).
- **Yedekleme** — tüm ayar ve kayıtlar JSON dosyasına dışa aktarılır / geri yüklenir.
- **NurVakti Pro** (Google Play Faturalandırma): Kâbe Örtüsü teması, hıfz modu (ayet tekrar 3/5/10×, metin gizleme), 30 günlük analiz, imsakiye paylaşımı.

## Google Play Console — yapılacaklar

1. **Uygulama içi ürünler** (Para kazanma > Ürünler):
   - Abonelik: `nurvakti_pro_monthly` (aylık), `nurvakti_pro_yearly` (yıllık) — her birine bir temel plan ekleyin.
   - Uygulama içi ürün: `nurvakti_pro_lifetime` (ömür boyu).
2. **İzin beyanları**: Uygulama `SCHEDULE_EXACT_ALARM` (ezan vakitleri), `FOREGROUND_SERVICE_MEDIA_PLAYBACK` (ezan sesini çalma) ve konum kullanır. Ön plan hizmeti formunda "Medya oynatma — vakit girdiğinde kullanıcının seçtiği ezan sesini çalar" yazın.
3. **Veri güvenliği formu**: Veri toplanmıyor/paylaşılmıyor; konum yalnızca cihazda kullanılıyor.
4. **Gizlilik politikası**: `github_files/nurvakti/gizlilik.html` güncellendi; yayındaki sayfaya da kopyalanmalı.

## Derleme

GitHub Actions her push'ta test APK'sı (`NurVakti-test.apk`, ayrı paket adıyla kurulur) ve imzasız AAB üretir; çıktılar Releases'ta. Play'e yüklenecek AAB, yükleme anahtarıyla `jarsigner` ile imzalanır.

```
./gradlew assembleDebug bundleRelease testDebugUnitTest
```

## Kaynaklar ve lisanslar

- Kur'an metni: Kur'an Ansiklopedisi (quranenc.com) — `quran-json` (CC BY 4.0)
- Mealler: Diyanet İşleri Başkanlığı, Saheeh International — tanzil.net
- Sayfa/cüz verisi: `quran-meta` (MIT)
- Yazı tipi: Amiri Quran — SIL Open Font License
- Vakit algoritması: adhan (MIT) temel alınarak Kotlin'e uyarlandı
- İlçe koordinatları: GeoNames (CC BY 4.0)
- Ses: mp3quran.net, everyayah.com
