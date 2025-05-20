package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.impl;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.BootcampHandler;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.BootcampMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.DefaultServerResponseMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootcampHandlerImpl implements BootcampHandler {
  private static final String LOG_PREFIX = "[BOOTCAMP_HANDLER] >>> ";

  private final BootcampServicePort bootcampServicePort;
  private final BootcampMapper bootcampMapper;
  private final RequestValidator requestValidator;
  private final DefaultServerResponseMapper defaultServerResponseMapper;

  @Override
  public Mono<ServerResponse> createBootcamp(ServerRequest request) {
    return request
        .bodyToMono(BootcampDto.class)
        .flatMap(requestValidator::validate)
        .flatMap(
            bootcampDto -> {
              log.info(
                  "{} Creating bootcamp with name: {}, description: {}, launch date: {}, duration in weeks: {} and profiles: {}.",
                  LOG_PREFIX,
                  bootcampDto.name(),
                  bootcampDto.description(),
                  bootcampDto.launchDate(),
                  bootcampDto.durationInWeeks(),
                  bootcampDto.profileIds());
              return bootcampServicePort
                  .registerBootcamp(bootcampMapper.toBootcamp(bootcampDto))
                  .doOnSuccess(
                      bootcamp ->
                          log.info(
                              "{} {} with id: {}.",
                              LOG_PREFIX,
                              ServerResponses.BOOTCAMP_CREATED.getMessage(),
                              bootcamp.id()));
            })
        .flatMap(
            ignore ->
                ServerResponse.status(ServerResponses.BOOTCAMP_CREATED.getHttpStatus())
                    .bodyValue(
                        defaultServerResponseMapper.toResponse(
                            ServerResponses.BOOTCAMP_CREATED.getMessage())));
  }
}
