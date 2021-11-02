package org.icann.ua.readiness.android.services.smtp

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.icann.ua.readiness.android.R
import java.lang.Integer.parseInt

abstract class SmtpRequestSample(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    class SmtpSettings(val server: String, val port: Int) {
    }

    fun getSettings(): SmtpSettings {
        val defaultServer = context.getString(R.string.default_smtp_server)
        val defaultPort = context.getString(R.string.default_smtp_port)
        val smtpServer = sharedPreferences.getString("smtp_server", defaultServer) ?: defaultServer
        val smtpPort = sharedPreferences.getString("smtp_port", defaultPort) ?: defaultPort
        return SmtpSettings(smtpServer, parseInt(smtpPort))

    }

    /**
     * Send an email with EAI support.
     */
    abstract suspend fun sendEmail(
        email: String,
        subject: String,
        body: String
    ): String;
}