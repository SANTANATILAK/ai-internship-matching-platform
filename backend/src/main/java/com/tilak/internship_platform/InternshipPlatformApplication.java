package com.tilak.internship_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InternshipPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            InternshipPlatformApplication.class,
            args
        );
    }
}