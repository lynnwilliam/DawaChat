package com.dawaluma.desisionmaker.database

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlinx.serialization.json.Json

class QuestionAnswerDB {

    fun createQuestionAnswerRecord(context: Context,question: String, conversationID: Long): QuestionAnswer {

        var newConversationID = conversationID
        if ( conversationID == -1L){
            val conversation = Conversation(
                conversationID = getIDMaker(context).getNewID(),
                timestamp = System.currentTimeMillis(),
                conversationLabel = question)

            newConversationID = conversation.conversationID
        }

        return QuestionAnswer(question = question,
            timestamp = System.currentTimeMillis(),
            conversationID = newConversationID )
    }

    fun getConversationDB(context: Context): ConversationDBInterface {
        return ConversationDB(context)
    }

    private class ConversationDB(val context: Context) : ConversationDBInterface {

        val named = "conversation"
        override fun getConversations(): List<Conversation> {
            val conversationList = mutableListOf<Conversation>()
            val sharedPref: SharedPreferences = context.getSharedPreferences(named, Context.MODE_PRIVATE)

            // Iterate through all the entries in SharedPreferences
            val allEntries = sharedPref.all
            for ((_, value) in allEntries) {
                conversationList.add(Json.decodeFromString(Conversation.serializer(), value.toString()))
            }
            conversationList.sortByDescending { it.conversationID }
            return conversationList
        }

        override fun getConversation(conversationID: Long): Conversation? {
            var objectS = context.getSharedPreferences(named, Context.MODE_PRIVATE).getString(conversationID.toString(),"")
            objectS?.let{
                return Json.decodeFromString(Conversation.serializer(), it)
            }
            return null
        }

        override fun insertConversation(conversation: Conversation) {
            getEditor(context).putString( conversation.conversationID.toString() ,
                Json.encodeToString(Conversation.serializer(), conversation)).apply()
        }

        override fun deleteConversation(conversationID: Long) {
            getEditor(context).remove(conversationID.toString()).commit()
        }

        override fun createConversation(question: String, iconID: Int): Conversation {
            val conversation = Conversation(
                conversationLabel = question,
                timestamp = System.currentTimeMillis(),
                conversationID = getIDMaker(context).getNewID("conversation"),
                iconID = iconID
            )

            insertConversation(conversation = conversation)
            return conversation
        }

        private fun getEditor(context: Context): SharedPreferences.Editor{
            return context.getSharedPreferences(named, Context.MODE_PRIVATE).edit()
        }
    }

    fun getQuestionAnswerDB(context: Context) : QuestionInterface {
        return QuestionADB(context)
    }

    private class QuestionADB(var context: Context) : QuestionInterface {

        private fun getEditor(conversationID: Long): Editor{
            return getSharedPref(conversationID).edit()
        }

        private fun getSharedPref(conversationID: Long): SharedPreferences{
            return context.getSharedPreferences(conversationID.toString(), Context.MODE_PRIVATE)
        }

        override fun insertQuestion(questionAnswer: QuestionAnswer) {
            getEditor(questionAnswer.conversationID).putString(questionAnswer.timestamp.toString(),
                Json.encodeToString(QuestionAnswer.serializer(), questionAnswer) ).commit()
        }

        override fun deleteQuestion(questionAnswer: QuestionAnswer) {
            getEditor(questionAnswer.conversationID).remove(questionAnswer.timestamp.toString())
        }

        override fun updateQuestion(questionAnswer: QuestionAnswer) {
            insertQuestion(questionAnswer)
        }

        override fun getQuestion(questionID: Long, conversationID: Long): QuestionAnswer? {
            val question = getSharedPref(conversationID).getString(questionID.toString(),"")
            if (question.isNullOrBlank()){
                return null
            }
            return Json.decodeFromString(QuestionAnswer.serializer(), question)
        }

        override fun getAllQuestions(conversationID: Long): List<QuestionAnswer> {
            val conversationList = mutableListOf<QuestionAnswer>()
            val sharedPref: SharedPreferences = context.getSharedPreferences(conversationID.toString(), Context.MODE_PRIVATE)

            val allEntries = sharedPref.all
            for ((_, value) in allEntries) {
                conversationList.add(
                    Json.decodeFromString(QuestionAnswer.serializer(), value.toString())
                )
            }
            conversationList.sortBy { it.timestamp }
            return conversationList
        }
    }

    fun getPinDB(context: Context): PinnedInterface {
        return PinDB(context)
    }

    private class PinDB(var context: Context) : PinnedInterface {

        override fun insertPin(pin: Pin) {
            getEditor().putString(pin.pinID.toString(),
                Json.encodeToString(Pin.serializer(), pin) ).commit()
        }

        override fun deletePin(pinID: Long) {
            getEditor().remove(pinID.toString()).commit()
        }

        override fun deletePinsForConversation(conversationID: Long) {

            for ( pin in getPins()){
                if ( pin.conversationID == conversationID){
                    deletePin(pin.pinID)
                }
            }
        }

        override fun getPins(): List<Pin> {
            val conversationList = mutableListOf<Pin>()
            val sharedPref: SharedPreferences = context.getSharedPreferences("pinned", Context.MODE_PRIVATE)

            // Iterate through all the entries in SharedPreferences
            val allEntries = sharedPref.all
            for ((_, value) in allEntries) {
                conversationList.add(
                    Json.decodeFromString(Pin.serializer(), value.toString())
                )
            }
            return conversationList
            
        }

        private fun getEditor(): Editor{
            return context.getSharedPreferences("pinned", Context.MODE_PRIVATE).edit()
        }

    }

}