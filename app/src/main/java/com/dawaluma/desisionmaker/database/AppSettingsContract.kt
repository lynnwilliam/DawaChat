package com.dawaluma.desisionmaker.database

interface AppSettingsContract {
    // Terms agreement
    fun isAppTermsAgreed(): Boolean
    fun setAppTermsAgreed(agreed: Boolean)

    // Maps API
    fun setMapsAPI(api: String)
    fun getMapsAPI(): String?
    fun hasMapsAPI(): Boolean

    // YouTube API
    fun setYoutubeAPI(api: String)
    fun getYouTubeAPI(): String?
    fun hasYouTubeAPI(): Boolean

    // GPT API
    fun setGptAPI(api: String)
    fun getGPTAPI(): String
    fun hasGPTAPI(): Boolean

    // Gemini API
    fun setGeminiAPI(api: String)
    fun getGeminiAPI(): String
    fun hasGeminiAPI(): Boolean

    // LLM methods
    fun hasValidLLMKey(): Boolean
    fun getLLMFromDefault(): String?
    fun getLLApiKey(): String?

    // API offerings
    fun isYouTubeAPIOffered(): Boolean
    fun setYouTubeOffered(offered: Boolean)
    fun isMapsAPIOffered(): Boolean
    fun setMapsOffered(offered: Boolean)

    // Demo mode
    fun setDemoMode(demo: Boolean)
    fun isDemoMode(): Boolean

    // Clear keys
    fun clearLLMKeys()
}
