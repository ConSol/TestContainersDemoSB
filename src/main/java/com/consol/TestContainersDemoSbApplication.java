package com.consol;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class TestContainersDemoSbApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TestContainersDemoSbApplication.class, args);
    }

}
