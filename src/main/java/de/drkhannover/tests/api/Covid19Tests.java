package de.drkhannover.tests.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("de.drkhannover.tests.api.user.jpa")
@SpringBootApplication
public class Covid19Tests {
    
    public static void main(String[] args) {
        SpringApplication.run(Covid19Tests.class, args);
    }
}
