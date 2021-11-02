package org.icann.ua.readiness.android.services.http

import android.content.Context

interface HttpRequestSample {

    /**
     * Make a UA compliant HTTP GET request.
     */
    suspend fun makeRequest(url: String, context: Context): String;
}