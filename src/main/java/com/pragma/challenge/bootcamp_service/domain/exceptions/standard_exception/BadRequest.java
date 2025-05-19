package com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception;

import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardException;
import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;

public class BadRequest extends StandardException {
  public BadRequest() {
    super(ServerResponses.BAD_REQUEST);
  }
}
