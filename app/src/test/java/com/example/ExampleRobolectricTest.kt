package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PrayerCalculationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NurVakti", appName)
  }

  @Test
  fun `test qibla bearing calculation for Istanbul`() {
    // Istanbul (41.0082° N, 28.9784° E) towards Makkah should be approx 150-155 degrees
    val bearing = PrayerCalculationEngine.calculateQiblaBearing(41.0082, 28.9784)
    assertTrue("Bearing should be between 145 and 160, was $bearing", bearing in 145f..160f)
  }

  @Test
  fun `test hijri date conversion`() {
    val cal = Calendar.getInstance()
    val hijri = PrayerCalculationEngine.getHijriDate(cal)
    assertTrue("Hijri date string should not be blank", hijri.isNotBlank())
  }
}
