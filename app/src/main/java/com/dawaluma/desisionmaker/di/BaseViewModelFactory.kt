package com.dawaluma.desisionmaker.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dawaluma.desisionmaker.database.AppSettingsContract

class BaseViewModelFactory(
    private val application: Application,
    private val creator: (AppSettingsContract) -> ViewModel,
    private val dependencyProviderContract: DependencyProviderContract = AppDependencyProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appSettings = dependencyProviderContract.provideAppSettings(application)
        val viewModel = creator(appSettings)
        if (modelClass.isInstance(viewModel)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}