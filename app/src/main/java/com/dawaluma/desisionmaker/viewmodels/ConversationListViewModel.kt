package com.dawaluma.desisionmaker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.database.Conversation
import com.dawaluma.desisionmaker.database.QuestionAnswerDB

class ConversationListViewModel(private var application: Application) : AndroidViewModel(application){

    fun deleteConversation(conversation: Conversation) {
        conversation.delete(application)
        _conversations.value = QuestionAnswerDB().getConversationDB(application).getConversations()
    }

    private val _conversations = MutableLiveData<List<Conversation>>().apply {
        value = QuestionAnswerDB().getConversationDB(application).getConversations()
    }
    val conversations: LiveData<List<Conversation>> = _conversations
}

class ConversationListViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationListViewModel(application,) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}