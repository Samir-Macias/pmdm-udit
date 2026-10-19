package com.example.mystore.data.remote
import com.example.mystore.data.model.AgentResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ValorantApiService {
    @GET("v1/agents")
    suspend fun getAgents(
        @Query("language") language: String = "es-ES",
        @Query("isPlayableCharacter") isPlayableCharacter: Boolean = true
    ):AgentResponse
}