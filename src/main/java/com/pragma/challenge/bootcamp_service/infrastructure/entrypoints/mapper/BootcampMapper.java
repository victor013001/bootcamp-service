package com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.mapper;

import com.pragma.challenge.bootcamp_service.domain.model.Bootcamp;
import com.pragma.challenge.bootcamp_service.infrastructure.entrypoints.dto.BootcampDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BootcampMapper {
  @Mapping(target = "id", ignore = true)
  Bootcamp toBootcamp(BootcampDto bootcampDto);
}
