package com.pragma.challenge.bootcamp_service.infrastructure.adapters.persistence.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bootcamp")
public class BootcampEntity {
  @Id private Long id;
  private String name;
  private String description;
  private LocalDate launchDate;
  private Integer durationInWeeks;
}
