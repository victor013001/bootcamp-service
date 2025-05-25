package com.pragma.challenge.bootcamp_service.domain.usecase;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.ProfileNotFound;
import com.pragma.challenge.bootcamp_service.domain.mapper.BootcampProfileMapper;
import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfileRelation;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampReport;
import com.pragma.challenge.bootcamp_service.domain.model.ProfileTechnology;
import com.pragma.challenge.bootcamp_service.domain.spi.BootcampPersistencePort;
import com.pragma.challenge.bootcamp_service.domain.spi.ProfileServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.spi.ReportServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.spi.UserServiceGateway;
import com.pragma.challenge.bootcamp_service.domain.validation.ValidListAnnotation;
import com.pragma.challenge.bootcamp_service.domain.validation.ValidNotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
public class BootcampUseCase implements BootcampServicePort {

  private final BootcampPersistencePort bootcampPersistencePort;
  private final ProfileServiceGateway profileServiceGateway;
  private final BootcampProfileMapper bootcampProfileMapper;
  private final ReportServiceGateway reportServiceGateway;
  private final UserServiceGateway userServiceGateway;

  @Override
  public Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp) {
    return Mono.just(bootcamp)
        .flatMap(
            bootcamp1 -> {
              ValidListAnnotation.valid(bootcamp1);
              ValidNotNull.valid(bootcamp1);
              return Mono.just(bootcamp1);
            })
        .flatMap(this::registerWithProfiles)
        .doOnSuccess(
            bootcampSaved ->
                createReport(bootcampSaved)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(
                        null,
                        ex ->
                            log.error(
                                "Unable to create report for bootcamp with id: {}",
                                bootcampSaved.id())));
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
        .then(profileServiceGateway.deleteBootcampProfiles(bootcampId));
  }

  @Override
  public Mono<Boolean> existsById(List<Long> bootcampIds) {
    return Flux.fromIterable(bootcampIds)
        .flatMap(id -> bootcampPersistencePort.existsById(id).flatMap(Mono::just))
        .any(result -> !result)
        .flatMap(foundFalse -> Mono.just(!foundFalse));
  }

  @Override
  public Mono<BootcampProfile> getBootcampUser() {
    return userServiceGateway
        .getBootcampWithHigherNumberUsers()
        .flatMap(
            bootcampId ->
                bootcampPersistencePort
                    .getBootcampById(bootcampId)
                    .flatMap(
                        bootcampProfile ->
                            profileServiceGateway
                                .getProfiles(bootcampProfile.id())
                                .map(
                                    profiles ->
                                        bootcampProfileMapper.toBootcampProfileWithProfiles(
                                            bootcampProfile, profiles))));
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

  private Mono<Void> createReport(Bootcamp bootcamp) {
    return profileServiceGateway
        .getProfiles(bootcamp.id())
        .flatMap(
            profileTechnologies ->
                reportServiceGateway.registerBootcampReport(
                    new BootcampReport(
                        bootcamp.id(),
                        bootcamp.name(),
                        bootcamp.description(),
                        bootcamp.launchDate(),
                        bootcamp.durationInWeeks(),
                        profileTechnologies.size(),
                        technologiesSize(profileTechnologies),
                        0)));
  }

  private int technologiesSize(List<ProfileTechnology> profileTechnologies) {
    return Math.toIntExact(
        profileTechnologies.stream()
            .flatMap(profileTechnology -> profileTechnology.technologies().stream())
            .distinct()
            .count());
  }
}
