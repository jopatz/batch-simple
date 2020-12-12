package net.opatz.batch;

import lombok.extern.slf4j.Slf4j;
import net.opatz.batch.domain.App;
import net.opatz.batch.repository.AppRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(AppRepository repository) {

        return (args -> {
            if (repository.count() == 0) {
                log.debug("Inserting test data...");

                final App[] apps = new App[]{
                    new App("My Awesome Application"),
                    new App("Super Duper Tool"),
                    new App("Lorem Ipsum Dolor"),
                };

                Arrays.asList(apps).forEach(app -> {
                    if (!repository.exists(Example.of(app))) {
                        repository.save(app);
                    }
                });
            }
        });
    }
}
