package com.dawaluma.desisionmaker.database

interface ConversationDBInterface {
    fun getConversations(): List<Conversation>
    fun getConversation(conversationID: Long): Conversation?
    fun insertConversation(conversation: Conversation)
    fun deleteConversation(conversationID: Long)
    fun createConversation(question: String, iconID: Int): Conversation
}