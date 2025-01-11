package com.dawaluma.desisionmaker.querybuilder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.dawaluma.desisionmaker.MainActivity
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.webapi.maps.WebAPIMaps
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.database.QuestionAnswer
import com.dawaluma.desisionmaker.webapi.ChatEngine
import com.dawaluma.desisionmaker.webapi.DemoModeData
import com.dawaluma.desisionmaker.webapi.youtube.LatLong
import com.dawaluma.desisionmaker.webapi.youtube.YouTubeAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AnswerEngine(
    val context: Context,
    val question: QuestionAnswer,
    val appSettings: AppSettingsContract,
    val deviceLatLong: LatLong?){

    private var level0: Query? = null

    suspend fun getAnswer(): DataType {
        return withContext(Dispatchers.IO) {
            getHighLevelAnswer(question.question, deviceLatLong) ?: getServerErrorResponse(context.getString(R.string.offlineResponse))
        }
    }

    private fun getServerErrorResponse(customMessage: String): DataType.TextType{
        val serverResponse = DataType.TextType(customMessage)
        serverResponse.isLocalMessage =true
        return serverResponse
    }

    private fun getNoPermissionsResponse(): DataType.TextType{
        val serverResponse = DataType.TextType(context.getString(R.string.nopermissions))
        serverResponse.isLocalMessage =true
        return serverResponse
    }

    private fun isMapsQuery(question: String): Boolean{
        return context.resources.getStringArray(R.array.mapprefix)
            .any { question.trim().startsWith("$it ", ignoreCase = true) }
    }

    private suspend fun getHighLevelAnswer(question: String, deviceLatLong: LatLong?): DataType?{

        if ( appSettings.hasMapsAPI() && isMapsQuery(question) ){
            if ( hasMapsPermissions() && deviceLatLong != null) {
                return mapsQuery(question, deviceLatLong)
            }else{
                return getNoPermissionsResponse()
            }
        }

        if ( appSettings.hasYouTubeAPI() && isVideoQuery(question)){
            return videoQuery(question)
        }

        level0 = Query(query = question, level = 0)
        return decideHigherLevelAnswer()
    }

    private fun isVideoQuery(question: String): Boolean{

        if ( question.endsWith(context.getString(R.string.videoKeyWord))){
            return true
        }

        return context.resources.getStringArray(R.array.video)
            .any { question.trim().startsWith("$it ", ignoreCase = true) }
    }

    private fun hasMapsPermissions(): Boolean {
        return  (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

     suspend fun videoQuery(question: String): DataType {

         if ( appSettings.isDemoMode()) {
             delay(1500)
             return DataType.VideoType(DemoModeData(context).getVideoData())
         }

         getVideosFromQuery(question, appSettings.getYouTubeAPI()!!)?.let{
             return DataType.VideoType(it)
         }
         return DataType.VideoType(emptyList())
    }

    private suspend fun getVideosFromQuery(question: String, apiKey: String): List<DataType.Video>? {
        return YouTubeAPI(context, ).getVideosFromQuery(question, apiKey)
    }

    @SuppressLint("MissingPermission")
    suspend fun mapsQuery(question: String, deviceLatLong: LatLong): DataType?{

        val placesList = getPlacesFromQuery(question,deviceLatLong)

        placesList?.let{
            if ( it.isEmpty()){
                return DataType.TextType(context.getString(R.string.defaultnomap))
            }

            //right now we only pass back the Top Result, but you can change this to pass
            // back more results
            val placeX= placesList.first()
            return DataType.MapsType(placeX.name, placeX.address, "", iconURL = placeX.iconURL)

        }
        return DataType.TextType(context.getString(R.string.defaultnomap))
    }

    data class Place(val name: String,
                     val address: String,
                     val latitude: Double = -1.0,
                     val longitude: Double = -1.0,
                     val iconURL: String?=""
    )

    private suspend fun getPlacesFromQuery(question: String, deviceLatLong: LatLong): List<Place>?{

        if ( appSettings.isDemoMode()) {
            delay(1500)
            return DemoModeData(context).getPlacesData()
        }

        return WebAPIMaps(context).getPlacesFromQuery(
            question,
            appSettings.getMapsAPI()!!,
            lat = deviceLatLong.lat,
            long = deviceLatLong.long)
    }

    private suspend fun decideHigherLevelAnswer() : DataType {

        if ( appSettings.isDemoMode()) {
            delay(1500)
            return DemoModeData(context).getTextData()
        }

        val apiKey = appSettings.getLLApiKey()
        println("wlynn apiKey=$apiKey")
        apiKey?.let{
            //get the original answer to the question
            val level0Answer: DataType.TextType = sendChatCompletionRequest(
                level0!!.query)
                ?: return getServerErrorResponse(context.getString(R.string.offlineResponse))

            return level0Answer
        }
        return getServerErrorResponse(context.getString(R.string.nollmkeysset))
    }

    object Models{
        const val expensiveModel = "gpt-3.5-turbo"
    }

    private suspend fun sendChatCompletionRequest(query: String): DataType.TextType? {
        return ChatEngine(context, appSettings).sendChatCompletionRequest(query)
    }
}


