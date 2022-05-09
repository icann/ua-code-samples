package com.cofomo.ua.idna;

import static org.apache.commons.validator.routines.DomainValidator.ArrayType.GENERIC_PLUS;

import com.cofomo.ua.idna.ConvertDomain.ConversionResult;
import com.google.common.net.InternetDomainName;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.DomainValidator.Item;
import org.apache.commons.validator.routines.UrlValidator;

public class ValidateDomain {

  public static class WithCommonValidator {

    private static final String IANA_TLD_LIST_URL = "https://data.iana.org/TLD/tlds-alpha-by-domain.txt";

    /**
     * Download the list of TLDs on ICANN website
     */
    public static String[] retrieveTlds() {
      StringBuilder out = new StringBuilder();
      try (BufferedInputStream in = new BufferedInputStream(
          new URL(IANA_TLD_LIST_URL).openStream())) {
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
          out.append(new String(dataBuffer, 0, bytesRead));
        }
      } catch (IOException e) {
        // handle exception
      }
      return Arrays.stream(out.toString().split("\n"))
          .filter(s -> !s.startsWith("#"))
          .map(String::toLowerCase).distinct().toArray(String[]::new);
    }

    /**
     * Domain validator uses a static list of TLDs that is shipped with the library and is therefore
     * likely to be obsolete as the TLD list is updated regularly. To bypass this problem, we
     * provide the current TLD to the DomainValidator instance. We also provide an option to
     * download the actual list of TLDs to be used, but as this is time-consuming, it is recommended
     * to use cache mechanisms in production. NB: We don't bother to use the actual TLD types in
     * Item.
     */
    public static DomainValidator createDomainValidatorInstance(String domain,
        boolean use_actual_domains) {
      List<Item> domains = new ArrayList<>();
      if (use_actual_domains) {
        domains.add(new Item(GENERIC_PLUS, retrieveTlds()));
      } else {
        String tld = domain;
        if (domain.contains(".")) {
          tld = domain.substring(domain.lastIndexOf(".") + 1);
        }
        // Convert TLD to A-Label
        ConversionResult result = ConvertDomain.WithIcu4j.toALabel(tld);
        // if there is an error, do nothing, validator will fail
        if (!result.hasError()) {
          domains.add(new Item(GENERIC_PLUS, new String[]{result.domainConverted}));
        }
      }

      return DomainValidator.getInstance(false, domains);
    }

    public static boolean validate(String domain) {
      DomainValidator validator = createDomainValidatorInstance(domain, false);
      return validator.isValid(domain);
    }

    public static boolean validateUrl(String url) {
      String domain;
      try {
        domain = new URL(url).getHost();
      } catch (MalformedURLException e) {
        return false;
      }
      UrlValidator urlValidator = new UrlValidator(null, null, 0L,
          createDomainValidatorInstance(domain, false));
      return urlValidator.isValid(url);
    }
  }


  public static class WithGuava {

    public static class GuavaResult {

      public boolean isValid;
      public boolean isPublicSuffix = false;

      public GuavaResult(boolean isValid) {
        this.isValid = isValid;
      }

      public GuavaResult(boolean isValid, boolean isPublicSuffix) {
        this.isValid = isValid;
        this.isPublicSuffix = isPublicSuffix;
      }
    }

    public static GuavaResult validate(String domain) {
      // Guava does not perform an extensive validation, for example INVALID Unicode characters are
      // accepted, therefore, we need to validate the domain with something else
      // Our most reliable way to validate a domain is through its conversion to A-Label with ICU
      ConversionResult icuResult = ConvertDomain.WithIcu4j.toALabel(domain);
      if (icuResult.hasError()) {
        return new GuavaResult(false);
      }

      // In order to get some benefit from guava, we check the domain in the public suffix, but again
      // this is not very reliable as the public suffix is only a snapshot of something changing.
      try {
        InternetDomainName internetDomainName = InternetDomainName.from(domain);
        return new GuavaResult(true, internetDomainName.isPublicSuffix());
      } catch (IllegalArgumentException e) {
        return new GuavaResult(false);
      }
    }
  }


}
