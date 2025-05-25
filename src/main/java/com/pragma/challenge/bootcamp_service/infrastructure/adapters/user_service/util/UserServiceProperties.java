package com.pragma.challenge.bootcamp_service.infrastructure.adapters.user_service.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("user-service")
public class UserServiceProperties {
  private String baseUrl;
  private String timeout;
}
