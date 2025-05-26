package com.pragma.challenge.bootcamp_service.domain.mapper;

import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfile;
import com.pragma.challenge.bootcamp_service.domain.model.BootcampProfileUser;
import com.pragma.challenge.bootcamp_service.domain.model.ProfileTechnology;
import com.pragma.challenge.bootcamp_service.domain.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BootcampProfileMapper {
  @Mapping(target = "profiles", source = "profiles")
  BootcampProfile toBootcampProfileWithProfiles(
      BootcampProfile bootcampProfile, List<ProfileTechnology> profiles);

  @Mapping(target = "users", source = "users")
  BootcampProfileUser toBootcampProfileUserWithUsers(BootcampProfile bootcampProfile, List<User> users);
}
