package org.icann.ua.readiness.android.services.smtp

import android.content.Context
import android.content.Intent
import android.net.Uri
import jakarta.mail.internet.InternetAddress
import org.icann.ua.readiness.android.services.IDNAUtils

/**
 * Send an email with Android Email Intent.
 *
 * This is not really sending an email bug delegating it to another application.
 */
class EmailIntentRequestSample(context: Context) : SmtpRequestSample(context) {

    override fun toString(): String {
        return "Intent"
    }

    override suspend fun sendEmail(
        email: String,
        subject: String,
        body: String
    ): String {
        /*
         * Intent transmits data as is and EAI support should be achieved by the email application,
         * therefore it is really uncertain and not under our control whether EAI compliance will
         * be met.
         * A mitigation for application that will convert domain to A-label without
         * IDNA 2008 compliance could be to convert the domain before starting the intent but
         * email address may therefore be displayed in A-label to the user, this may be confusing.
         * If you need compliance in this context, the best way is to test email application and
         * create or upvote bug reports.
         */

        // Validate email address to ensure it will be correctly handled by an EAI compliant app
        try {
            // Due to some Jakarta bugs, need to convert domain in A-label before validation
            val localPart = email.substringBeforeLast("@")
            val domain = email.substringAfterLast("@")
            val emailCompliant = "$localPart@${IDNAUtils.domainToAscii(domain)}"
            val internetAddress = InternetAddress(emailCompliant)
            internetAddress.validate()
        } catch (ex: Exception) {
            return "ERROR: ${ex.message}"
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        return if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            "Done"
        } else {
            "Failed to launch email intent, most probably there is no email application on your device"
        }
    }
}