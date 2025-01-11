package com.dawaluma.desisionmaker.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.webapi.ChatEngine
import com.dawaluma.desisionmaker.webapi.DemoModeData
import com.dawaluma.desisionmaker.webapi.maps.WebAPIMaps
import com.dawaluma.desisionmaker.webapi.youtube.YouTubeAPI
import kotlinx.coroutines.launch

class IntroViewModel(
    private var application: Application,
    val appSettings: AppSettingsContract) : AndroidViewModel(application){

    var selectedLLM = LLM.GEMINI

    //API Key requirements States
    private val _llmApiKeyValid: MutableState<APIKeyTest> = mutableStateOf(APIKeyTest.KeyRequired)
    val llmApiKeyValid: MutableState<APIKeyTest> = _llmApiKeyValid

    private val _mapsapiKeyValid: MutableState<APIKeyTest> = mutableStateOf(APIKeyTest.KeyNotRequired)
    val mapsapiKeyValid: MutableState<APIKeyTest> = _mapsapiKeyValid

    private val _youttubeapiKeyValid: MutableState<APIKeyTest> = mutableStateOf(APIKeyTest.KeyNotRequired)
    val youttubeapiKeyValid: MutableState<APIKeyTest> = _youttubeapiKeyValid

    //General loading and testing states
    private val _testingAPIKeys: MutableState<APITestState> = mutableStateOf(APITestState.NOT_TESTED)
    val testingAPIKeys: MutableState<APITestState> = _testingAPIKeys

    //the keys we get from the UI
    private val _llmKey = mutableStateOf("")
    fun updateLLMKey(newApiKey: String) {
        _llmKey.value = newApiKey
    }

    private val _youtubeApiKey = mutableStateOf("")
    fun updateYoutubeAIKey(newApiKey: String) {
        _youtubeApiKey.value = newApiKey
    }

    private val _mapsApiKey = mutableStateOf("")
    fun updateMapsAIKey(newApiKey: String) {
        _mapsApiKey.value = newApiKey
    }

    fun testUserApiKeys() {
        _testingAPIKeys.value = APITestState.TESTING
        viewModelScope.launch {
            if ( _llmKey.value.isNotEmpty()){

                appSettings.clearLLMKeys()

                //set the API key for use
                when(selectedLLM){
                    LLM.GEMINI -> {
                        appSettings.setGeminiAPI(_llmKey.value)
                    }
                    LLM.CHATGPT -> {
                        appSettings.setGptAPI(_llmKey.value)
                    }
                }

                val chatResponse = ChatEngine(application, appSettings).sendChatCompletionRequest(
                    query = "hello")

                if (chatResponse != null){
                    _llmApiKeyValid.value = APIKeyTest.KeyPassedTesting
                }else{
                    appSettings.clearLLMKeys()
                    _llmApiKeyValid.value = APIKeyTest.KeyFailedTesting
                }
            }

            if ( _youtubeApiKey.value.isNotEmpty()){
                val response = YouTubeAPI(application).getVideosFromQuery("fast cards",_youtubeApiKey.value )
                if ( response != null){
                    appSettings.setYoutubeAPI(_youtubeApiKey.value)
                    _youttubeapiKeyValid.value = APIKeyTest.KeyPassedTesting
                } else{
                    _youttubeapiKeyValid.value = APIKeyTest.KeyFailedTesting
                }
            }

            if ( _mapsApiKey.value.isNotEmpty()){

                val lat = DemoModeData(application).getPlacesData().first().latitude
                val long = DemoModeData(application).getPlacesData().first().latitude

                val response = WebAPIMaps(application)
                    .getPlacesFromQuery(
                        "mall",
                        _mapsApiKey.value,
                        lat = lat,
                        long = long)

                if (!response.isNullOrEmpty()){
                    appSettings.setMapsAPI(_mapsApiKey.value)
                    _mapsapiKeyValid.value = APIKeyTest.KeyPassedTesting
                } else{
                    _mapsapiKeyValid.value = APIKeyTest.KeyFailedTesting
                }
            }
            _testingAPIKeys.value = APITestState.NOT_TESTED
        }
    }

    fun isConfigured(): Boolean {
        var configRequired =
            !appSettings.isMapsAPIOffered() &&
                    !appSettings.isYouTubeAPIOffered() &&
                    appSettings.hasValidLLMKey()
        return configRequired
    }

    fun demoMode() {
        appSettings.setDemoMode(true)
    }
}

enum class APITestState{
    NOT_TESTED, TESTING, TESTING_PASSED, TESTING_FAILED
}

class IntroViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val appSettings = (application as DawaApplication).appSettings
            return IntroViewModel(application,appSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class LLM{
    GEMINI, CHATGPT
}

sealed class APIKeyTest{
    object KeyRequired: APIKeyTest()
    object KeyNotRequired: APIKeyTest()
    object KeyFailedTesting: APIKeyTest()
    object KeyPassedTesting: APIKeyTest()
}

