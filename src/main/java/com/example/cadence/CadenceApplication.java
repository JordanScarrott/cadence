package com.example.cadence;

import io.temporal.spring.boot.EnableTemporal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTemporal
public class CadenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadenceApplication.class, args);
    }

}
