package com.eavisionlabs.nurvakti

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.eavisionlabs.nurvakti.ui.AppScreen
import com.eavisionlabs.nurvakti.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Açılışta çökme olmadığını doğrular (ViewModel ve Activity gerçekten oluşturulur). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class StartupTest {

    @org.junit.Before
    fun grant() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        org.robolectric.Shadows.shadowOf(app).grantPermissions(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE)
    }

    @Test
    fun viewModelStarts() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MainViewModel(app)
        assertNotNull(vm.uiState.value.prayerSchedule)
        vm.setThemeMode("ottoman") // Pro değil: Pro ekranına yönlendirmeli, çökmemeli
        assertEquals(AppScreen.PREMIUM, vm.uiState.value.currentScreen)
        vm.navigateBack()
        vm.navigateTo(AppScreen.TOPICS)
        vm.navigateBack()
        vm.navigateTo(AppScreen.TASBIH)
        repeat(40) { vm.incrementDhikr() }
        vm.navigateBack()
        vm.setKazaPerDay(3)
        vm.openFromIntent("AYAH:13:28")
    }

    @Test
    fun activityLaunches() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
        controller.pause().stop().destroy()
    }
}
