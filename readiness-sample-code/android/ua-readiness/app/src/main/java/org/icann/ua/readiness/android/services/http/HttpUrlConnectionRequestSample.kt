package org.icann.ua.readiness.android.services.http

import android.content.Context
import org.icann.ua.readiness.android.services.IDNAUtils
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpUrlConnectionRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "HttpUrlConnection"
    }

    override suspend fun makeRequest(url: String, context: Context): String {
        var urlConnection: HttpURLConnection? = null
        return try {
            // As Android stack is not IDNA 2008 compliant, we need to convert host to ASCII ourselves
            val idnaCompliantUrl = IDNAUtils.hostToAscii(url)
            urlConnection = URL(idnaCompliantUrl).openConnection() as HttpURLConnection
            val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
            inputStream.bufferedReader().use(BufferedReader::readText)
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        } finally {
            urlConnection?.disconnect()
        }
    }
}