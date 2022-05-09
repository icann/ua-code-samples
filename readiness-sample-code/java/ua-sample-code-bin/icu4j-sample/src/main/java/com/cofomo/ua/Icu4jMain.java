package com.cofomo.ua;

import com.cofomo.ua.idna.ConvertDomain;
import com.cofomo.ua.idna.ConvertDomain.ConversionResult;
import com.cofomo.ua.idna.IdnaMainRunner;

public class Icu4jMain extends IdnaMainRunner {

  public Icu4jMain() {
    cliName = "icu4j-sample";
    domainHelp = "The domain name to convert in A-Label";
  }

  public static void main(String[] args) {
    new Icu4jMain().doMain(args);
  }

  @Override
  protected int run() {
    // convert domain
    ConversionResult result = ConvertDomain.WithIcu4j.toALabel(domain);
    if (result.hasError()) {
      System.err.printf("Domain '%s' is invalid: %s%n", domain, result.errors);
      return 1;
    }
    System.out.printf("Domain '%s' converted in A-Label is '%s'%n", domain, result.domainConverted);
    return 0;
  }
}
