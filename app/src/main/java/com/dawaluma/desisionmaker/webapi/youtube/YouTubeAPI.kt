package com.dawaluma.desisionmaker.webapi.youtube

import android.content.Context
import com.dawaluma.desisionmaker.database.DataType

interface YouTubeVideoAPI{
    suspend fun getVideosFromQuery(question: String, apiKey: String): List<DataType.Video>?
}

 class DefaultYouTubeAPI : YouTubeVideoAPI{

    override suspend fun getVideosFromQuery(question: String, apiKey: String): List<DataType.Video>? {
        return YouTubeWeb().getVideosFromQuery(question, apiKey)
    }
}

//MainEntry Point for the YouTube API
class YouTubeAPI(
    val context: Context,
    private val apiHandler:  YouTubeVideoAPI = DefaultYouTubeAPI() ) {

    suspend fun  getVideosFromQuery(question: String, apiKey: String): List<DataType.Video>? {
        return try{
            apiHandler.getVideosFromQuery(question, apiKey)
        }catch (e: Exception){
            return null
        }
    }
}

data class LatLong(val lat: Double, val long: Double)