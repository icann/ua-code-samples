package com.cofomo.ua;

import com.cofomo.ua.eai.EaiSendingMainRunner;
import com.cofomo.ua.eai.SendEmail;
import com.cofomo.ua.eai.SendEmail.SmtpResult;

public class JakartaMailMain extends EaiSendingMainRunner {

  public JakartaMailMain() {
    cliName = "jakarta-mail-sample";
  }

  public static void main(String[] args) {
    new JakartaMailMain().doMain(args);
  }

  @Override
  protected int run() {
    SmtpResult smtpResult = SendEmail.WithJakartaMail.send(to, host, port);
    if (smtpResult.getError().isBlank()) {
      System.out.printf("Email sent to '%s'%n", to);
      return 0;
    }
    System.err.printf("Error when sending email to '%s': %s%n", to, smtpResult.getError());
    return 1;
  }
}
