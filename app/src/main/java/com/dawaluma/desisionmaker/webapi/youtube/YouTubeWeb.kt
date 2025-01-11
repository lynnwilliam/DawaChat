package com.dawaluma.desisionmaker.webapi.youtube

import com.dawaluma.desisionmaker.database.DataType
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("key") apiKey: String,
        @Query("regionCode") regionCode: String = "US"
    ): Response<YouTubeResponse>
}

data class YouTubeResponse(
    @SerializedName("items") val items: List<YouTubeVideo>
)

data class YouTubeVideo(
    @SerializedName("id") val id: VideoId,
    @SerializedName("snippet") val snippet: VideoSnippet
)

data class VideoId(
    @SerializedName("videoId") val videoId: String
)

data class VideoSnippet(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnails") val thumbnails: Thumbnails
)

data class Thumbnails(
    @SerializedName("high") val high: Thumbnail
)

data class Thumbnail(
    @SerializedName("url") val url: String
)

object RetrofitInstance {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    val api: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }
}

class YouTubeWeb() {

    suspend fun getVideosFromQuery(question: String, apiKey: String): List<DataType.Video>?{

        val yrResponse =  RetrofitInstance.api.searchVideos(query = question, apiKey = apiKey)
        if ( yrResponse.isSuccessful){
            yrResponse.body()?.let{

                return it.items.map { video ->
                    DataType.Video(
                        text = video.snippet.title,
                        description = video.snippet.description,
                        thumbnailURL = video.snippet.thumbnails.high.url,
                        videoID = video.id.videoId
                    )
                }.toMutableList()
            }
        }
       return null
    }

}
