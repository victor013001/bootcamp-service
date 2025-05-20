package com.pragma.challenge.bootcamp_service.domain.model;

import java.time.LocalDate;
import java.util.List;

public record Bootcamp(
    Long id,
    String name,
    String description,
    LocalDate launchDate,
    Integer durationInWeeks,
    List<Long> profileIds) {}
