package com.pragma.challenge.bootcamp_service.infrastructure.adapters.report_service.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportServiceRetryConfiguration {

  private final RetryRegistry retryRegistry;

  public ReportServiceRetryConfiguration(RetryRegistry retryRegistry) {
    this.retryRegistry = retryRegistry;
  }

  @Bean
  public Retry reportServiceRetryPolicy() {
    return retryRegistry.retry("reportServiceRetry");
  }
}
