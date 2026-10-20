package com.eavisionlabs.nurvakti.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.eavisionlabs.nurvakti.model.City
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import kotlin.math.*

class AutoLocationManager(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun isLocationPermissionGranted(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation(onLocationResult: (City?) -> Unit) {
        if (!isLocationPermissionGranted()) {
            onLocationResult(null)
            return
        }

        // 1. Try Google Play Services FusedLocationProviderClient first (fastest and most accurate)
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        val city = resolveCityFromCoordinates(loc.latitude, loc.longitude)
                        onLocationResult(city)
                    } else {
                        // If null, try fused lastLocation
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                            if (lastLoc != null) {
                                val city = resolveCityFromCoordinates(lastLoc.latitude, lastLoc.longitude)
                                onLocationResult(city)
                            } else {
                                fallbackToLocationManager(onLocationResult)
                            }
                        }.addOnFailureListener {
                            fallbackToLocationManager(onLocationResult)
                        }
                    }
                }
                .addOnFailureListener {
                    fallbackToLocationManager(onLocationResult)
                }
        } catch (_: Exception) {
            fallbackToLocationManager(onLocationResult)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fallbackToLocationManager(onLocationResult: (City?) -> Unit) {
        if (locationManager == null) {
            onLocationResult(null)
            return
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        if (bestLocation != null) {
            val city = resolveCityFromCoordinates(bestLocation.latitude, bestLocation.longitude)
            onLocationResult(city)
            return
        }

        // Request single fresh location fix from standard LocationManager
        try {
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> null
            }

            if (provider != null) {
                locationManager.requestSingleUpdate(
                    provider,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            val city = resolveCityFromCoordinates(location.latitude, location.longitude)
                            onLocationResult(city)
                        }
                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    },
                    Looper.getMainLooper()
                )
            } else {
                onLocationResult(null)
            }
        } catch (_: Exception) {
            onLocationResult(null)
        }
    }

    fun resolveCityFromCoordinates(lat: Double, lng: Double): City {
        // En yakın kayıtlı il/ilçe (ilçe listesi dahil)
        val nearest = CityProvider.findClosestCity(lat, lng)
        val nearestKm = haversineDistanceKm(lat, lng, nearest.latitude, nearest.longitude)

        var name = nearest.name
        var province = nearest.province
        var country = nearest.country
        try {
            val geocoder = Geocoder(context, Locale("tr", "TR"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val district = addr.subAdminArea ?: addr.locality
                val admin = addr.adminArea
                if (!district.isNullOrBlank() && !admin.isNullOrBlank() && district != admin) {
                    name = district
                    province = admin
                } else if (!admin.isNullOrBlank()) {
                    name = admin
                    province = ""
                }
                if (!addr.countryName.isNullOrBlank()) country = addr.countryName
            }
        } catch (_: Exception) {
            // Geocoder yoksa en yakın kayıtlı yerin adı kullanılır
            if (nearestKm > 60) { name = "Konumum"; province = "" }
        }

        return City(
            id = AUTO_CITY_ID,
            name = name,
            country = country,
            latitude = lat,
            longitude = lng,
            isFavorite = true,
            // Kullanıcı fiilen orada olduğu için cihaz saat dilimi doğrudur
            timeZoneId = java.util.TimeZone.getDefault().id,
            province = province
        )
    }

    companion object {
        const val AUTO_CITY_ID = "auto_gps"
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
