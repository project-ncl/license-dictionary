package org.jboss.license.dictionary.model;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicenseCheck")
@Table(name = "project_version_license_check", indexes = {
        @Index(name = "idx_project_version_license_check_license_determination_type", columnList = "project_version_id") })

@ToString(exclude = { "projectVersionLicenses" })
@EqualsAndHashCode(exclude = { "projectVersionLicenses" })
public class ProjectVersionLicenseCheck {

    private static final String SEQUENCE_NAME = "project_version_license_check_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @NotNull
    @Size(max = 50)
    @Column(name = "determined_by_user", length = 50)
    @Getter
    @Setter
    private String determinedByUser;

    @NotNull
    @Column(name = "determination_date", columnDefinition = "timestamp with time zone")
    @Getter
    @Setter
    private Date determinationDate;

    @Column(name = "notes")
    @Getter
    @Setter
    private String notes;

    @ManyToOne
    @JoinColumn(name = "project_version_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_check_project_version"))
    @Getter
    @Setter
    private ProjectVersion projectVersion;

    @ManyToOne
    @JoinColumn(name = "license_determination_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_check_license_determination_type"))
    @Getter
    @Setter
    private LicenseDeterminationType licenseDeterminationType;

    @OneToMany(mappedBy = "projectVersionLicenseCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<ProjectVersionLicense> projectVersionLicenses;

    public ProjectVersionLicenseCheck() {
        this.determinationDate = Date.from(Instant.now());
        this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
    }

    @PrePersist
    private void initDeterminationTime() {
        this.determinationDate = Date.from(Instant.now());
    }

    public void addProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        this.projectVersionLicenses.add(projectVersionLicense);
        projectVersionLicense.setProjectVersionLicenseCheck(this);
    }

    public static class Builder {

        private Integer id;
        private String determinedByUser;
        private Date determinationDate;
        private String notes;
        private ProjectVersion projectVersion;
        private LicenseDeterminationType licenseDeterminationType;
        private Set<ProjectVersionLicense> projectVersionLicenses;

        private Builder() {
            this.determinationDate = Date.from(Instant.now());
            this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder determinedByUser(String determinedByUser) {
            this.determinedByUser = determinedByUser;
            return this;
        }

        public Builder determinationDate(Date determinationDate) {
            this.determinationDate = determinationDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder projectVersion(ProjectVersion projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder licenseDeterminationType(LicenseDeterminationType licenseDeterminationType) {
            this.licenseDeterminationType = licenseDeterminationType;
            return this;
        }

        public Builder projectVersionLicenses(Set<ProjectVersionLicense> projectVersionLicenses) {
            this.projectVersionLicenses = projectVersionLicenses;
            return this;
        }

        public ProjectVersionLicenseCheck build() {
            ProjectVersionLicenseCheck projectVersionLicenseCheck = new ProjectVersionLicenseCheck();
            projectVersionLicenseCheck.id = this.id;
            projectVersionLicenseCheck.setDeterminedByUser(determinedByUser);
            projectVersionLicenseCheck.setDeterminationDate(determinationDate);
            projectVersionLicenseCheck.setNotes(notes);
            projectVersionLicenseCheck.setProjectVersion(projectVersion);
            projectVersionLicenseCheck.setLicenseDeterminationType(licenseDeterminationType);
            projectVersionLicenseCheck.setProjectVersionLicenses(projectVersionLicenses);

            // Set bi-directional mappings
            projectVersion.addProjectVersionLicenseCheck(projectVersionLicenseCheck);
            licenseDeterminationType.addProjectVersionLicenseCheck(projectVersionLicenseCheck);
            projectVersionLicenses.stream().forEach(projectVersionLicense -> {
                projectVersionLicense.setProjectVersionLicenseCheck(projectVersionLicenseCheck);
            });

            return projectVersionLicenseCheck;
        }
    }

}
