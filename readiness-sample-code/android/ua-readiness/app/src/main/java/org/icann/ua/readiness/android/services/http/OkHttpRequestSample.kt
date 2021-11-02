package org.icann.ua.readiness.android.services.http

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import org.icann.ua.readiness.android.services.IDNAUtils

class OkHttpRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "okHttp"
    }

    override suspend fun makeRequest(url: String, context: Context): String {

        val client = OkHttpClient()
        return try {
            // As okHttp is not IDNA 2008 compliant, we need to convert host to ASCII ourselves
            val idnaCompliantUrl = IDNAUtils.hostToAscii(url)
            val request = Request.Builder()
                .url(idnaCompliantUrl)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    "ERROR: ${response.message}"
                } else {
                    response.body!!.string()
                }
            }
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        }
    }
}