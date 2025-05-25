package com.pragma.challenge.bootcamp_service;

import static com.pragma.challenge.bootcamp_service.util.BootcampDtoData.getBootcampDto;
import static com.pragma.challenge.bootcamp_service.util.BootcampDtoData.getInvalidBootcampDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import com.pragma.challenge.bootcamp_service.domain.enums.ServerResponses;
import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardError;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.entity.BootcampEntity;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.repository.BootcampRepository;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.DefaultServerResponse;
import java.time.LocalDate;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("it")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootcampRouterRestIT {
  private final String BASE_PATH = "/api/v1/bootcamp";
  private final String PROFILE_SERVICE_PATH = "/api/v1/profile";
  private final String REPORT_SERVICE_PATH = "/api/v1/report";

  @Autowired WebTestClient webTestClient;
  @Autowired BootcampRepository bootcampRepository;

  @BeforeEach
  void setUp() {
    bootcampRepository
        .saveAll(
            List.of(
                BootcampEntity.builder()
                    .name("Java Backend Bootcamp")
                    .description("Intensive training on Java, Spring Boot, and REST APIs.")
                    .launchDate(LocalDate.now().plusDays(3))
                    .durationInWeeks(8)
                    .build()))
        .blockLast();
  }

  @Test
  void createBootcamp() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo(PROFILE_SERVICE_PATH + "/exists"))
            .withQueryParam("id", WireMock.matching(".*"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("profilesExists.json")));

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo(PROFILE_SERVICE_PATH + "/bootcamp"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("bootcampProfileRelationSuccess.json")));

    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo(PROFILE_SERVICE_PATH))
            .withQueryParam(Constants.BOOTCAMP_ID_PARAM, WireMock.matching(".*"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("profileByBootcampId.json")));

    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo(REPORT_SERVICE_PATH + "/bootcamp"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

    webTestClient
        .post()
        .uri(BASE_PATH)
        .bodyValue(getBootcampDto())
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(DefaultServerResponse.class)
        .consumeWith(
            exchangeResult -> {
              var response = exchangeResult.getResponseBody();
              assertNotNull(response);
              assertEquals(ServerResponses.BOOTCAMP_CREATED.getMessage(), response.data());
            });
  }

  @Test
  void createBootcampBadRequest() {
    webTestClient
        .post()
        .uri(BASE_PATH)
        .bodyValue(getInvalidBootcampDto())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(
            new ParameterizedTypeReference<DefaultServerResponse<Object, StandardError>>() {})
        .consumeWith(
            exchangeResult -> {
              var response = exchangeResult.getResponseBody();
              assertNotNull(response);
              assertEquals(
                  ServerResponses.BAD_REQUEST.getMessage(), response.error().getDescription());
            });
  }

  @Test
  void createBootcampBadProfiles() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo(PROFILE_SERVICE_PATH + "/exists"))
            .withQueryParam("id", WireMock.matching(".*"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("profilesNotExists.json")));

    webTestClient
        .post()
        .uri(BASE_PATH)
        .bodyValue(getBootcampDto())
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody(
            new ParameterizedTypeReference<DefaultServerResponse<Object, StandardError>>() {})
        .consumeWith(
            exchangeResult -> {
              var response = exchangeResult.getResponseBody();
              assertNotNull(response);
              assertEquals(
                  ServerResponses.PROFILE_NOT_FOUND.getMessage(),
                  response.error().getDescription());
            });
  }

  @Test
  void getBootcamps() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlPathEqualTo(PROFILE_SERVICE_PATH))
            .withQueryParam(Constants.BOOTCAMP_ID_PARAM, WireMock.matching(".*"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("profileByBootcampId.json")));

    webTestClient
        .get()
        .uri(BASE_PATH)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(DefaultServerResponse.class)
        .consumeWith(
            exchangeResult -> {
              var response = exchangeResult.getResponseBody();
              assertNotNull(response);
            });
  }

  @Test
  void deleteBootcamp() {
    var bootcampId = 1L;
    WireMock.stubFor(
        WireMock.delete(WireMock.urlPathMatching(PROFILE_SERVICE_PATH + "/1"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBodyFile("deletedProfiles.json")));

    webTestClient
        .delete()
        .uri(BASE_PATH + "/{id}", bootcampId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(DefaultServerResponse.class)
        .consumeWith(
            exchangeResult -> {
              var response = exchangeResult.getResponseBody();
              assertNotNull(response);
              System.out.println(response);
            });
  }
}
