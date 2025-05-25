package com.pragma.challenge.bootcamp_service.application.config;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.mapper.BootcampProfileMapper;
import com.pragma.challenge.bootcamp_service.domain.spi.BootcampPersistencePort;
import com.pragma.challenge.bootcamp_service.domain.spi.ProfileServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.spi.ReportServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.spi.UserServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.usecase.BootcampUseCase;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.BootcampPersistenceAdapter;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

  private final BootcampRepository bootcampRepository;
  private final BootcampEntityMapper bootcampEntityMapper;
  private final TransactionalOperator transactionalOperator;

  @Bean
  public BootcampServicePort bootcampServicePort(
      BootcampPersistencePort bootcampPersistencePort,
      ProfileServiceGateway profileServiceGateway,
      BootcampProfileMapper bootcampProfileMapper,
      ReportServiceGateway reportServiceGateway,
      UserServiceGateway userServiceGateway) {
    return new BootcampUseCase(
        bootcampPersistencePort,
        profileServiceGateway,
        bootcampProfileMapper,
        reportServiceGateway,
        userServiceGateway);
  }

  @Bean
  public BootcampPersistencePort bootcampPersistencePort() {
    return new BootcampPersistenceAdapter(
        bootcampRepository, bootcampEntityMapper, transactionalOperator);
  }
}
