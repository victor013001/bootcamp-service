package com.pragma.challenge.bootcamp_service.infrastructure.adapters.profile_service.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("profile-service")
public class ProfileServiceProperties {
  private String baseUrl;
  private String timeout;
}
