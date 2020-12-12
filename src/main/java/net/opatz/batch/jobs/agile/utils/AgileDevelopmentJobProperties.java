package net.opatz.batch.jobs.agile.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jobs.agile")
@Getter
@Setter
public class AgileDevelopmentJobProperties {

    private String cronExpression;

    private int chunkSize = 1;

    private int maxIssues = Integer.MAX_VALUE;
}
