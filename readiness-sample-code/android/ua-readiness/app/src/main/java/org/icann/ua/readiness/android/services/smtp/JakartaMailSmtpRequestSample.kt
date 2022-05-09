package org.icann.ua.readiness.android.services.smtp

import android.content.Context
import jakarta.mail.Message
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.icann.ua.readiness.android.services.IDNAUtils
import java.text.Normalizer
import java.util.*

class JakartaMailSmtpRequestSample(context: Context) : SmtpRequestSample(context) {

    override fun toString(): String {
        return "Jakarta"
    }

    override suspend fun sendEmail(
        email: String,
        subject: String,
        body: String
    ): String {
        val smtpSettings = getSettings()

        var emailCompliant = email
        /*
         * Jakarta mail is EAI compliant with 2 issues:
         *   - it rejects domains that are not NFC normalized
         *   - it rejects some unicode domains
         * In such case, first try to normalize, then convert domain to A-label. We do normalization
         * first to get an email address the closest possible to the user input because once
         * converted in A-label it may be displayed as is to the user.
         */

        try {
            val internetAddress = InternetAddress(emailCompliant)
            internetAddress.validate()
        } catch (ex: Exception) {
            val localPart = emailCompliant.substringBeforeLast("@")
            val domain = emailCompliant.substringAfterLast("@")
            // Email address with domain normalized
            emailCompliant = "$localPart@${Normalizer.normalize(domain, Normalizer.Form.NFC)}"
        }

        try {
            val internetAddress = InternetAddress(emailCompliant)
            internetAddress.validate()
        } catch (e: Exception) {
            // Email address is still rejected, convert domain to A-label
            val localPart = emailCompliant.substringBeforeLast("@")
            val domain = emailCompliant.substringAfterLast("@")
            try {
                emailCompliant = "$localPart@${IDNAUtils.domainToAscii(domain)}"
            } catch (ex: Exception) {
                return "Domain part is invalid: ${ex.message}"
            }
        }

        val props = Properties()
        props["mail.smtp.host"] = smtpSettings.server
        props["mail.smtp.port"] = smtpSettings.port
        // enable UTF-8 support, mandatory for EAI support
        props["mail.mime.allowutf8"] = true

        val session: Session = Session.getInstance(props)

        return try {
            val msg = MimeMessage(session)
            //set message headers for internationalized content
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8")
            msg.addHeader("Content-Transfer-Encoding", "8bit")
            msg.addHeader("format", "flowed")
            msg.setFrom("no-reply@eai.test")
            msg.setRecipients(Message.RecipientType.TO, emailCompliant)
            msg.subject = subject
            msg.sentDate = Date()
            msg.setText(body, "UTF-8")
            Transport.send(msg)
            "Message sent to $email"
        } catch (ex: Exception) {
            "ERROR: ${ex.message}"
        }
    }
}