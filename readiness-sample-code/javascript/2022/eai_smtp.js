const SMTPConnection = require("nodemailer/lib/smtp-connection");

class SmtpClient {
    constructor(nodemailer_transport) {
        this.client = nodemailer_transport;
    }

    async support_SMTPUTF8() {
        let connection = new SMTPConnection(this.client.options);
        return new Promise(contains_SMTPUTF8 => {
            connection.connect(() => {
                // wrapper to access _supportedExtensions. Since the maintainers did not
                // make it public API, we cannot guarantee this name won't change in future
                // versions of nodemailer
                contains_SMTPUTF8(connection['_supportedExtensions'].includes('SMTPUTF8'));
                connection.close();
            });
        });
    }
}

async function eai_smtp(email, smtp_client, results, msg) {
    if (!email.includes('@') || !email.substring(email.lastIndexOf('@') + 1).includes('.')) {
        return results.addResult(true, "[Note] email does not contains the '@' AND '.' characters in this order", email);
    }

    const localpart = email.substring(0, email.lastIndexOf('@'));
    const is_localpart_non_ascii = /[\u0080-\uFFFF]/.test(localpart);
    // we don't need to check the domain part, since it will be converted into a A-LABEL automatically by nodemailer
    if (is_localpart_non_ascii && !await smtp_client.support_SMTPUTF8()) {
        return results.addResult(true, '[Note] SMTP server does not support SMTPUTF8', email);
    }

    try {
        await smtp_client.client.sendMail({
            from: "ua@test.org",
            to: email,
            subject: "Registration successful",
            text: msg
        });
    } catch (err) {
        return results.addResult(true,`[nodemailer] Failed to send email: ${err.message}`, email);
    }

    return results.addResult(false,`[nodemailer] Email has been sent successfully. See it <a href="${process.env.MAILHOG}" target="_blank">here</a>!`, email);
}

module.exports = { eai_smtp, SmtpClient };