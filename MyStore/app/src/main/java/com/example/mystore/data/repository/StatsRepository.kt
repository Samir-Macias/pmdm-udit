package com.example.mystore.data.repository

import com.example.mystore.data.model.AccountDataDto
import com.example.mystore.data.model.MatchDto
import com.example.mystore.data.model.MmrDataDto
import com.example.mystore.data.remote.RetrofitClient

class StatsRepository {
    private val api = RetrofitClient.henrikApiService

    suspend fun getAccount(name: String, tag: String): AccountDataDto {
        val response = api.getAccount(name, tag)
        return response.data ?: throw Exception("Jugador no encontrado")
    }

    suspend fun getMmr(region: String, name: String, tag: String): MmrDataDto? {
        val response = api.getMmr(region, name, tag)
        return response.data
    }

    suspend fun getMatches(region: String, name: String, tag: String): List<MatchDto> {
        val response = api.getMatches(region, name, tag)
        return response.data ?: emptyList()
    }
}