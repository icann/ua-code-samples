package org.icann.ua.readiness.android.services.http

import android.content.Context
import org.icann.ua.readiness.android.services.IDNAUtils
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHttpRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "Retrofit"
    }

    override suspend fun makeRequest(url: String, context: Context): String {
        return try {
            // As Retrofit is not IDNA 2008 compliant, we need to convert host to ASCII ourselves
            val idnaCompliantUrl = IDNAUtils.hostToAscii(url)
            val retrofit = Retrofit.Builder()
                .baseUrl(idnaCompliantUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            val service = retrofit.create(RetrofitResponseService::class.java)
            val call = service.getResponse(idnaCompliantUrl)
            val response = call.execute()
            response.body().toString()
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        }
    }
}