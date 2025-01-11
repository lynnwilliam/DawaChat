package com.dawaluma.desisionmaker.database

interface PinnedInterface {
    public fun insertPin(pin: Pin)
    public fun deletePin(pinID: Long)
    public fun deletePinsForConversation(conversationID: Long)
    public fun getPins(): List<Pin>
}