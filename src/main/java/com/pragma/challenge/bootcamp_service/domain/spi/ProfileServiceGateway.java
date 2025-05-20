package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProfileServiceGateway {
  Mono<Boolean> profilesExists(List<Long> profileIds);

  Mono<Void> createRelation(BootcampProfiles bootcampProfiles);
}
