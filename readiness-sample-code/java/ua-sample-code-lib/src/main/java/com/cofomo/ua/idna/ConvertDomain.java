package com.cofomo.ua.idna;

import com.ibm.icu.text.IDNA;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConvertDomain {

  public static class WithIcu4j {

    // Initialize IDNA with the relevant flags to be the most IDNA 2008 compliant as possible
    private static final IDNA idnaInstance = IDNA.getUTS46Instance(IDNA.NONTRANSITIONAL_TO_ASCII
        | IDNA.CHECK_BIDI
        | IDNA.CHECK_CONTEXTJ
        | IDNA.CHECK_CONTEXTO
        | IDNA.USE_STD3_RULES);

    public static ConversionResult toALabel(String domain) {
      // Create an IDNA.Info object that would contain relevant error information if conversion
      // fails
      StringBuilder output = new StringBuilder();
      IDNA.Info info = new IDNA.Info();

      idnaInstance.nameToASCII(domain, output, info);

      String domainALabel = output.toString();
      if (!info.hasErrors()) {
        return new ConversionResult(domainALabel);
      } else {
        return new ConversionResult(null,
            info.getErrors().stream().map(Enum::toString).collect(Collectors.toSet()));
      }
    }

  }

  public static class ConversionResult {

    public String domainConverted;
    public Set<String> errors = new HashSet<>();

    public ConversionResult(String domainConverted) {
      this.domainConverted = domainConverted;
    }

    public ConversionResult(String domainConverted, Set<String> errors) {
      this.domainConverted = domainConverted;
      this.errors = errors;
    }

    public boolean hasError() {
      return !this.errors.isEmpty();
    }
  }
}
