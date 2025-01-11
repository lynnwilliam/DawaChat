package com.dawaluma.desisionmaker.webapi.gpt

import com.dawaluma.desisionmaker.database.DataType

class GptAPI() {

    suspend fun makeChatRequest(gptRequest: GPTRequest, apiKey: String): DataType.TextType? {

        val gprResponse = ChatGPTWeb().getChatGPTResponse(gptRequest, apiKey)
        if ( gprResponse.isValid()){
            return DataType.TextType(gprResponse.getGPTAnswer())
        }
        return null
    }
}