package org.icann.ua.readiness.android.services.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitResponseService {

    @GET
    fun getResponse(@Url url: String): Call<String>
}