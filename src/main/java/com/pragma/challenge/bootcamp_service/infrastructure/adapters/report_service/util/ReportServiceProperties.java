package com.pragma.challenge.bootcamp_service.infrastructure.adapters.report_service.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("report-service")
public class ReportServiceProperties {
  private String baseUrl;
  private String timeout;
}
