package com.example.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CompassData(
    val hasCompassSensor: Boolean = true,
    val currentAzimuthDegrees: Float = 0f,
    val qiblaBearingDegrees: Float = 152f, // Default for Turkey towards Makkah (~150-155°)
    val degreesToQibla: Float = 0f,
    val isAlignedWithQibla: Boolean = false,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class CompassSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassData = MutableStateFlow(CompassData())
    val compassData: StateFlow<CompassData> = _compassData.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var currentLatitude: Double = 41.0082
    private var currentLongitude: Double = 28.9784

    init {
        val hasSensor = rotationVectorSensor != null || (accelerometer != null && magnetometer != null)
        _compassData.value = _compassData.value.copy(hasCompassSensor = hasSensor)
    }

    fun setLocation(lat: Double, lng: Double) {
        currentLatitude = lat
        currentLongitude = lng
        val bearing = PrayerCalculationEngine.calculateQiblaBearing(lat, lng)
        _compassData.value = _compassData.value.copy(qiblaBearingDegrees = bearing)
        updateQiblaAlignment(_compassData.value.currentAzimuthDegrees, bearing)
    }

    fun startListening() {
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuth = (azimuth + 360f) % 360f
            updateAzimuth(azimuth, event.accuracy)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, 3)
            hasGravity = true
            processOrientation(event.accuracy)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
            hasGeomagnetic = true
            processOrientation(event.accuracy)
        }
    }

    private fun processOrientation(accuracy: Int) {
        if (hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360f) % 360f
                updateAzimuth(azimuth, accuracy)
            }
        }
    }

    private fun updateAzimuth(azimuth: Float, accuracy: Int) {
        val bearing = _compassData.value.qiblaBearingDegrees
        updateQiblaAlignment(azimuth, bearing, accuracy)
    }

    private fun updateQiblaAlignment(azimuth: Float, bearing: Float, accuracy: Int? = null) {
        var diff = bearing - azimuth
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f

        val isAligned = kotlin.math.abs(diff) < 3.5f

        _compassData.value = _compassData.value.copy(
            currentAzimuthDegrees = azimuth,
            qiblaBearingDegrees = bearing,
            degreesToQibla = diff,
            isAlignedWithQibla = isAligned,
            accuracy = accuracy ?: _compassData.value.accuracy
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _compassData.value = _compassData.value.copy(accuracy = accuracy)
    }
}
