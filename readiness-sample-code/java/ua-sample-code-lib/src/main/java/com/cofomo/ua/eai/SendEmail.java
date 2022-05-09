package com.cofomo.ua.eai;

import com.cofomo.ua.idna.ConvertDomain;
import com.cofomo.ua.idna.ConvertDomain.ConversionResult;
import com.google.common.base.CharMatcher;
import com.sun.mail.smtp.SMTPTransport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.text.Normalizer;
import java.util.Date;
import java.util.Properties;

public class SendEmail {


  public static class WithJakartaMail {

    private static final String DEFAULT_SENDER = "ua@test.org";
    private static final String DEFAULT_SUBJECT = "UA Compliant Email";
    private static final String DEFAULT_CONTENT = "This email was sent to test UA compliance.";

    public static SmtpResult send(String to, String host, int port) {
      return send(to, host, port, null, null, null);
    }

    public static SmtpResult send(String to, String host, int port,
        String sender, String subject, String content) {
      if (sender == null || sender.isEmpty()) {
        sender = DEFAULT_SENDER;
      }
      if (subject == null || subject.isEmpty()) {
        subject = DEFAULT_SUBJECT;
      }
      if (content == null || content.isEmpty()) {
        content = DEFAULT_CONTENT;
      }

      SmtpResult result = new SmtpResult(to);
      String compliantTo = to;
      /*
       * Jakarta mail is EAI compliant with 2 issues:
       *   - it rejects domains that are not NFC normalized
       *   - it rejects some unicode domains
       * In such case, first try to normalize, then convert domain to A-label. We do normalization
       * first to get an email address the closest possible to the user input because once
       * converted in A-label it may be displayed as is to the user.
       */

      try {
        InternetAddress internetAddress = new InternetAddress(compliantTo, false);
        internetAddress.validate();
      } catch (Exception e) {
        String localPart = compliantTo.substring(0, compliantTo.lastIndexOf("@") - 1);
        String domain = compliantTo.substring(compliantTo.lastIndexOf("@") + 1);
        // Email address with domain normalized
        compliantTo = localPart + "@" + Normalizer.normalize(domain, Normalizer.Form.NFC);
        result.setNormalized(compliantTo);
      }

      try {
        InternetAddress internetAddress = new InternetAddress(compliantTo, false);
        internetAddress.validate();
      } catch (Exception e) {
        // Email address is still rejected, convert domain to A-label
        convertDomainToAlabel(compliantTo, result);
        if (!result.isValid()) {
          // There is nothing else we can do, email address is invalid
          return result;
        }
        compliantTo = result.getConverted();
      }

      Properties props = System.getProperties();

      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", port);
      // enable UTF-8 support, mandatory for EAI support
      props.put("mail.mime.allowutf8", true);

      Session session = Session.getInstance(props, null);
      // jakarta-mail does not return an exception when server does not support SMTPUTF8, so we
      // need to check it ourselves
      // credits: https://stackoverflow.com/questions/69885806/check-for-mail-server-smtputf8-support
      try (Transport transport = session.getTransport()) {
        transport.connect(host, port, null, null);

        if (transport instanceof SMTPTransport &&
            !((SMTPTransport) transport).supportsExtension("SMTPUTF8")) {
          result.setEmailDowngraded(true);
          // if local-part is not ASCII only, stop here
          String localPart = compliantTo.substring(0, compliantTo.lastIndexOf("@") - 1);
            if (!CharMatcher.ascii().matchesAllOf(localPart)) {
              result.setError("SMTP server does not support SMTPFUTF8 option and local-part"
                  + "contains non-ASCII characters");
              return result;
            }
          // server does not support SMTPUTF8 extension, convert domain to A-label if not already done
          if (result.getConverted().isBlank()) {
            convertDomainToAlabel(compliantTo, result);
            if (!result.isValid()) {
              // There is nothing else we can do, email address is invalid
              return result;
            }
            compliantTo = result.getConverted();
          }
        }
      } catch (MessagingException e) {
        // ignore
      }

      try {
        MimeMessage msg = new MimeMessage(session);
        //set message headers for internationalized content
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.addHeader("format", "flowed");

        msg.setFrom(new InternetAddress(sender));
        msg.setSubject(subject, "UTF-8");
        msg.setText(content, "UTF-8");
        msg.setSentDate(new Date());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(compliantTo, false));
        Transport.send(msg);
      } catch (Exception e) {
        result.setError(String.format("Failed to send email to %s: %s", to, e));
      }
      return result;
    }

    private static void convertDomainToAlabel(String emailAddress, SmtpResult result) {
      String localPart = emailAddress.substring(0, emailAddress.lastIndexOf("@") - 1);
      String domain = emailAddress.substring(emailAddress.lastIndexOf("@") + 1);
      ConversionResult icuResult = ConvertDomain.WithIcu4j.toALabel(domain);
      // Email address is invalid as domain is invalid
      if (icuResult.hasError()) {
        result.setValid(false);
        result.setError("Email domain is invalid: " + icuResult.errors);
      } else {
        String converted = localPart + "@" + icuResult.domainConverted;
        result.setConverted(converted);
      }
    }
  }

  public static class SmtpResult {

    private final String to;
    private boolean valid = true;
    private String normalized = "";
    private String converted = "";
    private String error = "";
    private boolean emailDowngraded = false;

    public SmtpResult(String to) {
      this.to = to;
    }

    public String getTo() {
      return to;
    }

    public boolean isValid() {
      return valid;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public String getNormalized() {
      return normalized;
    }

    public void setNormalized(String normalized) {
      this.normalized = normalized;
    }

    public String getConverted() {
      return converted;
    }

    public void setConverted(String converted) {
      this.converted = converted;
    }

    public String getError() {
      return error;
    }

    public void setError(String error) {
      this.error = error;
    }

    public boolean isEmailDowngraded() {
      return emailDowngraded;
    }

    public void setEmailDowngraded(boolean emailDowngraded) {
      this.emailDowngraded = emailDowngraded;
    }
  }
}
