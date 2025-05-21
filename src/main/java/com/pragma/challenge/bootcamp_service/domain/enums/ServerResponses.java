package com.pragma.challenge.bootcamp_service.domain.enums;

import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServerResponses {
  BAD_REQUEST("E000", HttpStatus.BAD_REQUEST, Constants.BAD_REQUEST_MSG),
  SERVER_ERROR("E001", HttpStatus.INTERNAL_SERVER_ERROR, Constants.SERVER_ERROR_MSG),
  RESOURCE_NOT_FOUND("E002", HttpStatus.NOT_FOUND, Constants.RESOURCE_NOT_FOUND_MSG),
  BOOTCAMP_CREATED("", HttpStatus.CREATED, Constants.BOOTCAMP_CREATED_MSG),
  PROFILE_NOT_FOUND("E003", HttpStatus.NOT_FOUND, Constants.PROFILES_NOT_FOUND_MSG),
  GATEWAY_ERROR("E004", HttpStatus.INTERNAL_SERVER_ERROR, Constants.GATEWAY_ERROR_MSG),
  GATEWAY_BAD_REQUEST("E005", HttpStatus.BAD_REQUEST, Constants.GATEWAY_BAD_REQUEST_MSG),
  BOOTCAMP_DELETED("", HttpStatus.OK, Constants.BOOTCAMP_DELETED_MSG);

  private final String code;
  private final HttpStatus httpStatus;
  private final String message;
}
