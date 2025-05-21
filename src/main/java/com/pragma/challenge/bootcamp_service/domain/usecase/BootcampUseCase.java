package com.pragma.challenge.bootcamp_service.domain.usecase;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.ProfileNotFound;
import com.pragma.challenge.bootcamp_service.domain.mapper.BootcampProfileMapper;
import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfileRelation;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import com.pragma.challenge.bootcamp_service.domain.spi.BootcampPersistencePort;
import com.pragma.challenge.bootcamp_service.domain.spi.ProfileServiceGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BootcampUseCase implements BootcampServicePort {

  private final BootcampPersistencePort bootcampPersistencePort;
  private final ProfileServiceGateway profileServiceGateway;
  private final TransactionalOperator transactionalOperator;
  private final BootcampProfileMapper bootcampProfileMapper;

  @Override
  public Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp) {
    return registerWithProfiles(bootcamp).as(transactionalOperator::transactional);
  }

  @Override
  public Flux<BootcampProfile> getBootcamps(PageRequest pageRequest) {
    return bootcampPersistencePort
        .findAllBy(pageRequest)
        .flatMap(
            bootcampProfile ->
                profileServiceGateway
                    .getProfiles(bootcampProfile.id())
                    .map(
                        profiles ->
                            bootcampProfileMapper.toBootcampProfileWithProfiles(
                                bootcampProfile, profiles)));
  }

  @Override
  public Mono<Void> delete(Long bootcampId) {
    return bootcampPersistencePort
        .existsById(bootcampId)
        .filter(Boolean.TRUE::equals)
        .switchIfEmpty(Mono.error(ProfileNotFound::new))
        .flatMap(exists -> bootcampPersistencePort.delete(bootcampId))
        .then(profileServiceGateway.deleteBootcampProfiles(bootcampId))
        .as(transactionalOperator::transactional);
  }

  private Mono<Bootcamp> registerWithProfiles(Bootcamp bootcamp) {
    return profileServiceGateway
        .profilesExists(bootcamp.profileIds())
        .filter(Boolean.TRUE::equals)
        .switchIfEmpty(Mono.error(ProfileNotFound::new))
        .flatMap(exists -> saveBootcampWithRelation(bootcamp));
  }

  private Mono<Bootcamp> saveBootcampWithRelation(Bootcamp bootcamp) {
    List<Long> profileIds = bootcamp.profileIds();
    return bootcampPersistencePort
        .save(bootcamp)
        .flatMap(
            savedBootcamp ->
                saveProfileRelation(savedBootcamp.id(), profileIds).thenReturn(savedBootcamp));
  }

  private Mono<Void> saveProfileRelation(Long bootcampId, List<Long> profileIds) {
    return profileServiceGateway.createRelation(
        new BootcampProfiles(
            profileIds.stream()
                .map(profileId -> new BootcampProfileRelation(bootcampId, profileId))
                .toList()));
  }
}
