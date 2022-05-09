package com.cofomo.ua;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterRequest {

  private final Map<String, Map<String, String>> libs;
  private final String username;
  private final String email;
  private final String website;

  public RegisterRequest(
      Map<String, Map<String, String>> libs, String username, String email, String website) {
    this.libs = libs;
    this.username = username;
    this.email = email;
    this.website = website;
  }

  public Map<String, Map<String, String>> getLibs() {
    return libs;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getWebsite() {
    return website;
  }

  public List<String> missingFields() {
    List<String> missing = new ArrayList<>();
    if (username.isBlank()) {
      missing.add("username");
    }
    if (email.isBlank()) {
      missing.add("email");
    }
    if (website.isBlank()) {
      missing.add("website");
    }
    return missing;
  }

  public String toEmailBody() {
    return String.format("Dear %s,\n"
        + "Your registration is successful.\n"
        + "Your information:\n"
        + " - username: %s\n"
        + " - email: %s\n"
        + " - website: %s\n"
        + "Best regards,\n"
        + "The UA Sample team", username, username, email, website);
  }
}
