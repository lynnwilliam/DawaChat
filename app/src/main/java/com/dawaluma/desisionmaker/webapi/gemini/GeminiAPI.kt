package com.dawaluma.desisionmaker.webapi.gemini

import com.dawaluma.desisionmaker.database.DataType

class GeminiAPI() {

    suspend fun makeChatRequest(geminiRequest: GeminiRequest,apiKey: String): DataType.TextType? {
        val gprResponse = ChatGeminiWeb().getGeminiResponse(geminiRequest, apiKey)
        if ( gprResponse.candidates.isNotEmpty()){
            return DataType.TextType(gprResponse.candidates.first().content.parts.first().text)
        }
        return null
    }
}