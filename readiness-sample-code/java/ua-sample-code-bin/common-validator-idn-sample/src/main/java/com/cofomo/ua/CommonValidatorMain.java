package com.cofomo.ua;

import com.cofomo.ua.idna.IdnaMainRunner;
import com.cofomo.ua.idna.ValidateDomain;

public class CommonValidatorMain extends IdnaMainRunner {

  public CommonValidatorMain() {
    cliName = "common-validator-idn-sample";
    domainHelp = "The domain name to validate";
  }

  public static void main(String[] args) {
    new CommonValidatorMain().doMain(args);
  }

  @Override
  protected int run() {
    if (ValidateDomain.WithCommonValidator.validate(domain)) {
      System.out.printf("Domain '%s' is valid%n", domain);
      return 0;
    }
    System.err.printf("Domain '%s' is invalid%n", domain);
    return 1;
  }
}
