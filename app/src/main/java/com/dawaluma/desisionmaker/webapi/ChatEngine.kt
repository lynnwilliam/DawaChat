package com.dawaluma.desisionmaker.webapi

import android.content.Context
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.webapi.gemini.Content
import com.dawaluma.desisionmaker.webapi.gemini.GeminiAPI
import com.dawaluma.desisionmaker.webapi.gemini.GeminiRequest
import com.dawaluma.desisionmaker.webapi.gemini.Part
import com.dawaluma.desisionmaker.webapi.gpt.GPTRequest
import com.dawaluma.desisionmaker.webapi.gpt.GptAPI
import com.dawaluma.desisionmaker.webapi.gpt.Message
import kotlinx.coroutines.delay

//This figures what Engine we need to use, Gemini or GPT
class ChatEngine(val context: Context,val appsettings: AppSettingsContract) {

    suspend fun sendChatCompletionRequest(query: String): DataType.TextType? {

        if ( appsettings.isDemoMode()) {
            delay(1500)
            return DemoModeData(context).getTextData()
        }

        try {
            if (appsettings.hasGPTAPI()) {
                val chatRequest = GPTRequest(
                    messages = listOf(
                        Message(role = "system", content = ""),
                        Message(role = "user", content = query)
                    )
                )
                return GptAPI().makeChatRequest(chatRequest, appsettings.getGPTAPI())
            }

            if (appsettings.hasGeminiAPI()) {
                val chatRequest = GeminiRequest(
                    contents = listOf(Content(listOf(Part(text = query))))
                )
                return GeminiAPI().makeChatRequest(chatRequest, appsettings.getGeminiAPI())
            }
            return null

        }catch (e: Exception){
            println("ChatEngine: Error ${e.toString()}")
            return null
        }
    }
    
}