package com.dawaluma.desisionmaker.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dawaluma.desisionmaker.DawaApplication
import com.dawaluma.desisionmaker.database.Conversation
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.database.QuestionAnswer
import com.dawaluma.desisionmaker.database.QuestionAnswerDB
import com.dawaluma.desisionmaker.IntentLauncher
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.querybuilder.AnswerEngine
import kotlinx.coroutines.launch

class StartChatViewModel(
    private var  application: Application,
    private val intentLauncher: IntentLauncher,
    val appSettings: AppSettingsContract) : AndroidViewModel(application){

    private var randomBackgroundID : Int

    //current conversation
    private val _questions = MutableLiveData<List<QuestionAnswer>>()
    val questions: LiveData<List<QuestionAnswer>> get() = _questions

    init{
        randomBackgroundID = createRandomBackgroundID()
        _questions.value = listOf()
    }

    //Loading state
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading

    //current conversation
    private var conversation: Conversation? = null

    //prompt text
    private val _showStartingPrompt = MutableLiveData(true)
    val showStartingPrompt: LiveData<Boolean> get() = _showStartingPrompt

    // Function to update the list of questions
    fun updateQuestions(newQuestions: List<QuestionAnswer>) {
        _questions.value = newQuestions
    }

    // Function to add a single question and update the list
    fun addQuestion(question: QuestionAnswer) {
        val updatedList = _questions.value?.toMutableList() ?: mutableListOf()
        updatedList.add(question)
        _questions.value = updatedList
    }

    // Function to perform the network request
    fun fetchAnswer(question: String) {

        if ( question.isBlank() || question.isEmpty()){
            return
        }

        _loading.value = true

        //create a new conversation if we have none already
        if ( conversation == null ){
            conversation = QuestionAnswerDB().getConversationDB(application).createConversation(question, iconID = randomBackgroundID)
        }

        // Define the query based on the input text
        val questionAnswer = QuestionAnswerDB().createQuestionAnswerRecord(
            context = application ,
            question = question,
            conversationID = conversation!!.conversationID
        )

        viewModelScope.launch {
            if ( question.isNullOrBlank() || question.isNullOrBlank()){
                questionAnswer.answer = DataType.TextType( "Hello! How can I assist you today?") // make it return random answers for empty queries?
                questionAnswer.answer.isLocalMessage = true
                _loading.value = false
                return@launch
            }

            questionAnswer.answer = AnswerEngine(
                context = application,
                question = questionAnswer, appSettings, intentLauncher.getDeviceLocation()).getAnswer()
            if ( !questionAnswer.answer.isLocalMessage && questionAnswer.question.isNotEmpty()) {
                QuestionAnswerDB().getQuestionAnswerDB(application).insertQuestion(questionAnswer)
            }
            _loading.value = false
            _showStartingPrompt.value = false
            addQuestion(questionAnswer)
        }
    }

    fun hasMapsPermissions(context: Context): Boolean {
        // Await the result of the findCurrentPlace call
        return  (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    fun loadConversation(context: Context,conversationID : Long){
        _showStartingPrompt.value = false
        viewModelScope.launch {
            _questions.value  = QuestionAnswerDB().getQuestionAnswerDB(context).getAllQuestions(conversationID = conversationID)
            conversation = QuestionAnswerDB().getConversationDB(context).getConversation(conversationID)
        }
    }

    private fun createRandomBackgroundID(): Int{
        println("createRandomBackgroundID() is getting called")
        return (0..(20)).random()
    }

    fun getBackgroundID(): Int {
        conversation?. let{
            return it.iconID
        }
        return randomBackgroundID
    }

    fun requestLocationPermissions() {
        intentLauncher.requestLocationPermissions()
    }

    fun openMapWithAddress(mapAddress: String) {
        intentLauncher.openMapWithAddress(mapAddress)
    }

    fun openVideo(video: DataType.Video) {
        intentLauncher.openVideo(video)
    }
}

class StartChatViewModelFactory(private val application: Application, private val intentLauncher: IntentLauncher) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val appSettings = (application as DawaApplication).appSettings
            return StartChatViewModel(application, intentLauncher, appSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
