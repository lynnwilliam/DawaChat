package com.dawaluma.desisionmaker.di

import android.app.Application
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.database.AppSettingsContract

interface DependencyProviderContract {
    fun provideAppSettings(application: Application): AppSettingsContract
}

object AppDependencyProvider: DependencyProviderContract {

    override fun provideAppSettings(application: Application): AppSettingsContract {
        return if (application is DawaApplication) {
            application.appSettings
        } else {
            throw IllegalStateException("Application must be of type DawaApplication")
        }
    }
}