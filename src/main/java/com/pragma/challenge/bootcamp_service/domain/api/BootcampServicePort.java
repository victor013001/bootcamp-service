package com.pragma.challenge.bootcamp_service.domain.api;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
  Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp);

  Flux<BootcampProfile> getBootcamps(PageRequest pageRequest);

  Mono<Void> delete(Long bootcampId);

  Mono<Boolean> existsById(List<Long> bootcampIds);
}
