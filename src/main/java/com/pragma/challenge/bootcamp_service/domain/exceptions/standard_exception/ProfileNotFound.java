package com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception;

import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;
import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardException;

public class ProfileNotFound extends StandardException {
  public ProfileNotFound() {
    super(ServerResponses.PROFILE_NOT_FOUND);
  }
}
