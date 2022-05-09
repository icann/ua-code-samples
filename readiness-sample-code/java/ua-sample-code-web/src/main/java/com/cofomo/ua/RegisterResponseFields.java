package com.cofomo.ua;

import java.util.ArrayList;
import java.util.List;

public class RegisterResponseFields {

  private final Field website;
  private final Field email;

  public RegisterResponseFields(Field website, Field email) {
    this.website = website;
    this.email = email;
  }

  public Field getWebsite() {
    return website;
  }

  public Field getEmail() {
    return email;
  }

  public static class Field {

    private String value;
    private List<String> messages = new ArrayList<>();
    private boolean error;

    public static Field noLib(String fieldName) {
      return new Field(fieldName,
          "Field has not been validated as no lib was specified",
          false);
    }

    public static Field unknownLib(String lib, String fieldName) {
      return new Field(fieldName,
          String.format("Unknown library '%s' for %s validation", lib, fieldName),
          false);
    }

    public Field(String value, String message, boolean error) {
      this.value = value;
      this.messages.add(message);
      this.error = error;
    }

    public Field(String value) {
      this.value = value;
    }

    public boolean isError() {
      return error;
    }

    public void setError(boolean error) {
      this.error = error;
    }

    public List<String> getMessages() {
      return messages;
    }

    public void setMessages(List<String> messages) {
      this.messages = messages;
    }

    public void addMessage(String message) {
      this.messages.add(message);
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }


  }


}
