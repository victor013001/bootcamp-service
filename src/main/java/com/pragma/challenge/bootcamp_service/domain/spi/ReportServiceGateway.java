package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.BootcampReport;
import reactor.core.publisher.Mono;

public interface ReportServiceGateway {
  Mono<Void> registerBootcampReport(BootcampReport bootcampReport);
}
