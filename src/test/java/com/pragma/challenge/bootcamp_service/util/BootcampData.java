package com.pragma.challenge.bootcamp_service.util;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import java.time.LocalDate;
import java.util.List;

public class BootcampData {
  private BootcampData() throws InstantiationException {
    throw new InstantiationException("Data class cannot be instantiated");
  }

  public static Bootcamp getBootcamp() {
    return new Bootcamp(
        null,
        "QA & Software Testing Bootcamp",
        "Hands-on training in manual and automated software testing.",
        LocalDate.now().plusDays(2),
        6,
        List.of(3L, 4L));
  }
}
