package com.cofomo.ua;

import com.cofomo.ua.RegisterResponseFields.Field;
import com.cofomo.ua.services.EAIService;
import com.cofomo.ua.services.IDNAService;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class Api {

  private static final Gson gson = new Gson();

  public static Route getLibs = (Request request, Response response) -> {
    response.type("application/json");
    Map<String, Object> jsonResponse = new HashMap<>();
    Map<String, List<String>> idnJson = new HashMap<>();
    Map<String, List<String>> eaiJson = new HashMap<>();
    idnJson.put("validate", List.of("commons-validator", "guava"));
    idnJson.put("convert", List.of("icu"));
    eaiJson.put("validate", List.of("commons-validator"));
    eaiJson.put("smtp", List.of("jakarta-mail"));
    jsonResponse.put("idn", idnJson);
    jsonResponse.put("eai", eaiJson);
    return gson.toJson(jsonResponse);
  };

  public static Route register = (Request request, Response response) -> {
    response.type("application/json");
    RegisterRequest body = gson.fromJson(request.body(), RegisterRequest.class);

    if (!body.missingFields().isEmpty()) {
      return gson.toJson(Map.of("error",
          "The following fields are required: " + String.join(", ", body.missingFields())));
    }

    IDNAService idnaService = new IDNAService(body.getWebsite(),
        body.getLibs().getOrDefault("idna", new HashMap<>()), body);
    EAIService eaiService = new EAIService(body.getEmail(),
        body.getLibs().getOrDefault("eai", new HashMap<>()), body);

    Field websiteField = idnaService.processWebsite();
    Field emailField = eaiService.processEmail();
    RegisterResponseFields responseFields = new RegisterResponseFields(websiteField, emailField);

    return gson.toJson(Map.of("field", responseFields));
  };

}