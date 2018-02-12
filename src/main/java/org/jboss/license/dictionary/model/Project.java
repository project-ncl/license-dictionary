package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "Project")
@Table(name = "project", indexes = { @Index(name = "idx_project", columnList = "ecosystem,key") })

@ToString(exclude = { "projectVersions" })
@EqualsAndHashCode(exclude = { "projectVersions" })
public class Project {

    public static final String SEQUENCE_NAME = "project_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
    private Integer id;

    @NotNull
    @Size(max = 10)
    @Column(name = "ecosystem", length = 10)
    @Getter
    @Setter
    private String ecosystem;

    @NotNull
    @Column(name = "key")
    @Getter
    @Setter
    private String key;

    @OneToMany(mappedBy = "project")
    @Getter
    @Setter
    private Set<ProjectVersion> projectVersions;

    public Project() {
        this.projectVersions = new HashSet<ProjectVersion>();
    }

    public void addProjectVersion(ProjectVersion projectVersion) {
        this.projectVersions.add(projectVersion);
    }

    public static class Builder {

        private Integer id;
        private String ecosystem;
        private String key;
        private Set<ProjectVersion> projectVersions;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder ecosystem(String ecosystem) {
            this.ecosystem = ecosystem;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder projectVersions(Set<ProjectVersion> projectVersions) {
            this.projectVersions = projectVersions;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.setId(id);
            project.setEcosystem(ecosystem);
            project.setKey(key);
            project.setProjectVersions(projectVersions);

            // Set bi-directional mappings
            projectVersions.stream().forEach(projectVersion -> {
                projectVersion.setProject(project);
            });

            return project;
        }
    }

}
