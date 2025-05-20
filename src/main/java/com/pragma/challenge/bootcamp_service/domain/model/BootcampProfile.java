package com.pragma.challenge.bootcamp_service.domain.model;

import java.util.List;

public record BootcampProfile(
    Long id, String name, String description, List<ProfileTechnology> profiles) {}
