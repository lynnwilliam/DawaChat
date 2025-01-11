package com.dawaluma.desisionmaker.webapi

import android.content.Context
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.querybuilder.AnswerEngine

class DemoModeData(val context: Context) {

    fun getTextData(): DataType.TextType{
        val array = context.resources.getStringArray(R.array.professorDemoChats)
        return DataType.TextType(array.random())
    }

    fun getVideoData(): List<DataType.Video> {

        val randomVideos:MutableList<DataType.Video>  = mutableListOf()
        randomVideos.add(
            DataType.Video(
                videoID = "buzsasTfC4s",
                text = "Hellcat vs Z06 Corvette",
                description = "Dodge Challenger Hellcat vs Corvette z06",
                thumbnailURL = "https://i.ytimg.com/vi/buzsasTfC4s/hqdefault.jpg"
                )
        )
        randomVideos.add(
            DataType.Video(
                videoID = "_XMSbdalHDQ",
                text = "How to make No Knead Bread",
                description = "This is the dutch oven i use",
                thumbnailURL = "https://i.ytimg.com/vi/_XMSbdalHDQ/hqdefault.jpg"
            )
        )
        return randomVideos
    }

    fun getPlacesData(): List<AnswerEngine.Place> {
        return listOf(
            AnswerEngine.Place(
                name = "Mall of America®",
                address = "60 E Broadway, Bloomington, MN 55425, United States",
                latitude = 44.8548651,
                longitude = -93.2422148,
                iconURL = "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/shopping-71.png"
            )
        )
    }
}