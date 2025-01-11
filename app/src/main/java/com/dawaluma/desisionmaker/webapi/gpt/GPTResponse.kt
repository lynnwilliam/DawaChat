package com.dawaluma.desisionmaker.webapi.gpt

import androidx.annotation.VisibleForTesting


data class GPTResponse(val response: ChatCompletionResponse?=null,
                       val responseCode: Int?=null,
                       val error: String?=null){

    private var answer: String
    init{

        if ( response == null ){
            log("response is null")
            answer=""
        } else if (response.choices.isEmpty() ){
            log("response.choices is empty")
            answer=""
        }

        response?.let{
            val choice1: Choice = it.choices.first()
            if ( choice1.message.content.isEmpty()){
                log("response.choice.message.content is empty")
                answer=""
            }
        }

        answer = if( response!= null ){
            response.choices.firstOrNull()?.message?.content ?: ""
        }else{
            println("ChatNetwork: GPT response  is null ")
            ""
        }

        if (answer.isEmpty()){
            println("ChatNetwork: GPT is $response ")
        }
    }

    fun isValid(): Boolean {
        return answer.isNotEmpty()
    }

    fun getGPTAnswer(): String{
        return answer
    }

    fun log(text: String){
        println("GPTResponse: $text")
    }
}