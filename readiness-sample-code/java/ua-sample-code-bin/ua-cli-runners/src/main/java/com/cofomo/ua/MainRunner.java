package com.cofomo.ua;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class MainRunner {

  protected String cliName = "app";

  protected CommandLine checkArgs(String[] args, Options options, List<String> mandatoryArgs) {
    CommandLine cmd = null;
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println("Wrong argument: " + e.getMessage());
      formatter.printHelp(cliName, options);
      System.exit(1);
    }
    if (cmd.hasOption("help")) {
      formatter.printHelp(cliName, options);
      System.exit(0);
    }
    for (String arg : mandatoryArgs) {
      if (!cmd.hasOption(arg)) {
        formatter.printHelp(cliName, options);
        System.exit(1);
      }
    }

    return cmd;
  }

  protected void doMain(String[] args) {
    parseArgs(args);
    System.exit(run());
  }

  protected abstract void parseArgs(String[] args);


  protected abstract int run();
}
