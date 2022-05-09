package com.cofomo.ua;

import com.cofomo.ua.eai.EaiValidationMainRunner;
import com.cofomo.ua.eai.ValidateEmail;

public class CommonValidatorMain extends EaiValidationMainRunner {

  public CommonValidatorMain() {
    cliName = "common-validator-eai-sample";
  }

  public static void main(String[] args) {
    new CommonValidatorMain().doMain(args);
  }

  @Override
  protected int run() {
    if (ValidateEmail.WithCommonValidator.validateAddress(emailAddress)) {
      System.out.printf("Email address '%s' is valid%n", emailAddress);
      return 0;
    }
    System.err.printf("Email address '%s' is invalid%n", emailAddress);
    return 1;
  }
}
