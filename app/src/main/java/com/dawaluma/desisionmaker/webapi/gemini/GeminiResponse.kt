package com.dawaluma.desisionmaker.webapi.gemini

import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
    val usageMetadata: UsageMetadata = UsageMetadata(0,0,0),
    val modelVersion: String = ""
)

@Serializable
data class Candidate(
    val content: Content,
    val role: String,
    val finishReason: String,
    val avgLogprobs: Double
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)