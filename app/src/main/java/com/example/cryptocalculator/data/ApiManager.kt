package com.example.cryptocalculator.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


object ApiManager {

    private const val BASE_URL = "https://cex.io/api/"

    val ceoApi: CEOInterface = provideRetrofit().create(CEOInterface::class.java)

    private fun provideOkHttpClient() = with(OkHttpClient.Builder()) {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }.build()

    private fun provideRetrofit() = with(Retrofit.Builder()) {
        addConverterFactory(GsonConverterFactory.create())
        baseUrl(BASE_URL)
        client(provideOkHttpClient())
    }.build()

    interface CEOInterface {

        @GET("last_prices/{currency}")
        suspend fun getLastPrices(@Path ("currency") currency: String): TradingPairs
    }
}