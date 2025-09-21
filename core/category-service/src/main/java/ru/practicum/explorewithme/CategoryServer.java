package ru.practicum.explorewithme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class CategoryServer {
    public static void main(String[] args) {
        SpringApplication.run(CategoryServer.class, args);
    }
}