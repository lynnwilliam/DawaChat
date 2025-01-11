package com.dawaluma.desisionmaker

import android.app.Application
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.SharedPrefAppSettings

class DawaApplication: Application() {

    lateinit var appSettings: AppSettingsContract
        private set

    override fun onCreate() {
        super.onCreate()
        appSettings = SharedPrefAppSettings(this)
    }

}