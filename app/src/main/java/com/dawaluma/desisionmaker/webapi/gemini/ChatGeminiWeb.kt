package com.dawaluma.desisionmaker.webapi.gemini

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

class ChatGeminiWeb() {

    interface GeminiApiService {
        @POST("v1beta/models/gemini-1.5-flash:generateContent")
        suspend fun chatCompletion(
            @Query("key") key: String,
            @Body chatRequest: GeminiRequest
        ): GeminiResponse
    }

    object RetrofitInstance {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        val api: GeminiApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeminiApiService::class.java)
        }
    }

    suspend fun getGeminiResponse(chatRequest: GeminiRequest, geminiAPIKey: String): GeminiResponse {
        try {

            println("getGeminiResponse() chatRequest=$chatRequest and $geminiAPIKey")

            val response = RetrofitInstance.api.chatCompletion(
                chatRequest = chatRequest ,
                key = geminiAPIKey
            )
            println("getGeminiResponse() ${response}")
            return response
        } catch (e: Exception) {
            println("getGeminiResponse() ${e.toString()}")
            e.printStackTrace()
            return GeminiResponse()
        }
    }
}
