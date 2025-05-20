package com.pragma.challenge.bootcamp_service.infrastructure.adapters.profile_service;

import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayBadRequest;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayError;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import com.pragma.challenge.bootcamp_service.domain.model.ProfileTechnology;
import com.pragma.challenge.bootcamp_service.domain.spi.ProfileServiceGateway;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.DefaultServerResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceAdapter implements ProfileServiceGateway {
  private static final String LOG_PREFIX = "[PROFILE_SERVICE_GATEWAY] >>>";

  private final String BASE_PATH = "api/v1/profile";

  private final WebClient webClient;
  private final Retry retry;
  private final Bulkhead bulkhead;

  @Override
  @CircuitBreaker(name = "profileService", fallbackMethod = "fallback")
  public Mono<Boolean> profilesExists(List<Long> profileIds) {
    log.info(
        "{} Starting profiles validation process for ids: {} in Profile Service.",
        LOG_PREFIX,
        profileIds);
    return webClient
        .get()
        .uri(
            uriBuilder -> {
              UriBuilder builder = uriBuilder.path(BASE_PATH + "/exists");
              profileIds.forEach(id -> builder.queryParam(Constants.ID_PARAM, id));
              return builder.build();
            })
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(new ParameterizedTypeReference<DefaultServerResponse<Boolean>>() {})
        .map(DefaultServerResponse::data)
        .doOnNext(exists -> log.info("{} Received Profile Service response.", LOG_PREFIX))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () ->
                log.info(
                    "{} Completed profiles validation process in Profiles Service.", LOG_PREFIX))
        .doOnError(
            ignore ->
                log.error("{} Error calling Profile Service with ids: {}", LOG_PREFIX, profileIds));
  }

  @Override
  @CircuitBreaker(name = "profileService", fallbackMethod = "fallback")
  public Mono<Void> createRelation(BootcampProfiles bootcampProfiles) {
    log.info("{} Starting bootcamp and profiles relation process in Profile Service.", LOG_PREFIX);
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(BASE_PATH + "/bootcamp").build())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(bootcampProfiles)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(new ParameterizedTypeReference<DefaultServerResponse<String>>() {})
        .map(DefaultServerResponse::data)
        .doOnNext(
            exists -> log.info("{} Received Profile Service response: {}", LOG_PREFIX, exists))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () ->
                log.info(
                    "{} Completed bootcamp and profiles relation process in Profile Service.",
                    LOG_PREFIX))
        .doOnError(
            ignore ->
                log.error("{} Error creating the relation for: {}.", LOG_PREFIX, bootcampProfiles))
        .then();
  }

  @Override
  @CircuitBreaker(name = "profileService", fallbackMethod = "fallback")
  public Mono<List<ProfileTechnology>> getProfiles(Long bootcampId) {
    log.info(
        "{} Starting find profiles for bootcamp id: {} process in Profile Service.",
        LOG_PREFIX,
        bootcampId);
    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(BASE_PATH)
                    .queryParam(Constants.BOOTCAMP_ID_PARAM, bootcampId)
                    .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(
            new ParameterizedTypeReference<DefaultServerResponse<List<ProfileTechnology>>>() {})
        .map(DefaultServerResponse::data)
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () ->
                log.info(
                    "{} Completed find profiles for bootcamp id: {} process in Profile Service.",
                    LOG_PREFIX,
                    bootcampId))
        .doOnError(
            error ->
                log.error(
                    "{} Error finding the profiles for bootcamp id: {}", LOG_PREFIX, bootcampId));
  }

  @Override
  @CircuitBreaker(name = "profileService", fallbackMethod = "fallback")
  public Mono<Void> deleteBootcampProfiles(Long bootcampId) {
    log.info(
        "{} Starting delete profiles for bootcamp id: {} process in Profile Service.",
        LOG_PREFIX,
        bootcampId);
    return webClient
        .delete()
        .uri(BASE_PATH + "/{id}", bootcampId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(new ParameterizedTypeReference<DefaultServerResponse<String>>() {})
        .map(DefaultServerResponse::data)
        .doOnNext(
            exists -> log.info("{} Received Profile Service response: {}", LOG_PREFIX, exists))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () ->
                log.info(
                    "{} Completed delete profiles for bootcamp process in Profile Service.",
                    LOG_PREFIX))
        .doOnError(
            ignore ->
                log.error(
                    "{} Error deleting the profiles for bootcamp id: {}.", LOG_PREFIX, bootcampId))
        .then();
  }

  public Mono<Boolean> fallback(Throwable t) {
    log.warn("{} Fallback triggered for Profile Service", LOG_PREFIX);
    return Mono.defer(() -> Mono.justOrEmpty(t instanceof TimeoutException ? Boolean.FALSE : null))
        .switchIfEmpty(Mono.error(t));
  }
}
