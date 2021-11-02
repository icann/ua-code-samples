package org.icann.ua.readiness.android.services.http

import android.content.Context
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.icann.ua.readiness.android.services.IDNAUtils

class ApacheHttpRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "Apache"
    }

    override suspend fun makeRequest(url: String, context: Context): String {
        val httpClient = HttpClients.createDefault()
        return try {
            // convert host to ASCII as Apache HTTP client is not IDNA compliant
            val idnaCompliantUrl = IDNAUtils.hostToAscii(url)
            val request = HttpGet(idnaCompliantUrl)
            val response = httpClient.execute(request);
            val code = response.code
            if (code == HttpStatus.SC_OK) {
                EntityUtils.toString(response.entity)
            } else {
                response.entity.content.close()
                "ERROR: ${response.reasonPhrase}"
            }
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        }
    }
}