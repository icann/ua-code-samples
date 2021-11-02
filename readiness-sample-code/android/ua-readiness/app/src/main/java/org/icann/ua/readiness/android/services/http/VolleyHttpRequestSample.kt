package org.icann.ua.readiness.android.services.http

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.icann.ua.readiness.android.services.IDNAUtils
import java.util.concurrent.TimeUnit

class VolleyHttpRequestSample : HttpRequestSample {

    override fun toString(): String {
        return "Volley"
    }

    override suspend fun makeRequest(url: String, context: Context): String {
        return try {
            // As Android stack is not IDNA 2008 compliant, we need to convert host to ASCII ourselves
            val idnaCompliantUrl = IDNAUtils.hostToAscii(url)
            val future = RequestFuture.newFuture<String>()
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(Request.Method.GET, idnaCompliantUrl, future, future)
            queue.add(stringRequest)
            future.get(10, TimeUnit.SECONDS)
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        }
    }
}