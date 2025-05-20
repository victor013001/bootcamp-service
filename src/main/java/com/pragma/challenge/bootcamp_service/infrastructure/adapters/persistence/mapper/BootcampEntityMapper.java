package com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.mapper;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BootcampEntityMapper {
  Bootcamp toModel(BootcampEntity bootcamp);

  BootcampEntity toEntity(Bootcamp bootcamp);

  BootcampProfile toBootcampProfile(BootcampEntity bootcamp);
}
