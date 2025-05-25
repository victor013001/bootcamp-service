package com.pragma.challenge.bootcamp_service.infrastructure.adapters.user_service;

import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardError;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayBadRequest;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayError;
import com.pragma.challenge.bootcamp_service.domain.spi.UserServiceGateway;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.DefaultServerResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserServiceAdapter implements UserServiceGateway {
  private static final String LOG_PREFIX = "[USER_SERVICE_GATEWAY] >>>";

  private final String BASE_PATH = "api/v1/user";

  private final WebClient webClient;
  private final Retry retry;
  private final Bulkhead bulkhead;

  public UserServiceAdapter(
      @Qualifier("userServiceWebClient") WebClient webClient,
      @Qualifier("userServiceRetryPolicy") Retry retry,
      @Qualifier("userServiceBulkhead") Bulkhead bulkhead) {
    this.webClient = webClient;
    this.retry = retry;
    this.bulkhead = bulkhead;
  }

  @Override
  public Mono<Long> getBootcampWithHigherNumberUsers() {
    log.info(
        "{} Starting get bootcamp id with higher number of users process in User Service.",
        LOG_PREFIX);
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(BASE_PATH + "/bootcamp").build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(new ParameterizedTypeReference<DefaultServerResponse<Long, StandardError>>() {})
        .map(DefaultServerResponse::data)
        .doOnNext(exists -> log.info("{} Received User Service response.", LOG_PREFIX))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () -> log.info("{} Completed get bootcamp id process in User Service.", LOG_PREFIX))
        .doOnError(ignore -> log.error("{} Error calling User Service", LOG_PREFIX));
  }
}
