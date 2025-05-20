package com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.repository;

import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
  Flux<BootcampEntity> findAllBy(Pageable pageable);
}
