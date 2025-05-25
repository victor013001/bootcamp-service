package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
  Mono<Bootcamp> save(Bootcamp bootcamp);

  Flux<BootcampProfile> findAllBy(PageRequest pageRequest);

  Mono<Void> delete(Long bootcampId);

  Mono<Boolean> existsById(Long bootcampId);

  Mono<BootcampProfile> getBootcampById(Long bootcampId);
}
