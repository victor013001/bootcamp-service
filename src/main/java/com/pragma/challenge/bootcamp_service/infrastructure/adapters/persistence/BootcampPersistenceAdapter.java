package com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import com.pragma.challenge.bootcamp_service.domain.spi.BootcampPersistencePort;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements BootcampPersistencePort {
  private static final String LOG_PREFIX = "[BOOTCAMP_PERSISTENCE_ADAPTER] >>>";

  private final BootcampRepository bootcampRepository;
  private final BootcampEntityMapper bootcampEntityMapper;
  private final TransactionalOperator transactionalOperator;

  @Override
  public Mono<Bootcamp> save(Bootcamp bootcamp) {
    log.info(
        "{} Saving bootcamp with name: {}, description: {}, launch date: {}, duration in weeks: {}.",
        LOG_PREFIX,
        bootcamp.name(),
        bootcamp.description(),
        bootcamp.launchDate(),
        bootcamp.durationInWeeks());
    return bootcampRepository
        .save(bootcampEntityMapper.toEntity(bootcamp))
        .map(bootcampEntityMapper::toModel);
  }

  @Override
  public Flux<BootcampProfile> findAllBy(PageRequest pageRequest) {
    return bootcampRepository.findAllBy(pageRequest).map(bootcampEntityMapper::toBootcampProfile);
  }

  @Override
  public Mono<Boolean> existsById(Long bootcampId) {
    log.info("{} Checking id bootcamp with id: {} exists.", LOG_PREFIX, bootcampId);
    return bootcampRepository.existsById(bootcampId);
  }

  @Override
  public Mono<BootcampProfile> getBootcampById(Long bootcampId) {
    log.info("{} Finding bootcamp by id: {}", LOG_PREFIX, bootcampId);
    return bootcampRepository.findById(bootcampId).map(bootcampEntityMapper::toBootcampProfile);
  }

  @Override
  public Mono<Void> delete(Long bootcampId) {
    log.info("{} Deleting bootcamp with id: {}", LOG_PREFIX, bootcampId);
    return bootcampRepository.deleteById(bootcampId);
  }
}
