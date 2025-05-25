package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto;

import java.time.LocalDate;
import java.util.Set;

public record BootcampDto(
    String name,
    String description,
    LocalDate launchDate,
    Integer durationInWeeks,
    Set<Long> profileIds) {}
