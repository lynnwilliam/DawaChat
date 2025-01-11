package com.dawaluma.desisionmaker

import com.dawaluma.desisionmaker.viewmodels.IntroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class IntroViewModelTests {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the Main dispatcher to a test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original state
        Dispatchers.resetMain()
    }


    @Test
    fun testUserApiKeys() = runTest {
        val application = Mocks.TestApplication()
        val dependencyProvider = Mocks.TestDependencyProvider()

        // Use the helper to create the ViewModel
        val viewModel = ViewModelTestHelper.createViewModel(
            application = application,
            viewModelClass = IntroViewModel::class.java,
            dependencyProvider = dependencyProvider
        ) { appSettings ->
            IntroViewModel(application, appSettings)
        }

        runBlocking {
            viewModel.testUserApiKeys()
        }

    }

}