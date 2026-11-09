package com.example.mystore.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://valorant-api.com/"
    private const val HENRIK_BASE_URL = "https://api.henrikdev.xyz/"
    private const val HENRIK_API_KEY = "HDEV-16cb3b46-d1a5-41dc-bc8b-0ddbf1e13206"

    val apiService: ValorantApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ValorantApiService::class.java)
    }

    //OkHttpClient que adjunta la API Key automáticamente
    private val henrikOkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", HENRIK_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }
    val henrikApiService: HenrikApiService by lazy {
        Retrofit.Builder()
            .baseUrl(HENRIK_BASE_URL)
            .client(henrikOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HenrikApiService::class.java)
    }

}