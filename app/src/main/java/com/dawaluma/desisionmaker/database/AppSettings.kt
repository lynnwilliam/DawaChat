package com.dawaluma.desisionmaker.database

import android.content.Context
import android.util.Log
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.database.KEYS.KEY_TERMS
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object KEYS{
    const val KEY_TERMS = "app_terms_agreed"
    const val KEY_APP_SETTINGS = "app_settings"
    const val KEY_MAPS_API = "mapsapi"
    const val KEY_YOUTUBE_API = "youtubeapi"
    const val KEY_CHATGPT_API = "gptapitkey"
    const val KEY_GEMINI_API = "geminiapitkey"
}

object APIKeys{
    private var apiSettings: SettingFile? = null
    private var fileAvailable: Boolean = true

    fun loadAPIKeysFile(context: Context, appSettings: AppSettingsContract = SharedPrefAppSettings(context)){

        //DO NOT OVERRIDE VALUES IF WE ARE ALREADY SETUP
        if (fileAvailable && !appSettings.isAppTermsAgreed()){
            try{
                val jsontext = context.resources.openRawResource(R.raw.keys).bufferedReader().use { it.readText() }
                apiSettings = Json.decodeFromString(SettingFile.serializer(), jsontext)
                fileAvailable=false
                apiSettings?.let{
                    appSettings.setMapsAPI(it.mapsAPIKey)
                    appSettings.setYoutubeAPI(it.youtubeApiKey)
                    appSettings.setGptAPI(it.gptAPIKey)
                    appSettings.setGeminiAPI(it.geminiAPIKey)
                }
            }
            catch (e : Exception){
                Log.e("AppSettings","Settings File Could not be read")
                fileAvailable=false
                apiSettings = SettingFile()
            }
        }
        if (apiSettings == null){
            apiSettings = SettingFile()
        }
    }

    fun getAPIKeys(): SettingFile {
        return apiSettings!!
    }
}

public class SharedPrefAppSettings(val context: Context): AppSettingsContract {

    override fun isAppTermsAgreed() = getSharedPreferences().getBoolean(KEYS.KEY_TERMS, false)
    override fun setAppTermsAgreed(agreed: Boolean) {
        getSharedPreferences().edit().putBoolean(KEY_TERMS, agreed).apply()
    }

    private fun getSharedPreferences() = context.getSharedPreferences(KEYS.KEY_APP_SETTINGS, Context.MODE_PRIVATE)

    //maps API
    override fun setMapsAPI(api: String){
        getSharedPreferences().edit().putString(KEYS.KEY_MAPS_API,api).apply()
    }

    override fun getMapsAPI(): String?{
        return getSharedPreferences().getString(KEYS.KEY_MAPS_API,"")
    }

    override fun hasMapsAPI(): Boolean{
        return !getMapsAPI().isNullOrEmpty()
    }

    //YouTubeAPI
    override fun getYouTubeAPI(): String?{
        return getSharedPreferences().getString(KEYS.KEY_YOUTUBE_API,"")
    }

    override fun hasYouTubeAPI(): Boolean{
        return !getYouTubeAPI().isNullOrEmpty()
    }

    override fun setYoutubeAPI(api: String){
        getSharedPreferences().edit().putString(KEYS.KEY_YOUTUBE_API,api).apply()
    }

    //GPT API
    override fun getGPTAPI(): String{
        return getSharedPreferences().getString(KEYS.KEY_CHATGPT_API, "") ?: ""
    }

    override fun setGptAPI(api: String){
        getSharedPreferences().edit().putString(KEYS.KEY_CHATGPT_API,api).apply()
    }

    override fun hasGPTAPI(): Boolean{
        return getGPTAPI().isNotEmpty()
    }

    //Gemini
    override fun getGeminiAPI(): String{
        return getSharedPreferences().getString(KEYS.KEY_GEMINI_API, "") ?: ""
    }

    override fun setGeminiAPI(api: String){
        getSharedPreferences().edit().putString(KEYS.KEY_GEMINI_API,api).apply()
    }

    override fun hasGeminiAPI(): Boolean{
        return getGeminiAPI().isNotEmpty()
    }

    override fun hasValidLLMKey(): Boolean{
        return ( hasGPTAPI() || hasGeminiAPI())
    }

    //helper methods
    override fun getLLMFromDefault(): String?{
        if ( hasGPTAPI()) return "ChatGPT is your LLM"
        if ( hasGeminiAPI()) return "Gemini is your LLM"
        return null
    }

    override fun getLLApiKey(): String?{
        if ( hasGPTAPI()) return getGPTAPI()
        if ( hasGeminiAPI()) return getGeminiAPI()
        return null
    }

    //return true if you want the user the option of adding the YouTubeAPI
    override fun isYouTubeAPIOffered(): Boolean{
        if ( hasYouTubeAPI()){
            return false
        }
        return APIKeys.getAPIKeys().requestYouTubeAPIKey
    }

    override fun setYouTubeOffered(offered: Boolean) {
        APIKeys.getAPIKeys().requestYouTubeAPIKey = false
    }

    override fun isMapsAPIOffered(): Boolean{
        if ( hasMapsAPI()){
            return false
        }
        return APIKeys.getAPIKeys().requestMapsAPIKey
    }

    override fun setMapsOffered(offered: Boolean) {
        APIKeys.getAPIKeys().requestMapsAPIKey = false
    }

    override fun setDemoMode(demo: Boolean) {

        if ( demo){
            setYoutubeAPI("demomode")
            setMapsAPI("demomode")
            setGptAPI("demomode")
            setGeminiAPI("demomode")
        } else {
            setAppTermsAgreed(false)
            setGptAPI("")
            setGeminiAPI("")
            setMapsAPI("")
            setYoutubeAPI("")
        }

        getSharedPreferences().edit().putBoolean("demomode",demo).apply()
    }

    override fun isDemoMode(): Boolean{
        return getSharedPreferences().getBoolean("demomode", false)
    }

    override fun clearLLMKeys() {
        setGptAPI("")
        setGeminiAPI("")
    }
}

@Serializable
data class SettingFile(
    val gptAPIKey: String="",
    val youtubeApiKey: String = "",
    val geminiAPIKey: String = "",
    val mapsAPIKey: String = "",
    var requestMapsAPIKey: Boolean = true,
    var requestYouTubeAPIKey: Boolean = true
)