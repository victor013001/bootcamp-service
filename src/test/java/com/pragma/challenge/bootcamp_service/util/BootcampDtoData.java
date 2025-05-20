package com.pragma.challenge.bootcamp_service.util;

import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;

import java.time.LocalDate;
import java.util.Set;

public class BootcampDtoData {
  private BootcampDtoData() throws InstantiationException {
    throw new InstantiationException("Data class cannot be instantiated");
  }

  public static BootcampDto getBootcampDto() {
    return new BootcampDto(
        "QA & Software Testing Bootcamp",
        "Hands-on training in manual and automated software testing.",
        LocalDate.now().plusDays(2),
        6,
        Set.of(3L, 4L));
  }

  public static BootcampDto getInvalidBootcampDto() {
    return new BootcampDto(
        "QA & Software Testing Bootcamp",
        "Hands-on training in manual and automated software testing.",
        LocalDate.now().minusDays(2),
        6,
        null);
  }
}
