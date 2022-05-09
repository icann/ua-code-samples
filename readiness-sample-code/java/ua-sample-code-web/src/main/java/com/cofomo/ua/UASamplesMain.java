package com.cofomo.ua;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.options;
import static spark.Spark.staticFiles;
import static spark.Spark.before;
import spark.Filter;

import com.cofomo.ua.utils.PropertiesUtil;
import java.util.Optional;

public class UASamplesMain {

  private static void enableCORS(final String origin, final String methods, final String headers) {

    options("/*", (request, response) -> {

      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    before((request, response) -> {
      response.header("Access-Control-Allow-Origin", origin);
      response.header("Access-Control-Request-Method", methods);
      response.header("Access-Control-Allow-Headers", headers);
      // Note: this may or may not be necessary in your particular application
      response.type("application/json");
    });
  }

  public static void main(String[] args) {
    // Configure Spark
    Optional<String> port = PropertiesUtil.getProperty("serverPort");
    port(Integer.parseInt(port.orElse("4567")));

    staticFiles.location("/static");
    staticFiles.expireTime(600L);

    enableCORS("*", "GET,PUT,POST,DELETE,OPTIONS", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");

    path("/ua", () -> {
      get("/libs", Api.getLibs);
      post("/register", Api.register);
    });
  }
}
