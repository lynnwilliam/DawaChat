package com.dawaluma.desisionmaker.webapi.maps

import android.content.Context
import com.dawaluma.desisionmaker.querybuilder.AnswerEngine.Place
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface MapsApiService {
    @GET("maps/api/place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("location") location: String,
        @Query("rankby") rankby: String,
        @Query("key") apiKey: String
    ): Response<MapsResponse>
}

object MapsAPIInstance {
    private const val BASE_URL = "https://maps.googleapis.com/"

    @OptIn(ExperimentalSerializationApi::class)
    val api: MapsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
               Json{
                   ignoreUnknownKeys = true
               }.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(MapsApiService::class.java)
    }
}


interface GoogleMapsAPI{
    suspend fun getPlacesFromQuery(question: String, apiKey: String, lat: Double, long: Double): List<Place>?
}

//Main Entry Point for Dealing with Maps API
class WebAPIMaps(
    val context: Context,
    private val mapsAPI: GoogleMapsAPI = GoogleMapsWebImpl()) :
    GoogleMapsAPI {

    override suspend fun getPlacesFromQuery(question: String, apiKey: String, lat: Double, long: Double): List<Place>? {
        return mapsAPI.getPlacesFromQuery(question, apiKey, lat, long)
    }
}

private suspend fun getMapsAPIResponse(query: String, apiKey: String, lat: Double, long: Double): MapsResponse? {
    try {
        val response = MapsAPIInstance.api.searchPlaces(
           query = query,
            location = "$lat,$long",
            rankby = "distance",
            apiKey = apiKey
        )

        response.body()?.let{
           return it
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private class GoogleMapsWebImpl: GoogleMapsAPI {

    override suspend fun getPlacesFromQuery(question: String, apiKey: String, lat: Double, long: Double): List<Place>? {
        val mapsResponse =  getMapsAPIResponse(question, apiKey, lat, long)
        mapsResponse?.let{
            return it.results.map {
                Place(it.name, it.formatted_address, it.geometry.location.lat, it.geometry.location.lng)
            }.toMutableList()
        }
        return null
    }

}
