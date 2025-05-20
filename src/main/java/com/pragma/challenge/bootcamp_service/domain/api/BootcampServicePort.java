package com.pragma.challenge.bootcamp_service.domain.api;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
  Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp);
}
