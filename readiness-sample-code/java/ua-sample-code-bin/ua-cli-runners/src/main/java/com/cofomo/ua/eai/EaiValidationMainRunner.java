package com.cofomo.ua.eai;

import com.cofomo.ua.MainRunner;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class EaiValidationMainRunner extends MainRunner {

  protected String emailAddress;

  protected void parseArgs(String[] args) {
    // CLI options
    Options options = new Options();

    options.addOption("h", "help", false, "Print this messages and exit");
    options.addOption("e", "email", true, "The email address to validate");
    CommandLine cmd = checkArgs(args, options, List.of("email"));
    emailAddress = cmd.getOptionValue("email");
  }
}
