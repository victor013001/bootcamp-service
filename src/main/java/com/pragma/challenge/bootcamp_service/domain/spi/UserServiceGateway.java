package com.pragma.challenge.bootcamp_service.domain.spi;

import reactor.core.publisher.Mono;

public interface UserServiceGateway {
  Mono<Long> getBootcampWithHigherNumberUsers();
}
