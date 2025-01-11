package com.dawaluma.desisionmaker.webapi.gpt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class ChatGPTWeb() {

    interface OpenAIService {
        @POST("chat/completions")
        suspend fun chatCompletion(
            @Header("Authorization") authorization: String,  // Authorization header
            @Body chatRequest: GPTRequest // Body of the request (ChatRequest)
        ): ChatCompletionResponse
    }

    object RetrofitInstance {
        private const val BASE_URL = "https://api.openai.com/v1/"

        val api: OpenAIService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService::class.java)
        }
    }

    suspend fun getChatGPTResponse(chatRequest: GPTRequest, chatGptAPIKey: String): GPTResponse {
        try {
            val response = RetrofitInstance.api.chatCompletion(
                authorization = "Bearer $chatGptAPIKey", // Set the Authorization header
                chatRequest = chatRequest // Set the request body
            )

            // Handle the response
            println("Response: ${response.toString()}")
            return GPTResponse(response=response)
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
            return GPTResponse(error = e.toString())
        }
    }
}
