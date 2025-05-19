package com.pragma.challenge.bootcamp_service;

import org.springframework.boot.SpringApplication;

public class TestBootcampServiceApplication {
  public static void main(String[] args) {
    SpringApplication.from(BootcampServiceApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
