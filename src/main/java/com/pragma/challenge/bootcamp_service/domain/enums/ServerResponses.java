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
  RESOURCE_NOT_FOUND("E002", HttpStatus.NOT_FOUND, Constants.RESOURCE_NOT_FOUND_MSG);

  private final String code;
  private final HttpStatus httpStatus;
  private final String message;
}
