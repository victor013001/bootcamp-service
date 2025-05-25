package com.pragma.challenge.bootcamp_service.infrastructure.adapters.report_service.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportServiceBulkheadConfiguration {

  private final BulkheadRegistry bulkheadRegistry;

  public ReportServiceBulkheadConfiguration(BulkheadRegistry bulkheadRegistry) {
    this.bulkheadRegistry = bulkheadRegistry;
  }

  @Bean
  public Bulkhead reportServiceBulkhead() {
    return bulkheadRegistry.bulkhead("reportServiceBulkhead");
  }
}
