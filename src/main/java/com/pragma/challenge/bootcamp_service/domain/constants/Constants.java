package com.pragma.challenge.bootcamp_service.domain.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
  public static final String BAD_REQUEST_MSG =
      "The request could not be processed due to invalid or incomplete data.";
  public static final String SERVER_ERROR_MSG =
      "An unexpected server error occurred. Please try again later.";
  public static final String RESOURCE_NOT_FOUND_MSG = "The requested resource was not found.";
  public static final String BOOTCAMP_CREATED_MSG = "The bootcamp was created successfully.";
  public static final String PROFILES_NOT_FOUND_MSG = "The provided profiles were not found.";
  public static final String ID_PARAM = "id";
  public static final String GATEWAY_ERROR_MSG =
      "Failed to process the request due to an internal gateway error.";
  public static final String GATEWAY_BAD_REQUEST_MSG =
      "An unexpected error occurred while processing the request through the gateway.";
}
