package com.example.festimo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class FestimoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FestimoApplication.class, args);
    }
}