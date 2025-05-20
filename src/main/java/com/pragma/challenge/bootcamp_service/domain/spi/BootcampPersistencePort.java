package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
  Mono<Bootcamp> save(Bootcamp bootcamp);
}
