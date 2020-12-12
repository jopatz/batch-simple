package net.opatz.batch.jobs.agile;

import lombok.extern.slf4j.Slf4j;
import net.opatz.batch.domain.App;
import net.opatz.batch.jobs.agile.utils.AgileDevelopmentJobProperties;
import net.opatz.batch.jobs.agile.utils.AgileDevelopmentProcessor;
import net.opatz.batch.repository.AppRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
public class AgileDevelopmentJobConfig {

    private static final String JOB_NAME = "AGILE_DEVELOPMENT";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final JobLauncher jobLauncher;

    private final AgileDevelopmentJobProperties properties;

    private final AppRepository repository;

    public AgileDevelopmentJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobLauncher jobLauncher, AgileDevelopmentJobProperties properties, AppRepository repository) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
        this.properties = properties;
        this.repository = repository;
    }

    @Scheduled(cron = "${jobs.agile.cronExpression}")
    public void run() throws Exception {

        final Job job = this.agileDevelopmentJob();
        final JobParameters parameters = new JobParametersBuilder()
            .addString("uuid", UUID.randomUUID().toString())
            .toJobParameters();

        JobExecution execution = this.jobLauncher.run(job, parameters);
        log.info("Exit status: {}", execution.getStatus());
    }

    @Bean
    public Job agileDevelopmentJob() {

        return this.jobBuilderFactory.get(JOB_NAME)
            .start(doAgileThingsStep())
            .build();
    }

    @Bean
    public Step doAgileThingsStep() {

        return stepBuilderFactory.get("DO_AGILE_THINGS")
            .<App, App>chunk(this.properties.getChunkSize())
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @Bean
    public ItemReader<App> reader() {

        final Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<App>()
            .name("AppRepositoryItemReader")
            .repository(this.repository)
            .methodName("findAll")
            .sorts(sorts)
            .build();
    }

    @Bean
    public ItemProcessor<App, App> processor() {

        return new AgileDevelopmentProcessor(this.properties);
    }

    @Bean
    public ItemWriter<App> writer() {

        return new RepositoryItemWriterBuilder<App>()
            .repository(this.repository)
            .methodName("save")
            .build();
    }
}
