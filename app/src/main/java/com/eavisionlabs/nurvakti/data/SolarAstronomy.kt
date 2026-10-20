package com.eavisionlabs.nurvakti.data

import kotlin.math.*

/**
 * Jean Meeus "Astronomical Algorithms" tabanlı güneş konumu hesapları.
 * adhan (MIT lisansı, Batoul Apps) kütüphanesindeki algoritmanın Kotlin karşılığıdır.
 */
internal object Astro {

    fun toRad(d: Double) = d * PI / 180.0
    fun toDeg(r: Double) = r * 180.0 / PI

    fun normalize(num: Double, max: Double): Double = num - max * floor(num / max)
    fun unwind(angle: Double): Double = normalize(angle, 360.0)
    fun quadrantShift(angle: Double): Double =
        if (angle >= -180 && angle <= 180) angle else angle - 360 * Math.round(angle / 360).toDouble()

    fun meanSolarLongitude(t: Double) = unwind(280.4664567 + 36000.76983 * t + 0.0003032 * t * t)
    fun meanLunarLongitude(t: Double) = unwind(218.3165 + 481267.8813 * t)
    fun ascendingLunarNode(t: Double) = unwind(125.04452 - 1934.136261 * t + 0.0020708 * t * t + t * t * t / 450000)
    fun meanSolarAnomaly(t: Double) = unwind(357.52911 + 35999.05029 * t - 0.0001537 * t * t)

    fun equationOfCenter(t: Double, m: Double): Double {
        val mr = toRad(m)
        return (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mr) +
            (0.019993 - 0.000101 * t) * sin(2 * mr) +
            0.000289 * sin(3 * mr)
    }

    fun apparentSolarLongitude(t: Double, l0: Double): Double {
        val lon = l0 + equationOfCenter(t, meanSolarAnomaly(t))
        val omega = 125.04 - 1934.136 * t
        return unwind(lon - 0.00569 - 0.00478 * sin(toRad(omega)))
    }

    fun meanObliquity(t: Double) = 23.439291 - 0.013004167 * t - 0.0000001639 * t * t + 0.0000005036 * t * t * t
    fun apparentObliquity(t: Double, e0: Double) = e0 + 0.00256 * cos(toRad(125.04 - 1934.136 * t))

    fun meanSiderealTime(t: Double): Double {
        val jd = t * 36525 + 2451545.0
        return unwind(280.46061837 + 360.98564736629 * (jd - 2451545) + 0.000387933 * t * t - t * t * t / 38710000)
    }

    fun nutationLongitude(l0: Double, lp: Double, om: Double) =
        -17.2 / 3600 * sin(toRad(om)) - 1.32 / 3600 * sin(2 * toRad(l0)) -
            0.23 / 3600 * sin(2 * toRad(lp)) + 0.21 / 3600 * sin(2 * toRad(om))

    fun nutationObliquity(l0: Double, lp: Double, om: Double) =
        9.2 / 3600 * cos(toRad(om)) + 0.57 / 3600 * cos(2 * toRad(l0)) +
            0.1 / 3600 * cos(2 * toRad(lp)) - 0.09 / 3600 * cos(2 * toRad(om))

    fun altitude(lat: Double, dec: Double, h: Double): Double =
        toDeg(asin(sin(toRad(lat)) * sin(toRad(dec)) + cos(toRad(lat)) * cos(toRad(dec)) * cos(toRad(h))))

    fun interpolate(y2: Double, y1: Double, y3: Double, n: Double): Double {
        val a = y2 - y1
        val b = y3 - y2
        return y2 + n / 2 * (a + b + n * (b - a))
    }

    fun interpolateAngles(y2: Double, y1: Double, y3: Double, n: Double): Double {
        val a = unwind(y2 - y1)
        val b = unwind(y3 - y2)
        return y2 + n / 2 * (a + b + n * (b - a))
    }

    fun julianDay(year: Int, month: Int, day: Int): Double {
        val y = if (month > 2) year else year - 1
        val m = if (month > 2) month else month + 12
        val a = y / 100
        val b = 2 - a + a / 4
        val i0 = (365.25 * (y + 4716)).toInt()
        val i1 = (30.6001 * (m + 1)).toInt()
        return i0 + i1 + day + b - 1524.5
    }

    fun julianCentury(jd: Double) = (jd - 2451545.0) / 36525
}

internal class SolarCoordinates(jd: Double) {
    val declination: Double
    val rightAscension: Double
    val apparentSiderealTime: Double

    init {
        val t = Astro.julianCentury(jd)
        val l0 = Astro.meanSolarLongitude(t)
        val lp = Astro.meanLunarLongitude(t)
        val om = Astro.ascendingLunarNode(t)
        val lambda = Astro.toRad(Astro.apparentSolarLongitude(t, l0))
        val theta0 = Astro.meanSiderealTime(t)
        val dPsi = Astro.nutationLongitude(l0, lp, om)
        val dEps = Astro.nutationObliquity(l0, lp, om)
        val e0 = Astro.meanObliquity(t)
        val eApp = Astro.toRad(Astro.apparentObliquity(t, e0))
        declination = Astro.toDeg(asin(sin(eApp) * sin(lambda)))
        rightAscension = Astro.unwind(Astro.toDeg(atan2(cos(eApp) * sin(lambda), cos(lambda))))
        apparentSiderealTime = theta0 + dPsi * 3600 * cos(Astro.toRad(e0 + dEps)) / 3600
    }
}

/** Belirli bir gün ve konum için güneş olaylarının UTC saatleri (0..24, gün dışına taşabilir). */
internal class SolarTime(year: Int, month: Int, day: Int, private val lat: Double, private val lng: Double) {
    private val solar: SolarCoordinates
    private val prev: SolarCoordinates
    private val next: SolarCoordinates
    private val approxTransit: Double
    val transit: Double
    val sunrise: Double
    val sunset: Double

    init {
        val jd = Astro.julianDay(year, month, day)
        solar = SolarCoordinates(jd)
        prev = SolarCoordinates(jd - 1)
        next = SolarCoordinates(jd + 1)
        approxTransit = approximateTransit()
        transit = correctedTransit()
        sunrise = hourAngle(-50.0 / 60.0, false)
        sunset = hourAngle(-50.0 / 60.0, true)
    }

    private fun approximateTransit(): Double {
        val lw = -lng
        val m0 = Astro.normalize((solar.rightAscension + lw - solar.apparentSiderealTime) / 360, 1.0)
        val expected = Astro.normalize((12.0 - lng / 15.0) / 24.0, 1.0)
        return when {
            m0 - expected > 0.5 -> m0 - 1.0
            expected - m0 > 0.5 -> m0 + 1.0
            else -> m0
        }
    }

    private fun correctedTransit(): Double {
        val m0 = approxTransit
        val lw = -lng
        val theta = Astro.unwind(solar.apparentSiderealTime + 360.985647 * m0)
        val a = Astro.unwind(Astro.interpolateAngles(solar.rightAscension, prev.rightAscension, next.rightAscension, m0))
        val h = Astro.quadrantShift(theta - lw - a)
        return (m0 + h / -360) * 24
    }

    /** Güneşin [angle] derece yükseklikte olduğu an. NaN dönebilir (kutup bölgeleri). */
    fun hourAngle(angle: Double, afterTransit: Boolean): Double {
        val m0 = approxTransit
        val lw = -lng
        val term1 = sin(Astro.toRad(angle)) - sin(Astro.toRad(lat)) * sin(Astro.toRad(solar.declination))
        val term2 = cos(Astro.toRad(lat)) * cos(Astro.toRad(solar.declination))
        val h0 = Astro.toDeg(acos(term1 / term2))
        val m = if (afterTransit) m0 + h0 / 360 else m0 - h0 / 360
        val theta = Astro.unwind(solar.apparentSiderealTime + 360.985647 * m)
        val a = Astro.unwind(Astro.interpolateAngles(solar.rightAscension, prev.rightAscension, next.rightAscension, m))
        val delta = Astro.interpolate(solar.declination, prev.declination, next.declination, m)
        val hh = theta - lw - a
        val alt = Astro.altitude(lat, delta, hh)
        val dm = (alt - angle) / (360 * cos(Astro.toRad(delta)) * cos(Astro.toRad(lat)) * sin(Astro.toRad(hh)))
        return (m + dm) * 24
    }

    /** İkindi (asr-ı evvel için gölge katsayısı 1). */
    fun afternoon(shadowLength: Double): Double {
        val tangent = abs(lat - solar.declination)
        val inverse = shadowLength + tan(Astro.toRad(tangent))
        val angle = Astro.toDeg(atan(1.0 / inverse))
        return hourAngle(angle, true)
    }
}
