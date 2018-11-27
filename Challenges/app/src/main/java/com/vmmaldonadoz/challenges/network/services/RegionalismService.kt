package com.vmmaldonadoz.challenges.network.services

import com.vmmaldonadoz.challenges.models.Regionalism
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RegionalismService {

    @GET("cj5v-834c.json")
    fun getRegionalisms(): Single<List<Regionalism>>

    @GET("cj5v-834c.json")
    fun getFilteredRegionalisms(@Query("regionalism") regionalism: String): Single<List<Regionalism>>

}