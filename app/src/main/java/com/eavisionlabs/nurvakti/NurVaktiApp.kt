package com.eavisionlabs.nurvakti

import android.app.Application
import com.eavisionlabs.nurvakti.data.CityProvider
import com.eavisionlabs.nurvakti.data.QuranRepository
import com.eavisionlabs.nurvakti.data.UserPreferences
import com.eavisionlabs.nurvakti.i18n.I18n
import com.eavisionlabs.nurvakti.notify.NotificationHelper
import com.eavisionlabs.nurvakti.notify.PrayerAlarmScheduler

class NurVaktiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CityProvider.init(this)
        QuranRepository.init(this)
        I18n.language = UserPreferences(this).language
        PrayerAlarmScheduler.applyEngineSettings(this)
        NotificationHelper.createChannels(this)
        com.eavisionlabs.nurvakti.billing.BillingManager.configure(this)
    }
}
