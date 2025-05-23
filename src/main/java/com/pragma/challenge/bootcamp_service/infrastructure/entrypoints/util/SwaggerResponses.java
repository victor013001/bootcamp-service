package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util;

import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public final class SwaggerResponses {

  private SwaggerResponses() throws InstantiationException {
    throw new InstantiationException("Utility class");
  }

  @Data
  @Schema(name = "DefaultMessageResponse")
  @AllArgsConstructor
  public static class DefaultMessageResponse {
    private String data;
  }

  @Data
  @Schema(name = "DefaultBootcampProfileResponse")
  @AllArgsConstructor
  public static class DefaultBootcampProfileResponse {
    private BootcampProfile data;
  }

  @Data
  @Schema(name = "DefaultBooleanResponse")
  @AllArgsConstructor
  public static class DefaultBooleanResponse {
    private Boolean data;
  }
}
