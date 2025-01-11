package com.dawaluma.desisionmaker.database

interface QuestionInterface {
    fun insertQuestion(questionAnswer: QuestionAnswer)
    fun deleteQuestion(questionAnswer: QuestionAnswer)
    fun updateQuestion(questionAnswer: QuestionAnswer)
    fun getAllQuestions(conversationID: Long) : List<QuestionAnswer>
    fun getQuestion(questionID: Long, conversationID: Long) : QuestionAnswer?
}