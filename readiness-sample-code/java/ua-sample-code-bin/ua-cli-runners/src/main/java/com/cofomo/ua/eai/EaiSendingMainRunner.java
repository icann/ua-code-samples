package com.cofomo.ua.eai;

import com.cofomo.ua.MainRunner;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class EaiSendingMainRunner extends MainRunner {

  protected String to;
  protected String host;
  protected int port;

  protected void parseArgs(String[] args) {
    // CLI options
    Options options = new Options();

    options.addOption("h", "help", false, "Print this messages and exit");
    options.addOption("t", "to", true, "The Email recipient");
    options.addOption("h", "host", true, "The SMTP server address");
    options.addOption("p", "port", true, "The SMTP server port");
    CommandLine cmd = checkArgs(args, options, List.of("to"));
    to = cmd.getOptionValue("to");
    host = cmd.getOptionValue("host", "localhost");
    port = Integer.parseInt(cmd.getOptionValue("port", "25"));
  }
}
