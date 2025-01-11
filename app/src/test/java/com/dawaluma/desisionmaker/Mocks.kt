package com.dawaluma.desisionmaker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.di.AppDependencyProvider
import com.dawaluma.desisionmaker.di.BaseViewModelFactory
import com.dawaluma.desisionmaker.di.DependencyProviderContract

class Mocks {

    class TestApplication : Application(){
        var appSettings: AppSettingsContract

        init{
            appSettings = MockedSharedPref()
        }

        override fun onCreate() {
            super.onCreate()
        }
    }

    class MockedSharedPref : AppSettingsContract {
        private var termsAgreed = false
        private var mapsAPI: String? = null
        private var youtubeAPI: String? = null
        private var gptAPI: String? = null
        private var geminiAPI: String? = null
        private var demoMode = false

        override fun isAppTermsAgreed(): Boolean {
            return termsAgreed
        }

        override fun setAppTermsAgreed(agreed: Boolean) {
            termsAgreed = agreed
        }

        override fun setMapsAPI(api: String) {
            mapsAPI = api
        }

        override fun getMapsAPI(): String? {
            return mapsAPI
        }

        override fun hasMapsAPI(): Boolean {
            return !mapsAPI.isNullOrEmpty()
        }

        override fun setYoutubeAPI(api: String) {
            youtubeAPI = api
        }

        override fun getYouTubeAPI(): String? {
            return youtubeAPI
        }

        override fun hasYouTubeAPI(): Boolean {
            return !youtubeAPI.isNullOrEmpty()
        }

        override fun setGptAPI(api: String) {
            gptAPI = api
        }

        override fun getGPTAPI(): String {
            return gptAPI.orEmpty()
        }

        override fun hasGPTAPI(): Boolean {
            return !gptAPI.isNullOrEmpty()
        }

        override fun setGeminiAPI(api: String) {
            geminiAPI = api
        }

        override fun getGeminiAPI(): String {
            return geminiAPI.orEmpty()
        }

        override fun hasGeminiAPI(): Boolean {
            return !geminiAPI.isNullOrEmpty()
        }

        override fun hasValidLLMKey(): Boolean {
            return hasGPTAPI() || hasGeminiAPI()
        }

        override fun getLLMFromDefault(): String? {
            return when {
                hasGPTAPI() -> "ChatGPT is your LLM"
                hasGeminiAPI() -> "Gemini is your LLM"
                else -> null
            }
        }

        override fun getLLApiKey(): String? {
            return when {
                hasGPTAPI() -> gptAPI
                hasGeminiAPI() -> geminiAPI
                else -> null
            }
        }

        override fun isYouTubeAPIOffered(): Boolean {
            return youtubeAPI.isNullOrEmpty()
        }

        override fun isMapsAPIOffered(): Boolean {
            return mapsAPI.isNullOrEmpty()
        }

        override fun setDemoMode(demo: Boolean) {
            demoMode = demo
            if (demo) {
                mapsAPI = "demomode"
                youtubeAPI = "demomode"
                gptAPI = "demomode"
                geminiAPI = "demomode"
            } else {
                mapsAPI = null
                youtubeAPI = null
                gptAPI = null
                geminiAPI = null
            }
        }

        override fun isDemoMode(): Boolean {
            return demoMode
        }

        override fun clearLLMKeys() {
            gptAPI = null
            geminiAPI = null
        }
    }


    class TestDependencyProvider: DependencyProviderContract{
        override fun provideAppSettings(application: Application): AppSettingsContract {
            return if (application is TestApplication) {
                application.appSettings
            } else {
                throw IllegalStateException("Application must be of type DawaApplication")
            }
        }
    }

    class MockedIntentLauncher: IntentLauncher{

        override fun openMapWithAddress(address: String) {

        }

        override fun requestLocationPermissions() {

        }

        override fun openVideo(video: DataType.Video) {

        }

        override fun openBrowser(address: String) {

        }
    }
}

object ViewModelTestHelper {

    fun <T : ViewModel> createViewModel(
        application: Application,
        viewModelClass: Class<T>,
        dependencyProvider: DependencyProviderContract = AppDependencyProvider,
        creator: (AppSettingsContract) -> T
    ): T {
        // Create the ViewModel factory
        val factory = BaseViewModelFactory(application, creator, dependencyProvider)

        // Create a ViewModelStoreOwner for the test
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }

        // Use ViewModelProvider to create the ViewModel
        return ViewModelProvider(viewModelStoreOwner, factory)[viewModelClass]
    }
}
