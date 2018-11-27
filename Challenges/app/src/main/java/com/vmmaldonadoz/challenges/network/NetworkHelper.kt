package com.vmmaldonadoz.challenges.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {


    fun <T> getClient(
            baseUrl: String,
            service: Class<T>
    ): T {
        return getRetrofitInstance(baseUrl)
                .create(service)
    }


    @Volatile
    private var INSTANCE: Retrofit? = null

    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(baseUrl).also { INSTANCE = it }
        }
    }

    private fun buildDatabase(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }

}