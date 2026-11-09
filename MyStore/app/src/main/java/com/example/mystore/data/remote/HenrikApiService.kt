package com.example.mystore.data.remote

import com.example.mystore.data.model.AccountResponse
import com.example.mystore.data.model.MatchesResponse
import com.example.mystore.data.model.MmrResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HenrikApiService {

    @GET("valorant/v1/account/{name}/{tag}")
    suspend fun getAccount(
        @Path("name") name: String,
        @Path("tag") tag: String
    ): AccountResponse

    @GET("valorant/v2/mmr/{region}/{name}/{tag}")
    suspend fun getMmr(
        @Path("region") region: String,
        @Path("name") name: String,
        @Path("tag") tag: String
    ): MmrResponse

    @GET("valorant/v3/matches/{region}/{name}/{tag}")
    suspend fun getMatches(
        @Path("region") region: String,
        @Path("name") name: String,
        @Path("tag") tag: String
    ): MatchesResponse
}