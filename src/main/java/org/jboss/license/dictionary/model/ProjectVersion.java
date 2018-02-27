package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

@Entity(name = "ProjectVersion")
@Table(name = "project_version", indexes = {
        @Index(name = ProjectVersion.IDX_NAME_PROJECT_VERSION_PROJECT, columnList = "project_id") })

@ToString(exclude = { "projectVersionLicenseChecks" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseChecks" })
public class ProjectVersion {

    public static final String SEQUENCE_NAME = "project_version_id_seq";
    public static final String IDX_NAME_PROJECT_VERSION_PROJECT = "idx_projver_proj";
    public static final String FK_NAME_PROJECT_VERSION_PROJECT = "fk_projver_proj";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @NotNull
    @Column(name = "scm_url")
    @Getter
    @Setter
    private String scmUrl;

    @NotNull
    @Size(max = 50)
    @Column(name = "scm_revision", length = 50)
    @Getter
    @Setter
    private String scmRevision;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_PROJECT))
    @Getter
    @Setter
    private Project project;

    @OneToMany(mappedBy = "projectVersion")
    @Getter
    @Setter
    private Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks;

    public ProjectVersion() {
        this.projectVersionLicenseChecks = new HashSet<ProjectVersionLicenseCheck>();
    }

    public void addProjectVersionLicenseCheck(ProjectVersionLicenseCheck projectVersionLicenseCheck) {
        this.projectVersionLicenseChecks.add(projectVersionLicenseCheck);
        projectVersionLicenseCheck.setProjectVersion(this);
    }

    public static class Builder {

        private Integer id;
        private String scmUrl;
        private String scmRevision;
        private Project project;
        private Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks;

        private Builder() {
            this.projectVersionLicenseChecks = new HashSet<ProjectVersionLicenseCheck>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder scmUrl(String scmUrl) {
            this.scmUrl = scmUrl;
            return this;
        }

        public Builder scmRevision(String scmRevision) {
            this.scmRevision = scmRevision;
            return this;
        }

        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder projectVersionLicenseChecks(Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks) {
            this.projectVersionLicenseChecks = projectVersionLicenseChecks;
            return this;
        }

        public ProjectVersion build() {
            ProjectVersion projectVersion = new ProjectVersion();
            projectVersion.id = this.id;
            projectVersion.setScmUrl(scmUrl);
            projectVersion.setScmRevision(scmRevision);
            projectVersion.setProject(project);
            projectVersion.setProjectVersionLicenseChecks(projectVersionLicenseChecks);

            // Set bi-directional mappings
            project.addProjectVersion(projectVersion);
            projectVersionLicenseChecks.stream().forEach(projectVersionLicenseCheck -> {
                projectVersionLicenseCheck.setProjectVersion(projectVersion);
            });

            return projectVersion;
        }
    }

}
