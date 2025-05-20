package com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.repository;

import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {}
