package com.cofomo.ua.services;

import com.cofomo.ua.RegisterRequest;
import com.cofomo.ua.RegisterResponseFields.Field;
import com.cofomo.ua.eai.SendEmail;
import com.cofomo.ua.eai.SendEmail.SmtpResult;
import com.cofomo.ua.eai.ValidateEmail;
import com.cofomo.ua.utils.PropertiesUtil;
import java.util.Map;

public class EAIService {

  private final String email;
  private final Map<String, String> libs;
  private final RegisterRequest body;
  private Field result;

  public EAIService(String email, Map<String, String> libs, RegisterRequest body) {
    this.email = email;
    this.libs = libs;
    this.body = body;
    this.result = new Field(email);
  }

  public Field processEmail() {
    if (libs.isEmpty()) {
      return Field.noLib(email);
    }

    if (!libs.getOrDefault("validate", "").isBlank()) {
      eaiValidate();
    }
    if (!result.isError() && !libs.getOrDefault("smtp", "").isBlank()) {
      eaiSmtp();
    }

    return result;
  }

  private void eaiValidate() {
    String lib = libs.get("validate");
    if (!lib.equals("commons-validator")) {
      this.result = Field.unknownLib(lib, "email");
    }
    commonsValidatorProcess(email);
  }

  private void eaiSmtp() {
    String lib = libs.get("smtp");
    if (!lib.equals("jakarta-mail")) {
      this.result = Field.unknownLib(lib, "email");
    }
    jakartaMailProcess();
  }


  private void commonsValidatorProcess(String email) {
    if (ValidateEmail.WithCommonValidator.validateAddress(email)) {
      this.result.addMessage(String.format("[commons-validator] Email address %s is valid", email));
    } else {
      this.result.setError(true);
      this.result.addMessage(
          String.format("[commons-validator] Email address %s is invalid", email));
    }
  }

  private void jakartaMailProcess() {
    SmtpResult smtpResult = SendEmail.WithJakartaMail.send(email,
        PropertiesUtil.getProperty("smtpServer").orElse("localhost"),
        Integer.parseInt(PropertiesUtil.getProperty("smtpPort").orElse("25")), null,
        "Registration successful", body.toEmailBody());
    if (smtpResult.getError().isBlank()) {
      if (smtpResult.isValid()) {
        result.addMessage("[jakarta-mail] Email address is valid");
      }
      if (!smtpResult.getNormalized().isBlank()) {
        result.addMessage("[Note] Email address domain has been normalized else jakarta-mail will "
            + "reject it. It is recommended to work with a fully normalized email address when "
            + "this email address is stored and queried. Normalized email address is "
            + smtpResult.getNormalized());
      }
      if (!smtpResult.getConverted().isBlank()) {
        if (!smtpResult.isEmailDowngraded()) {
          result.addMessage("[Note] Jakarta mail validation considers some non-ASCII valid emails"
              + "as invalid, therefore email domain has been converted to A-Label with ICU4j. "
              + "Email will be sent to " + smtpResult.getConverted());
        } else {
          result.addMessage("[jakarta-mail] Server did not support SMTPUTF8 options. As local-part "
              + "is plain ascii, the domain was converted to A-Label and the email sent to "
              + smtpResult.getConverted());
        }
      }
      String message = "[jakarta-mail] Email has been sent successfully";
      if (PropertiesUtil.getProperty("mailhog").isPresent()) {
        message += String.format(", see it <a href=\"%s\" target=\"_blank\">here</a>!",
            PropertiesUtil.getProperty("mailhog").get());
      }
      result.addMessage(message);
    } else {
      result.setError(true);
      result.addMessage(
          String.format("[jakarta-mail] Cannot send email to %s: %s", email,
              smtpResult.getError()));
    }
  }
}