package org.jboss.license.dictionary.model;

import java.time.Instant;
import java.time.LocalDateTime;
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
@Table(name = "project_version_license_chk", indexes = {
        @Index(name = ProjectVersionLicenseCheck.IDX_NAME_PROJECT_VERSION_LICENSE_CHECK_PROJECT_VERSION, columnList = "project_version_id"),
        @Index(name = ProjectVersionLicenseCheck.IDX_NAME_PROJECT_VERSION_LICENSE_CHECK_LICENSE_DETERMINATION_TYPE, columnList = "license_det_type_id") })

@ToString(exclude = { "projectVersionLicenses" })
@EqualsAndHashCode(exclude = { "projectVersionLicenses" })
public class ProjectVersionLicenseCheck {

    public static final String SEQUENCE_NAME = "projverlicchk_id_seq";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_CHECK_PROJECT_VERSION = "idx_projverlicchk_projver";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_CHECK_LICENSE_DETERMINATION_TYPE = "idx_projverlicchk_licdettype";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_CHECK_PROJECT_VERSION = "fk_projverlicchk_projver";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_CHECK_LICENSE_DETERMINATION_TYPE = "fk_projverlicchk_licdettype";

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
    //@Column(name = "determination_date", columnDefinition = "timestamp with time zone")
    @Column(name = "determination_date")
    @Getter
    @Setter
    private LocalDateTime determinationDate;

    @Column(name = "notes")
    @Getter
    @Setter
    private String notes;

    @ManyToOne
    @JoinColumn(name = "project_version_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_CHECK_PROJECT_VERSION))
    @Getter
    @Setter
    private ProjectVersion projectVersion;

    @ManyToOne
    @JoinColumn(name = "license_det_type_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_CHECK_LICENSE_DETERMINATION_TYPE))
    @Getter
    @Setter
    private LicenseDeterminationType licenseDeterminationType;

    @OneToMany(mappedBy = "projectVersionLicenseCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<ProjectVersionLicense> projectVersionLicenses;

    public ProjectVersionLicenseCheck() {
        this.determinationDate = LocalDateTime.now();
        this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
    }

    @PrePersist
    private void initDeterminationTime() {
        this.determinationDate = LocalDateTime.now();
    }

    public void addProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        this.projectVersionLicenses.add(projectVersionLicense);
        projectVersionLicense.setProjectVersionLicenseCheck(this);
    }

    public static class Builder {

        private Integer id;
        private String determinedByUser;
        private LocalDateTime determinationDate;
        private String notes;
        private ProjectVersion projectVersion;
        private LicenseDeterminationType licenseDeterminationType;
        private Set<ProjectVersionLicense> projectVersionLicenses;

        private Builder() {
            this.determinationDate = LocalDateTime.now();
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

        public Builder determinationDate(LocalDateTime determinationDate) {
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
