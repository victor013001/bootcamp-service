package com.pragma.challenge.bootcamp_service.infrastructure;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.pragma.challenge.bootcamp_service.domain.constants.Constants;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.handler.BootcampHandler;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.util.SwaggerResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class)))
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
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class)))
                })),
    @RouterOperation(
        path = "/api/v1/bootcamp/{id}",
        method = RequestMethod.DELETE,
        beanClass = BootcampHandler.class,
        beanMethod = "deleteBootcamp",
        operation =
            @Operation(
                operationId = "deleteBootcamp",
                summary = "Delete a bootcamp by ID",
                parameters = {
                  @Parameter(
                      in = ParameterIn.PATH,
                      name = "id",
                      required = true,
                      description = "ID of the bootcamp to delete")
                },
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      description = Constants.BOOTCAMP_DELETED_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultMessageResponse.class))),
                  @ApiResponse(
                      responseCode = "404",
                      description = Constants.BOOTCAMP_NOT_FOUND_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class)))
                })),
    @RouterOperation(
        path = "/api/v1/bootcamp/exists",
        method = RequestMethod.GET,
        beanClass = BootcampHandler.class,
        beanMethod = "bootcampsExists",
        operation =
            @Operation(
                operationId = "bootcampsExists",
                summary = "Check if bootcamps exist",
                parameters = {
                  @Parameter(
                      in = ParameterIn.QUERY,
                      name = "id",
                      description = "Bootcamp IDs to check existence",
                      required = true,
                      array = @ArraySchema(schema = @Schema(type = "string", example = "1")))
                },
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      description = "Existence check completed.",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultBooleanResponse.class))),
                  @ApiResponse(
                      responseCode = "400",
                      description = Constants.BAD_REQUEST_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class)))
                })),
    @RouterOperation(
        path = "/api/v1/bootcamp/user",
        method = RequestMethod.GET,
        beanClass = BootcampHandler.class,
        beanMethod = "getBootcampUser",
        operation =
            @Operation(
                operationId = "getBootcampUser",
                summary = "Get Bootcamp with higher number of users",
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultBootcampProfileResponse.class))),
                  @ApiResponse(
                      responseCode = "400",
                      description = Constants.BAD_REQUEST_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class))),
                  @ApiResponse(
                      responseCode = "500",
                      description = Constants.SERVER_ERROR_MSG,
                      content =
                          @Content(
                              mediaType = MediaType.APPLICATION_JSON_VALUE,
                              schema =
                                  @Schema(
                                      implementation =
                                          SwaggerResponses.DefaultErrorResponse.class)))
                }))
  })
  public RouterFunction<ServerResponse> routerFunction(BootcampHandler bootcampHandler) {
    return nest(
        path("/api/v1/bootcamp"),
        route(RequestPredicates.POST(""), bootcampHandler::createBootcamp)
            .andRoute(RequestPredicates.GET(""), bootcampHandler::getBootcamps)
            .andRoute(RequestPredicates.DELETE("/{id}"), bootcampHandler::deleteBootcamp)
            .andRoute(RequestPredicates.GET("/exists"), bootcampHandler::exists)
            .andRoute(RequestPredicates.GET("/user"), bootcampHandler::getBootcampUser));
  }
}
