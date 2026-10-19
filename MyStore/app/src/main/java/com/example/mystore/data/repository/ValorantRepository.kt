package com.example.mystore.data.repository

import com.example.mystore.data.model.AgentDto
import com.example.mystore.data.remote.RetrofitClient

class ValorantRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAgents(): List<AgentDto>{
        val response = api.getAgents()
        return response.data
    }
}