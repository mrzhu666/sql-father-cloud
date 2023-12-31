package org.mrzhuyk.sqlfather.report;

import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableCustomFeign
@SpringBootApplication
public class ReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
