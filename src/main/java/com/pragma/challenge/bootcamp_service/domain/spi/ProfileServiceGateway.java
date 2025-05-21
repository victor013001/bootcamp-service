package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfiles;
import com.pragma.challenge.bootcamp_service.domain.model.ProfileTechnology;
import java.util.List;
import reactor.core.publisher.Mono;

public interface ProfileServiceGateway {
  Mono<Boolean> profilesExists(List<Long> profileIds);

  Mono<Void> createRelation(BootcampProfiles bootcampProfiles);

  Mono<List<ProfileTechnology>> getProfiles(Long bootcampId);

  Mono<Void> deleteBootcampProfiles(Long bootcampId);
}
