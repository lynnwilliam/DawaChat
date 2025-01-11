package com.dawaluma.desisionmaker.database

import android.content.Context
import com.dawaluma.desisionmaker.R
import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnswer(

    var answer: DataType = DataType.TextType(""),
    var question: String="",
    val timestamp: Long,
    var pinned: Boolean = false,
    val conversationID: Long) {

    fun togglePin(context: Context) {
        pinned = !pinned
        QuestionAnswerDB().getQuestionAnswerDB(context).updateQuestion(this)
        if (pinned){
            var pin = Pin(pinID = timestamp, conversationID = conversationID, answerTimestamp = timestamp)
            QuestionAnswerDB().getPinDB(context).insertPin(pin)
        }else{
            QuestionAnswerDB().getPinDB(context).deletePin(timestamp)
        }
    }

    fun hasPills(): Boolean {
        return ( question.split(" ").size <= 10)
    }

    fun getPills(context: Context, videoAPIEnabled: Boolean, mapsAPIEnabled: Boolean): List<Pill>{
        val pillList = mutableListOf<Pill>()

        if ( videoAPIEnabled && answer is DataType.TextType || answer is DataType.VideoType){
            var newQuestion = "$question ${context.getString(R.string.videoKeyWord)}"
            pillList.add( Pill(context.getString(R.string.relatedvideos),newQuestion, PillType.Video))
        }

        if ( mapsAPIEnabled && answer is DataType.MapsType){
            var newQuestion = "${context.getString(R.string.mapsKeyWord)} $question"
            pillList.add( Pill(context.getString(R.string.similarplaces),newQuestion, PillType.Maps))
        }
        return pillList
    }

}

enum class PillType{
    Maps, Video
}

data class Pill(var display: String,var question: String, var pillType: PillType)


@Serializable
sealed class DataType {

    @kotlinx.serialization.Transient
    var isLocalMessage: Boolean = false

    abstract fun getAnswerAsString(): String

    @Serializable
    data class TextType(var answer: String) : DataType() {
        override fun getAnswerAsString(): String {
            return answer
        }
    }

    @Serializable
    data class MapsType(val text: String, val mapsLocation: String, val distance: String, val iconURL: String?) : DataType() {
        override fun getAnswerAsString(): String {
            return "$text, Location: $mapsLocation, Distance: $distance"
        }
    }

    @Serializable
    data class VideoType(val videoList:List<Video>) : DataType() {
        override fun getAnswerAsString(): String {
            return "Videos Found ${videoList.size}"
        }
    }

    @Serializable
    data class Video(val text: String, val videoID: String, val thumbnailURL: String, val description: String)
}

@Serializable
data class Conversation(
    val conversationID: Long,
    val conversationVersion: Int = 1,
    val timestamp: Long,
    val conversationLabel: String,
    val iconID: Int=0,
) {
    fun delete(context: Context) {
        QuestionAnswerDB().getConversationDB(context).deleteConversation(conversationID)
        QuestionAnswerDB().getPinDB(context).deletePinsForConversation(conversationID)
    }
}

fun lookupDrawable(iconID: Int): Int {
    val randomIDList = mutableListOf(
        R.drawable.a,
        R.drawable.b,
        R.drawable.c,
        R.drawable.d,
        R.drawable.e,
        R.drawable.f,
        R.drawable.g,
        R.drawable.h,
        R.drawable.i,
        R.drawable.j,
        R.drawable.k,
        R.drawable.l,
        R.drawable.m,
        R.drawable.n,
        R.drawable.o,
        R.drawable.q,
        R.drawable.r,
        R.drawable.s,
        R.drawable.t,
        R.drawable.u,
        R.drawable.v,
        R.drawable.w
    )
    if ( iconID > randomIDList.size-1){
        return randomIDList[0]
    }
    return randomIDList[iconID]
}

@Serializable
data class Pin(
    val pinID: Long,
    val conversationID: Long,
    val answerTimestamp: Long)