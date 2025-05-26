package com.pragma.challenge.bootcamp_service.infrastructure.adapters.report_service;

import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardError;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayBadRequest;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.GatewayError;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampReport;
import com.pragma.challenge.bootcamp_service.domain.spi.ReportServiceGateway;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.DefaultServerResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReportServiceAdapter implements ReportServiceGateway {
  private static final String LOG_PREFIX = "[REPORT_SERVICE_GATEWAY] >>>";

  private final String BASE_PATH = "api/v1/report";

  private final WebClient webClient;
  private final Retry retry;
  private final Bulkhead bulkhead;

  public ReportServiceAdapter(
      @Qualifier("reportServiceWebClient") WebClient webClient,
      @Qualifier("reportServiceRetryPolicy") Retry retry,
      @Qualifier("reportServiceBulkhead") Bulkhead bulkhead) {
    this.webClient = webClient;
    this.retry = retry;
    this.bulkhead = bulkhead;
  }

  @Override
  @CircuitBreaker(name = "reportService", fallbackMethod = "fallback")
  public Mono<Void> registerBootcampReport(BootcampReport bootcampReport) {
    log.info(
        "{} Starting report creation for bootcamp id: {} in Report Service.",
        LOG_PREFIX,
        bootcampReport.bootcampId());
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(BASE_PATH + "/bootcamp").build())
        .bodyValue(bootcampReport)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(GatewayBadRequest::new))
        .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(GatewayError::new))
        .bodyToMono(
            new ParameterizedTypeReference<DefaultServerResponse<String, StandardError>>() {})
        .map(DefaultServerResponse::data)
        .doOnNext(exists -> log.info("{} Received Report Service response.", LOG_PREFIX))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
        .doOnTerminate(
            () -> log.info("{} Completed report creation process in Report Service.", LOG_PREFIX))
        .doOnError(
            ignore ->
                log.error(
                    "{} Error calling Report Service with request: {}", LOG_PREFIX, bootcampReport))
        .then();
  }

  public Mono<Boolean> fallback(Throwable t) {
    log.warn("{} Fallback triggered for Report Service", LOG_PREFIX);
    return Mono.defer(() -> Mono.justOrEmpty(t instanceof TimeoutException ? Boolean.FALSE : null))
        .switchIfEmpty(Mono.error(t));
  }
}
