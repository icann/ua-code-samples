package com.cofomo.ua.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PropertiesUtil {

  private static final String PROPERTIES_FILE = "config.properties";
  private static final Map<String, String> PROPERTIES = readProperties();

  public static Map<String, String> readProperties() {
    Properties prop = new Properties();
    Map<String, String> properties = new HashMap<>();

    try (InputStream inputStream = PropertiesUtil.class.getClassLoader()
        .getResourceAsStream(PROPERTIES_FILE)) {
      prop.load(inputStream);
      for (String propName : List.of("serverPort", "smtpServer", "smtpPort", "mailhog")) {
        if (System.getProperty(propName) != null) {
          // first read command line properties
          properties.put(propName, System.getProperty(propName));
        } else if (prop.get(propName) != null) {
          // then read configuration file properties
          properties.put(propName, prop.getProperty(propName));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return properties;
  }

  public static Optional<String> getProperty(String name) {
    return Optional.ofNullable(PROPERTIES.get(name));
  }
}
