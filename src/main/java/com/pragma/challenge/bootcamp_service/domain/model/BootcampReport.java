package com.pragma.challenge.bootcamp_service.domain.model;

import java.time.LocalDate;

public record BootcampReport(
    Long bootcampId,
    String name,
    String description,
    LocalDate launchDate,
    int durationInWeeks,
    int profileCount,
    int technologyCount,
    int userCount) {}
