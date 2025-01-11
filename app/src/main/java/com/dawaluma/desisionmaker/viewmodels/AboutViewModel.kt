package com.dawaluma.desisionmaker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.database.AppSettingsContract

class AboutViewModel(private val application: Application, val appSettings: AppSettingsContract) : AndroidViewModel(application){

    fun disableDemoMode() {
        appSettings.setDemoMode(false)
    }

    fun enableDemoMode() {
        appSettings.setDemoMode(true)
    }
}

class AboutViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AboutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val appSettings = (application as DawaApplication).appSettings
            return AboutViewModel(application,appSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}