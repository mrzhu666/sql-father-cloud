package org.mrzhuyk.sqlfather.table;


import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


@EnableCustomConfig
@EnableCustomFeign
@SpringBootApplication
@Slf4j
public class TableApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TableApplication.class);
        ConfigurableApplicationContext application=app.run(args);
        //ConfigurableApplicationContext application=SpringApplication.run(Knife4jSpringBootDemoApplication.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \thttp://{}:{}\n\t"+
                "Doc: \thttp://{}:{}/doc.html\n"+
                "----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            env.getProperty("server.port"),
            "localhost",
            env.getProperty("server.port"),
            "localhost",
            env.getProperty("server.port"));
    }
}
