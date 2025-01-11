package com.dawaluma.desisionmaker.webapi.gpt

import com.dawaluma.desisionmaker.querybuilder.AnswerEngine.Models
import kotlinx.serialization.Serializable

@Serializable
sealed class OpenAIResponse

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
): OpenAIResponse(){
    fun getGPTResponse() : GPTResponse {
        return GPTResponse(this)
    }
}

@Serializable
data class ErrorResponse(
    val error: ErrorDetails? = null
) : OpenAIResponse()

@Serializable
data class ErrorDetails(
    val message: String,
    val type: String,
    val param: String? = null,
    val code: String? = null
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_tokens_details: PromptTokensDetails,
    val completion_tokens_details: CompletionTokensDetails,
)

@Serializable
data class PromptTokensDetails(
    val cached_tokens: Int
)

@Serializable
data class CompletionTokensDetails(
    val reasoning_tokens: Int
)

@Serializable
data class GPTRequest(
    val model: String= Models.expensiveModel,
    val messages: List<Message>,
    val temperature: Double = 0.5 // Default temperature value
)