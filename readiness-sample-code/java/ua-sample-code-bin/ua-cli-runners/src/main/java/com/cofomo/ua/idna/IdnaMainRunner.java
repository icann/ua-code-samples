package com.cofomo.ua.idna;

import com.cofomo.ua.MainRunner;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class IdnaMainRunner extends MainRunner {

  protected String domain;
  protected String domainHelp = "The domain name";

  protected void parseArgs(String[] args) {
    // CLI options
    Options options = new Options();

    options.addOption("h", "help", false, "Print this messages and exit");
    options.addOption("d", "domain", true, domainHelp);
    CommandLine cmd = checkArgs(args, options, List.of("domain"));
    domain = cmd.getOptionValue("domain");
  }
}
