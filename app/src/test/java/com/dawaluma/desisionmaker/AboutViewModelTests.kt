package com.dawaluma.desisionmaker

import com.dawaluma.desisionmaker.viewmodels.AboutViewModel
import org.junit.Test

class AboutViewModelTests {

    @Test
    fun disableEnableDemoMode() {
        val application = Mocks.TestApplication()
        val dependencyProvider = Mocks.TestDependencyProvider()

        // Use the helper to create the ViewModel
        val viewModel = ViewModelTestHelper.createViewModel(
            application = application,
            viewModelClass = AboutViewModel::class.java,
            dependencyProvider = dependencyProvider
        ) { appSettings ->
            AboutViewModel(application, appSettings)
        }

        // Perform actions and assertions
        viewModel.disableDemoMode()
        assert(!viewModel.appSettings.hasGeminiAPI())
        assert(!viewModel.appSettings.hasYouTubeAPI())
        assert(!viewModel.appSettings.hasValidLLMKey())
        assert(!viewModel.appSettings.hasYouTubeAPI())
        assert(!viewModel.appSettings.hasGeminiAPI())

        viewModel.enableDemoMode()
        assert(viewModel.appSettings.hasGeminiAPI())
        assert(viewModel.appSettings.hasYouTubeAPI())
        assert(viewModel.appSettings.hasValidLLMKey())
        assert(viewModel.appSettings.hasYouTubeAPI())
        assert(viewModel.appSettings.hasGeminiAPI())
    }

    @Test
    fun testMockedSharedPref() {
        val mockedSharedPref = Mocks.MockedSharedPref()

        // Test setting and getting terms agreement
        mockedSharedPref.setAppTermsAgreed(true)
        assert(mockedSharedPref.isAppTermsAgreed())

        // Test Maps API functionality
        mockedSharedPref.setMapsAPI("maps_key")
        assert(mockedSharedPref.hasMapsAPI())
        assert(mockedSharedPref.getMapsAPI() == "maps_key")

        // Test Demo Mode
        mockedSharedPref.setDemoMode(true)
        assert(mockedSharedPref.isDemoMode())
        assert(mockedSharedPref.getMapsAPI() == "demomode")

        // Clear Demo Mode
        mockedSharedPref.setDemoMode(false)
        assert(!mockedSharedPref.isDemoMode())
        assert(!mockedSharedPref.hasMapsAPI())
    }
}