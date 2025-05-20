package com.pragma.challenge.bootcamp_service.domain.model;

import java.util.List;

public record ProfileTechnology(
    Long id, String name, String description, List<TechnologyNoDescription> technologies) {}
