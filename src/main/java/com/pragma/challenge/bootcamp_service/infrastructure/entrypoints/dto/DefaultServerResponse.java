package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto;

public record DefaultServerResponse<T, E>(T data, E error) {}
