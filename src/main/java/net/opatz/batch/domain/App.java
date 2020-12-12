package net.opatz.batch.domain;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "apps")
@Data
@NoArgsConstructor
public class App implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ISSUES")
    private Integer issues;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "version")
    @NotNull
    private Long version;

    @Column(name = "LAST_MODIFIED")
    @NotNull
    private Timestamp lastModified;

    public App(String description) {

        this.description = description;
        this.issues = 0;
        this.version = 1L;
        this.lastModified = Timestamp.from(Instant.now());
    }

    @PrePersist
    public void prePersist() {

        this.lastModified = Timestamp.from(Instant.now());
    }

    /**
     * Convenience method
     */
    public void incrementVersion() {

        this.version += 1;
        this.issues = 0;
    }

    /**
     * Convenience method
     */
    public void addIssues(int newIssues) {

        this.setIssues(this.getIssues() + newIssues);
    }
}
