package com.cofomo.ua.services;

import com.cofomo.ua.RegisterRequest;
import com.cofomo.ua.RegisterResponseFields.Field;
import com.cofomo.ua.idna.ConvertDomain;
import com.cofomo.ua.idna.ConvertDomain.ConversionResult;
import com.cofomo.ua.idna.ValidateDomain;
import com.cofomo.ua.idna.ValidateDomain.WithGuava.GuavaResult;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class IDNAService {

  private final String website;
  private final Map<String, String> libs;
  private final RegisterRequest body;
  private Field result;

  public IDNAService(String website, Map<String, String> libs, RegisterRequest body) {
    this.website = website;
    this.libs = libs;
    this.body = body;
    this.result = new Field(website);
  }

  public Field processWebsite() {
    if (libs.isEmpty()) {
      return Field.noLib(website);
    }

    if (!libs.getOrDefault("validate", "").isBlank()) {
      eaiValidate();
    }
    if (!result.isError() && !libs.getOrDefault("convert", "").isBlank()) {
      eaiSmtp();
    }

    return result;
  }

  private void eaiValidate() {
    String lib = libs.get("validate");

    if (lib.equals("commons-validator")) {
      commonsValidatorProcess();
    } else if (lib.equals("guava")) {
      guavaProcess();
    } else {
      this.result = Field.unknownLib(lib, "website");
    }
  }

  private void eaiSmtp() {
    String lib = libs.get("convert");
    if (!lib.equals("icu")) {
      this.result = Field.unknownLib(lib, "website");
    }
    icuProcess();
  }

  private void icuProcess() {
    try {
      URL url = new URL(website);
      ConversionResult conversionResult = ConvertDomain.WithIcu4j.toALabel(url.getHost());
      if (conversionResult.hasError()) {
        this.result.setError(true);
        this.result.addMessage(String.format("[icu] URL %s is invalid", website));
      } else {
        String websiteConverted = new URL(url.getProtocol(), conversionResult.domainConverted,
            url.getPort(), url.getFile()).toString();
        this.result.addMessage(String.format("[icu] The website domain name has been transformed "
                + "to A-Label to ensure IDNA 2008 compliant queries: <a href=\"%s\">%s</a>",
            websiteConverted, websiteConverted));
      }
    } catch (MalformedURLException e) {
      this.result.setError(true);
      this.result.addMessage(String.format("[java.net.URL] URL %s is invalid", website));
    }
  }

  private void commonsValidatorProcess() {
    result.addMessage("[Note] commons-validator uses a static list of TLDs which can be obsolete, "
        + "therefore we bypass the TLD validation.");
    if (ValidateDomain.WithCommonValidator.validateUrl(website)) {
      this.result.addMessage(String.format("[commons-validator] URL %s is valid", website));
    } else {
      this.result.setError(true);
      this.result.addMessage(String.format("[commons-validator] URL %s is invalid", website));
      result.addMessage("[Note] commons-validator does not provide any information about the "
          + "reason domain is invalid.");
    }
  }

  private void guavaProcess() {
    try {
      URL url = new URL(website);
      GuavaResult guavaResult = ValidateDomain.WithGuava.validate(url.getHost());
      result.addMessage("[Note] Guava does not check whether the website domain is valid as per"
          + "IDNA 2008, therefore icu4j was used here to check validity.");
      if (guavaResult.isValid) {
        if (guavaResult.isPublicSuffix) {
          result.addMessage(String.format("[guava] Website domain %s is valid and in the public "
              + "suffix list included in the library", url.getHost()));

        } else {
          result.addMessage(String.format("[guava] Domain %s is valid but is not in the public "
              + "suffix list included in the library", url.getHost()));
        }
        result.addMessage("[Note] The public suffix list may not be up to date, the only way "
            + "to check whether a domain exists is to make a DNS query, but it may not scale");
      } else {
        result.addMessage(String.format("Website domain %s is invalid", url.getHost()));
      }
    } catch (MalformedURLException e) {
      this.result.setError(true);
      this.result.addMessage(String.format("[java.net.URL] URL %s is invalid", website));
    }
  }
}