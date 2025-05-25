package com.pragma.challenge.bootcamp_service.domain.usecase;

import static com.pragma.challenge.bootcamp_service.util.BootcampData.getBootcamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.ProfileNotFound;
import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import com.pragma.challenge.bootcamp_service.domain.spi.BootcampPersistencePort;
import com.pragma.challenge.bootcamp_service.domain.spi.ProfileServiceGateway;
import java.util.Collections;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

  @InjectMocks BootcampUseCase bootcampUseCase;

  @Mock BootcampPersistencePort bootcampPersistencePort;

  @Mock ProfileServiceGateway profileServiceGateway;

  @Test
  void shouldRegisterBootcampSuccessfully() {
    var bootcamp = getBootcamp();

    when(profileServiceGateway.profilesExists(bootcamp.profileIds())).thenReturn(Mono.just(true));
    when(profileServiceGateway.getProfiles(anyLong()))
        .thenReturn(Mono.just(Collections.emptyList()));

    when(bootcampPersistencePort.save(bootcamp))
        .thenAnswer(
            invocation -> {
              Bootcamp b = invocation.getArgument(0);
              return Mono.just(
                  new Bootcamp(
                      1L,
                      b.name(),
                      b.description(),
                      b.launchDate(),
                      b.durationInWeeks(),
                      b.profileIds()));
            });

    when(profileServiceGateway.createRelation(any(BootcampProfiles.class)))
        .thenReturn(Mono.empty());

    StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
        .assertNext(
            saved -> {
              assert Objects.nonNull(saved.id());
            })
        .verifyComplete();

    verify(profileServiceGateway).createRelation(any(BootcampProfiles.class));
  }

  @Test
  void shouldReturnErrorWhenProfilesNotFound() {
    var bootcamp = getBootcamp();

    when(profileServiceGateway.profilesExists(bootcamp.profileIds())).thenReturn(Mono.just(false));

    StepVerifier.create(bootcampUseCase.registerBootcamp(bootcamp))
        .expectError(ProfileNotFound.class)
        .verify();

    verify(bootcampPersistencePort, never()).save(any());
    verify(profileServiceGateway, never()).createRelation(any());
  }
}
