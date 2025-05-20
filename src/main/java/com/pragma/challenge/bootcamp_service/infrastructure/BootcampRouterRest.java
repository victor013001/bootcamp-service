package com.pragma.challenge.bootcamp_service.infrastructure;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import com.pragma.challenge.bootcamp_service.domain.exceptions.StandardError;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.BootcampHandler;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.SwaggerResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouterRest {
  @Bean
  @RouterOperations({
    @RouterOperation(
        path = "/api/v1/bootcamp",
        method = RequestMethod.POST,
        beanClass = BootcampHandler.class,
        beanMethod = "createBootcamp",
        operation =
            @Operation(
                operationId = "createBootcamp",
                summary = "Create new bootcamp",
                requestBody =
                    @RequestBody(
                        required = true,
                        content =
                            @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = BootcampDto.class))),
                responses = {
                  @ApiResponse(
                      responseCode = "201",
                      description = Constants.BOOTCAMP_CREATED_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultMessageResponse.class))),
                  @ApiResponse(
                      responseCode = "400",
                      description = Constants.BAD_REQUEST_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = StandardError.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = StandardError.class)))
                })),
    @RouterOperation(
        path = "/api/v1/bootcamp",
        method = RequestMethod.GET,
        beanClass = BootcampHandler.class,
        beanMethod = "getBootcamps",
        operation =
            @Operation(
                operationId = "getBootcamps",
                summary = "Get bootcamps ordered by name or profile size",
                parameters = {
                  @Parameter(
                      in = ParameterIn.QUERY,
                      name = Constants.SORT_DIRECTION,
                      description = "ASC for ascending or DESC for descending order."),
                  @Parameter(
                      in = ParameterIn.QUERY,
                      name = Constants.SORT_BY,
                      description = "Field to sort by. Accepted values: 'name' or 'profile'."),
                  @Parameter(
                      in = ParameterIn.QUERY,
                      name = Constants.PAGE_NUMBER_PARAM,
                      description = "Page number to retrieve (0-based)."),
                  @Parameter(
                      in = ParameterIn.QUERY,
                      name = Constants.PAGE_SIZE_PARAM,
                      description = "Number of items per page.")
                },
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      description = "Bootcamp with profiles.",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultBootcampProfileResponse.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema = @Schema(implementation = StandardError.class)))
                }))
  })
  public RouterFunction<ServerResponse> routerFunction(BootcampHandler bootcampHandler) {
    return nest(
        path("/api/v1/bootcamp"),
        route(RequestPredicates.POST(""), bootcampHandler::createBootcamp)
            .andRoute(RequestPredicates.GET(""), bootcampHandler::getBootcamps));
  }
}
