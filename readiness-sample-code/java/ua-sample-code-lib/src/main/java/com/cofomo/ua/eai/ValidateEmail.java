package com.cofomo.ua.eai;


import static com.cofomo.ua.idna.ValidateDomain.WithCommonValidator.createDomainValidatorInstance;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidateEmail {

  public static class WithCommonValidator {

    public static boolean validateAddress(String email) {
      /*
       * Email validator relies on domain validator that uses a static list of TLD, therefore
       * we need to bypass this by providing our own DomainValidator instance
       */
      String domain = email.substring(email.lastIndexOf("@") + 1);
      EmailValidator validator = new EmailValidator(false, false,
          createDomainValidatorInstance(domain, false));
      return validator.isValid(email);
    }
  }
}
