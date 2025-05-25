package com.pragma.challenge.bootcamp_service.infrastructure.adapters.user_service.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceRetryConfiguration {

  private final RetryRegistry retryRegistry;

  public UserServiceRetryConfiguration(RetryRegistry retryRegistry) {
    this.retryRegistry = retryRegistry;
  }

  @Bean
  public Retry userServiceRetryPolicy() {
    return retryRegistry.retry("userServiceRetry");
  }
}
