package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record BootcampDto(
    @NotBlank(message = "Name is mandatory")
        @Size(max = 50, message = "Name exceeds the permitted limit")
        String name,
    @NotBlank(message = "Description is mandatory")
        @Size(max = 90, message = "Description exceeds the permitted limit")
        String description,
    @NotNull(message = "Launch date is mandatory")
        @Future(message = "Launch date cannot be in the past")
        LocalDate launchDate,
    @NotNull(message = "Duration is mandatory")
        @Min(value = 1, message = "Duration must be at least 1 week")
        Integer durationInWeeks,
    @NotNull(message = "Technologies are mandatory")
        @Size(
            max = 4,
            min = 1,
            message = "A minimum of 1 and a maximum of 4 profiles must be specified.")
        Set<Long> profileIds) {}
