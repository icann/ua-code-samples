package org.icann.ua.readiness.android.services.http

import android.content.Context
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.icann.ua.readiness.android.services.IDNAUtils

class FuelHttpRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "Fuel"
    }

    override suspend fun makeRequest(url: String, context: Context): String {
        val idnaCompliantUrl: String
        try {
            // As Android stack is not IDNA 2008 compliant, we need to convert host to ASCII ourselves
            idnaCompliantUrl = IDNAUtils.hostToAscii(url)
        } catch (ex: Exception) {
            return "ERROR: ${ex.message}"
        }
        val (_, _, result) = idnaCompliantUrl
            .httpGet()
            .responseString()

        return when (result) {
            is Result.Failure -> {
                "ERROR: ${result.error.message}"
            }
            is Result.Success -> {
                result.get()
            }
        }
    }
}