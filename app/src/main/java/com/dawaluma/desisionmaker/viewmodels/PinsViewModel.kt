package com.dawaluma.desisionmaker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.IntentLauncher
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.database.QuestionAnswer
import com.dawaluma.desisionmaker.database.QuestionAnswerDB

class PinsViewModel(
    private var application: Application,
    private val intentLauncher: IntentLauncher,
    public val appSettings: AppSettingsContract) : AndroidViewModel(application){

    fun openMapWithAddress(address: String) {
        intentLauncher.openMapWithAddress(address)
    }

    fun openVideo(video: DataType.Video) {
        intentLauncher.openVideo(video)
    }

    private val _conversations = MutableLiveData<List<QuestionAnswer>>().apply {

        val listPins = QuestionAnswerDB().getPinDB(application).getPins()
        val questionAnswerList = mutableListOf<QuestionAnswer>()
        for ( pinX in listPins){

            var pinQuestion = QuestionAnswerDB().getQuestionAnswerDB(application).getQuestion(conversationID = pinX.conversationID, questionID = pinX.answerTimestamp)
            if ( pinQuestion!= null){
                questionAnswerList.add(pinQuestion)
            }
        }
        questionAnswerList.sortByDescending { it.timestamp}
        value = questionAnswerList
    }
    val conversations: LiveData<List<QuestionAnswer>> = _conversations
}

class PinsListViewModelFactory(private val application: Application,private val intentLauncher: IntentLauncher) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PinsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val appSettings = (application as DawaApplication).appSettings
            return PinsViewModel(application, intentLauncher, appSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}