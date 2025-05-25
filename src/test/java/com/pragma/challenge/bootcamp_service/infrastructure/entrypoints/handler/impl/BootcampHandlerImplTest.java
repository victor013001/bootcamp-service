package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.impl;

import static com.pragma.challenge.bootcamp_service.util.BootcampDtoData.getBootcampDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;
import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.BootcampMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.BootcampMapperImpl;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.DefaultServerResponseMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.DefaultServerResponseMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BootcampHandlerImplTest {

  @InjectMocks BootcampHandlerImpl bootcampHandler;

  @Mock BootcampServicePort bootcampServicePort;

  @Spy BootcampMapper bootcampMapper = new BootcampMapperImpl();

  @Spy
  DefaultServerResponseMapper defaultServerResponseMapper = new DefaultServerResponseMapperImpl();

  @Test
  void shouldCreateBootcampSuccessfully() {
    var bootcampDto = getBootcampDto();
    var request = MockServerRequest.builder().body(Mono.just(bootcampDto));

    when(bootcampServicePort.registerBootcamp(any(Bootcamp.class)))
        .thenAnswer(
            invocation -> {
              Bootcamp bootcamp = invocation.getArgument(0);
              return Mono.just(
                  new Bootcamp(
                      1L,
                      bootcamp.name(),
                      bootcamp.description(),
                      bootcamp.launchDate(),
                      bootcamp.durationInWeeks(),
                      bootcamp.profileIds()));
            });

    StepVerifier.create(bootcampHandler.createBootcamp(request))
        .assertNext(
            serverResponse -> {
              assert serverResponse
                  .statusCode()
                  .isSameCodeAs(ServerResponses.BOOTCAMP_CREATED.getHttpStatus());
            })
        .verifyComplete();
  }
}
