package com.eavisionlabs.nurvakti.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.eavisionlabs.nurvakti.R
import com.eavisionlabs.nurvakti.i18n.t
import com.eavisionlabs.nurvakti.model.MonthlyDaySchedule
import kotlin.math.cos
import kotlin.math.sin

/** Paylaşılabilir görsel kartlar (tebrik, ayet/hadis, imsakiye). Tamamen cihazda çizilir. */
object ShareCardRenderer {

    data class CardStyle(
        val id: String,
        val name: String,
        val top: Int,
        val bottom: Int,
        val accent: Int,
        val text: Int,
        val sub: Int
    )

    val styles = listOf(
        CardStyle("emerald", "Zümrüt", 0xFF14463A.toInt(), 0xFF07170F.toInt(), 0xFFD8B875.toInt(), 0xFFF7F4EC.toInt(), 0xFFB9C7C0.toInt()),
        CardStyle("night", "Gece Mavisi", 0xFF1B2A55.toInt(), 0xFF070B1C.toInt(), 0xFFE6C878.toInt(), 0xFFF3F1EA.toInt(), 0xFFB5BCD3.toInt()),
        CardStyle("ottoman", "Osmanlı Bordosu", 0xFF5E1622.toInt(), 0xFF22070C.toInt(), 0xFFE2BE6E.toInt(), 0xFFFBF3E6.toInt(), 0xFFD9BFB8.toInt()),
        CardStyle("kiswa", "Kâbe Siyahı", 0xFF1E1A12.toInt(), 0xFF050505.toInt(), 0xFFD4AF37.toInt(), 0xFFF5EFE0.toInt(), 0xFFB3AA98.toInt()),
        CardStyle("ivory", "Fildişi", 0xFFFBF6EA.toInt(), 0xFFEDE3CC.toInt(), 0xFF8A5F12.toInt(), 0xFF13362C.toInt(), 0xFF56655E.toInt())
    )

    private const val W = 1080
    private const val H = 1350

    /** Tebrik / ayet / hadis kartı (1080x1350, Instagram ve WhatsApp durum için uygun) */
    fun renderCard(
        context: Context,
        style: CardStyle,
        title: String,
        body: String,
        arabic: String? = null,
        reference: String? = null
    ): Bitmap {
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        drawBackground(c, style, W, H)

        // Hilal ve başlık
        drawCrescent(c, W / 2f, 175f, 46f, style.accent)
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.accent; textSize = 66f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val titleLayout = layout(title, titlePaint, W - 220, Layout.Alignment.ALIGN_CENTER, 1.0f)
        c.save(); c.translate(110f, 255f); titleLayout.draw(c); c.restore()
        var y = 255f + titleLayout.height + 26f
        drawOrnament(c, W / 2f, y, style.accent)
        y += 40f

        // Alt bilgi alanı
        val footerTop = H - 175f
        val areaBottom = footerTop - 30f

        // Arapça metin
        if (!arabic.isNullOrBlank()) {
            val arPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.text
                typeface = runCatching { ResourcesCompat.getFont(context, R.font.amiri_quran) }.getOrNull() ?: Typeface.DEFAULT
            }
            var size = 62f
            var arLayout: StaticLayout
            do {
                arPaint.textSize = size
                arLayout = layout(arabic, arPaint, W - 240, Layout.Alignment.ALIGN_CENTER, 1.25f)
                size -= 4f
            } while (arLayout.height > 330 && size > 30f)
            c.save(); c.translate(120f, y); arLayout.draw(c); c.restore()
            y += arLayout.height + 30f
        }

        // Gövde metni: alana sığana kadar küçült
        val refPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.accent; textSize = 36f; typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }
        val refHeight = if (reference.isNullOrBlank()) 0f else 70f
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.text; typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }
        val avail = areaBottom - y - refHeight
        var size = 58f
        var bodyLayout: StaticLayout
        do {
            bodyPaint.textSize = size
            bodyLayout = layout(body, bodyPaint, W - 240, Layout.Alignment.ALIGN_CENTER, 1.18f)
            size -= 2f
        } while (bodyLayout.height > avail && size > 26f)
        val blockH = bodyLayout.height + refHeight
        val startY = y + ((avail + refHeight - blockH) / 2f).coerceAtLeast(0f)
        c.save(); c.translate(120f, startY); bodyLayout.draw(c); c.restore()
        if (!reference.isNullOrBlank()) {
            val refLayout = layout("— $reference", refPaint, W - 240, Layout.Alignment.ALIGN_CENTER, 1f)
            c.save(); c.translate(120f, startY + bodyLayout.height + 24f); refLayout.draw(c); c.restore()
        }

        drawFooter(context, c, style, W, footerTop + 40f)
        return bmp
    }

    /** Ramazan imsakiyesi tablosu */
    fun renderImsakiye(
        context: Context,
        style: CardStyle,
        cityName: String,
        subtitle: String,
        days: List<MonthlyDaySchedule>
    ): Bitmap {
        val rowH = 54
        val headerH = 360
        val tableTop = headerH + 70
        val h = tableTop + rowH * days.size + 230
        val bmp = Bitmap.createBitmap(W, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        drawBackground(c, style, W, h)

        drawCrescent(c, W / 2f, 150f, 40f, style.accent)
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.accent; textSize = 58f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val title = layout(String.format(t("%s Ramazan İmsakiyesi"), cityName), titlePaint, W - 200, Layout.Alignment.ALIGN_CENTER, 1f)
        c.save(); c.translate(100f, 220f); title.draw(c); c.restore()
        val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = style.sub; textSize = 32f; textAlign = Paint.Align.CENTER }
        c.drawText(subtitle, W / 2f, 230f + title.height + 46f, subPaint)

        val cols = listOf(t("Gün"), t("Tarih"), t("İmsak"), t("Güneş"), t("İftar"), t("Yatsı"))
        val weights = floatArrayOf(0.7f, 1.5f, 1f, 1f, 1f, 1f)
        val left = 90f
        val width = W - 180f
        val unit = width / weights.sum()
        val centers = FloatArray(cols.size)
        var x = left
        for (i in cols.indices) { centers[i] = x + weights[i] * unit / 2f; x += weights[i] * unit }

        val head = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.accent; textSize = 30f; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD
        }
        cols.forEachIndexed { i, s -> c.drawText(s, centers[i], tableTop - 20f, head) }
        val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = style.accent; alpha = 140; strokeWidth = 2f }
        c.drawLine(left, tableTop - 2f, left + width, tableTop - 2f, line)

        val zebra = Paint().apply { color = style.text; alpha = 14 }
        val cell = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = style.text; textSize = 29f; textAlign = Paint.Align.CENTER }
        val cellAccent = Paint(cell).apply { color = style.accent; typeface = Typeface.DEFAULT_BOLD }
        val cellSub = Paint(cell).apply { color = style.sub }
        days.forEachIndexed { r, d ->
            val top = tableTop + r * rowH
            if (r % 2 == 0) c.drawRect(left, top.toFloat(), left + width, (top + rowH).toFloat(), zebra)
            val base = top + rowH / 2f + 10f
            c.drawText("${d.dayNumber}", centers[0], base, cellSub)
            c.drawText(shortDate(d.gregorianDate), centers[1], base, cell)
            c.drawText(d.imsak, centers[2], base, cellAccent)
            c.drawText(d.gunes, centers[3], base, cellSub)
            c.drawText(d.aksam, centers[4], base, cellAccent)
            c.drawText(d.yatsi, centers[5], base, cellSub)
        }
        c.drawLine(left, (tableTop + rowH * days.size).toFloat(), left + width, (tableTop + rowH * days.size).toFloat(), line)
        drawFooter(context, c, style, W, h - 100f)
        return bmp
    }

    /** "18.02.2027" -> "18.02 Prş" */
    private fun shortDate(s: String): String {
        val p = s.trim().split('.')
        if (p.size < 3) return s.take(10)
        val cal = java.util.Calendar.getInstance().apply {
            set(p[2].take(4).toIntOrNull() ?: 2000, (p[1].toIntOrNull() ?: 1) - 1, p[0].toIntOrNull() ?: 1)
        }
        val names = listOf("Paz", "Pzt", "Sal", "Çar", "Prş", "Cum", "Cmt")
        return "${p[0]}.${p[1]} ${t(names[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1])}"
    }

    private fun layout(text: String, paint: TextPaint, width: Int, align: Layout.Alignment, spacing: Float): StaticLayout =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(align)
            .setLineSpacing(0f, spacing)
            .setIncludePad(false)
            .build()

    private fun drawBackground(c: Canvas, s: CardStyle, w: Int, h: Int) {
        val bg = Paint().apply { shader = LinearGradient(0f, 0f, 0f, h.toFloat(), s.top, s.bottom, Shader.TileMode.CLAMP) }
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bg)
        // Sekiz köşeli yıldız deseni
        val star = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 1.6f; color = s.accent; alpha = 26 }
        val step = 120f
        var yy = 0f
        var row = 0
        while (yy < h + step) {
            var xx = if (row % 2 == 0) 0f else step / 2f
            while (xx < w + step) { drawStar(c, xx, yy, 34f, star); xx += step }
            yy += step * 0.866f; row++
        }
        // Çift çerçeve
        val frame = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; color = s.accent; strokeWidth = 3f; alpha = 190 }
        c.drawRoundRect(RectF(44f, 44f, w - 44f, h - 44f), 36f, 36f, frame)
        frame.strokeWidth = 1.5f; frame.alpha = 110
        c.drawRoundRect(RectF(60f, 60f, w - 60f, h - 60f), 28f, 28f, frame)
    }

    private fun drawStar(c: Canvas, cx: Float, cy: Float, r: Float, p: Paint) {
        val path = Path()
        for (sq in 0..1) {
            val off = if (sq == 0) 0.0 else Math.PI / 4
            for (k in 0..3) {
                val a = off + k * Math.PI / 2
                val px = cx + (r * cos(a)).toFloat()
                val py = cy + (r * sin(a)).toFloat()
                if (k == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
        }
        c.drawPath(path, p)
    }

    private fun drawCrescent(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        val outer = Path().apply { addCircle(cx, cy, r, Path.Direction.CW) }
        val inner = Path().apply { addCircle(cx + r * 0.42f, cy - r * 0.18f, r * 0.84f, Path.Direction.CW) }
        outer.op(inner, Path.Op.DIFFERENCE)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        c.drawPath(outer, p)
        // küçük yıldız
        val star = Path()
        val sx = cx + r * 0.62f; val sy = cy - r * 0.05f; val sr = r * 0.26f
        for (k in 0 until 10) {
            val rr = if (k % 2 == 0) sr else sr * 0.42f
            val a = -Math.PI / 2 + k * Math.PI / 5
            val px = sx + (rr * cos(a)).toFloat(); val py = sy + (rr * sin(a)).toFloat()
            if (k == 0) star.moveTo(px, py) else star.lineTo(px, py)
        }
        star.close()
        c.drawPath(star, p)
    }

    private fun drawOrnament(c: Canvas, cx: Float, y: Float, color: Int) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = 2.5f; alpha = 200 }
        c.drawLine(cx - 170f, y, cx - 22f, y, p)
        c.drawLine(cx + 22f, y, cx + 170f, y, p)
        val d = Path().apply { moveTo(cx, y - 12f); lineTo(cx + 12f, y); lineTo(cx, y + 12f); lineTo(cx - 12f, y); close() }
        c.drawPath(d, p)
    }

    private fun drawFooter(context: Context, c: Canvas, s: CardStyle, w: Int, centerY: Float) {
        val logo = runCatching { BitmapFactory.decodeResource(context.resources, R.drawable.nurvakti_logo) }.getOrNull()
        val name = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = s.accent; textSize = 38f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val tag = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = s.sub; textSize = 24f }
        val label = "NurVakti"
        val tagline = t("Namaz Vakitleri & Kur'an-ı Kerim")
        val textW = maxOf(name.measureText(label), tag.measureText(tagline))
        val logoSize = 72f
        val total = (if (logo != null) logoSize + 18f else 0f) + textW
        var x = (w - total) / 2f
        if (logo != null) {
            c.drawBitmap(logo, null, RectF(x, centerY - logoSize / 2f, x + logoSize, centerY + logoSize / 2f), Paint(Paint.FILTER_BITMAP_FLAG))
            x += logoSize + 18f
        }
        c.drawText(label, x, centerY - 2f, name)
        c.drawText(tagline, x, centerY + 30f, tag)
    }
}
