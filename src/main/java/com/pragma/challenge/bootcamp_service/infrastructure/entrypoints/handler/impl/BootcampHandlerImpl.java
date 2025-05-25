package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.impl;

import static com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.ResponseUtil.buildResponse;
import static com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.ResponseUtil.buildStandardError;

import com.pragma.challenge.bootcamp_service.domain.api.BootcampServicePort;
import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;
import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardException;
import com.pragma.challenge.bootcamp_service.domain.exceptions.standard_exception.BadRequest;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.BootcampHandler;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.BootcampMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper.DefaultServerResponseMapper;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.ParseUtil;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
  private final DefaultServerResponseMapper defaultServerResponseMapper;

  @Override
  public Mono<ServerResponse> createBootcamp(ServerRequest request) {
    return request
        .bodyToMono(BootcampDto.class)
        .switchIfEmpty(Mono.error(BadRequest::new))
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
                buildResponse(
                    ServerResponses.BOOTCAMP_CREATED.getHttpStatus(),
                    ServerResponses.BOOTCAMP_CREATED.getMessage(),
                    null,
                    defaultServerResponseMapper))
        .doOnError(logErrorHandler())
        .onErrorResume(StandardException.class, standardErrorHandler())
        .onErrorResume(genericErrorHandler());
  }

  @Override
  public Mono<ServerResponse> getBootcamps(ServerRequest request) {
    String pageNumber =
        request.queryParam(Constants.PAGE_NUMBER_PARAM).orElse(Constants.PAGE_NUMBER_DEFAULT);
    String pageSize =
        request.queryParam(Constants.PAGE_SIZE_PARAM).orElse(Constants.PAGE_SIZE_DEFAULT);
    String sortDirectionParam = request.queryParam(Constants.SORT_DIRECTION).orElse(Constants.ASC);
    String sortByParam = request.queryParam(Constants.SORT_BY).orElse(Constants.NAME_PARAM);
    log.info(
        "{} Getting profiles sorted by: {} with direction: {}",
        LOG_PREFIX,
        sortByParam,
        sortDirectionParam);
    return bootcampServicePort
        .getBootcamps(
            PageRequest.of(
                ParseUtil.toInt(pageNumber),
                ParseUtil.toInt(pageSize),
                Sort.by(
                    ParseUtil.toSortDirection(sortDirectionParam),
                    ParseUtil.validate(sortByParam))))
        .collectList()
        .flatMap(
            bootcampProfile -> {
              log.info("{} Bootcamp page: {} with size: {}", LOG_PREFIX, pageNumber, pageSize);
              return buildResponse(
                  HttpStatus.OK, bootcampProfile, null, defaultServerResponseMapper);
            })
        .doOnError(logErrorHandler())
        .onErrorResume(StandardException.class, standardErrorHandler())
        .onErrorResume(genericErrorHandler());
  }

  @Override
  public Mono<ServerResponse> deleteBootcamp(ServerRequest request) {
    String id = request.pathVariable(Constants.ID_PATH_VARIABLE);
    return Mono.just(ParseUtil.toLong(id))
        .flatMap(
            bootcampId -> {
              log.info("{} Deleting bootcamp with id: {}.", LOG_PREFIX, bootcampId);
              return bootcampServicePort.delete(bootcampId);
            })
        .then(
            buildResponse(
                ServerResponses.BOOTCAMP_DELETED.getHttpStatus(),
                ServerResponses.BOOTCAMP_DELETED.getMessage(),
                null,
                defaultServerResponseMapper))
        .doOnError(logErrorHandler())
        .onErrorResume(StandardException.class, standardErrorHandler())
        .onErrorResume(genericErrorHandler());
  }

  @Override
  public Mono<ServerResponse> exists(ServerRequest request) {
    return Mono.justOrEmpty(
            request.queryParams().get(Constants.ID_PATH_VARIABLE).stream()
                .map(Long::parseLong)
                .toList())
        .flatMap(
            bootcampIds -> {
              log.info("{} Checking if bootcamps with ids {} exist.", LOG_PREFIX, bootcampIds);
              return bootcampServicePort.existsById(bootcampIds);
            })
        .flatMap(exists -> buildResponse(HttpStatus.OK, exists, null, defaultServerResponseMapper))
        .doOnError(logErrorHandler())
        .onErrorResume(StandardException.class, standardErrorHandler())
        .onErrorResume(genericErrorHandler());
  }

  @Override
  public Mono<ServerResponse> getBootcampUser(ServerRequest request) {
    return bootcampServicePort
        .getBootcampUser()
        .flatMap(
            bootcampProfile ->
                buildResponse(HttpStatus.OK, bootcampProfile, null, defaultServerResponseMapper))
        .doOnError(logErrorHandler())
        .onErrorResume(StandardException.class, standardErrorHandler())
        .onErrorResume(genericErrorHandler());
  }

  private Consumer<Throwable> logErrorHandler() {
    return ex ->
        log.error(
            "{} Exception {} caught. Caused by: {}",
            LOG_PREFIX,
            ex.getClass().getSimpleName(),
            ex.getMessage());
  }

  private Function<StandardException, Mono<ServerResponse>> standardErrorHandler() {
    return ex ->
        buildResponse(ex.getHttpStatus(), null, ex.getStandardError(), defaultServerResponseMapper);
  }

  private Function<Throwable, Mono<ServerResponse>> genericErrorHandler() {
    return ex ->
        buildResponse(
            ServerResponses.SERVER_ERROR.getHttpStatus(),
            null,
            buildStandardError(ServerResponses.SERVER_ERROR),
            defaultServerResponseMapper);
  }
}
