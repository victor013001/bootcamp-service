package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface BootcampHandler {
  Mono<ServerResponse> createBootcamp(ServerRequest request);

  Mono<ServerResponse> getBootcamps(ServerRequest request);
}
