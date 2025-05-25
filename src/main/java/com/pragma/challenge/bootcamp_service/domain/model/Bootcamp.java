package com.pragma.challenge.bootcamp_service.domain.model;

import com.pragma.challenge.bootcamp_service.domain.validation.annotation.NotNull;
import com.pragma.challenge.bootcamp_service.domain.validation.annotation.ValidList;
import java.time.LocalDate;
import java.util.List;

public record Bootcamp(
    Long id,
    @NotNull String name,
    @NotNull String description,
    @NotNull LocalDate launchDate,
    @NotNull Integer durationInWeeks,
    @ValidList(min = 1, max = 4) List<Long> profileIds) {}
