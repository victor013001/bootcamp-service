package com.pragma.challenge.bootcamp_service.infrastructure.adapters.profile_service.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkheadConfiguration {

  private final BulkheadRegistry bulkheadRegistry;

  public BulkheadConfiguration(BulkheadRegistry bulkheadRegistry) {
    this.bulkheadRegistry = bulkheadRegistry;
  }

  @Bean
  public Bulkhead profileServiceBulkhead() {
    return bulkheadRegistry.bulkhead("profileServiceBulkhead");
  }
}
