package net.opatz.batch.jobs.agile.utils;

import lombok.extern.slf4j.Slf4j;
import net.opatz.batch.domain.App;
import org.springframework.batch.item.ItemProcessor;

import java.util.Random;

@Slf4j
public class AgileDevelopmentProcessor implements ItemProcessor<App, App> {

    private final AgileDevelopmentJobProperties properties;

    public AgileDevelopmentProcessor(AgileDevelopmentJobProperties properties) {

        this.properties = properties;
    }

    @Override
    public App process(App item) {

        if (maxIssuesExceeded(item.getIssues(), this.properties.getMaxIssues())) {
            return null;
        }

        this.addNewIssues(item);

        if (maxIssuesExceeded(item.getIssues(), this.properties.getMaxIssues())) {
            log.warn(
                "App '{}' with version {} has exceeded the maximum of allowed issues ({})!",
                item.getDescription(),
                item.getVersion(),
                this.properties.getMaxIssues()
            );

            item.incrementVersion();

            log.debug(
                "The new version {} was created for app '{}' - All issues were magically solved ;)",
                item.getVersion(),
                item.getDescription()
            );
        }

        return item;
    }

    /**
     * Convenience method
     */
    private boolean maxIssuesExceeded(int issues, int maxIssues) {

        return issues > maxIssues;
    }

    /**
     * Convenience method
     */
    private void addNewIssues(App item) {

        final Random random = new Random();
        final int newIssues = random.nextInt(11);

        log.debug("Adding {} new issue(s) to app '{}'", newIssues, item.getDescription());

        item.addIssues(newIssues);
    }
}
