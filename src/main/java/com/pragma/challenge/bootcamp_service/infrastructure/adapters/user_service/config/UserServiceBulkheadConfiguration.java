package com.pragma.challenge.bootcamp_service.infrastructure.adapters.user_service.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceBulkheadConfiguration {

  private final BulkheadRegistry bulkheadRegistry;

  public UserServiceBulkheadConfiguration(BulkheadRegistry bulkheadRegistry) {
    this.bulkheadRegistry = bulkheadRegistry;
  }

  @Bean
  public Bulkhead userServiceBulkhead() {
    return bulkheadRegistry.bulkhead("userServiceBulkhead");
  }
}
