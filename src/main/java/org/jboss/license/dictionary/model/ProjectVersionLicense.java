package org.jboss.license.dictionary.model;

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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicense")
@Table(name = "project_version_license", indexes = {
        @Index(name = ProjectVersionLicense.IDX_NAME_PROJECT_VERSION_LICENSE_LICENSE, columnList = "license_id"),
        @Index(name = ProjectVersionLicense.IDX_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK, columnList = "proj_vers_license_check_id") })

@ToString(exclude = { "projectVersionLicenseHints" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseHints" })
public class ProjectVersionLicense {

    public static final String SEQUENCE_NAME = "project_version_license_id_seq";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_LICENSE = "idx_projverlic_lic";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK = "idx_projverlic_projverlicchk";

    public static final String FK_NAME_PROJECT_VERSION_LICENSE_LICENSE = "fk_projverlic_lic";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK = "fk_projverlic_projverlicchk";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @Column(name = "scope")
    @Getter
    @Setter
    private String scope;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_LICENSE))
    @Getter
    @Setter
    private License license;

    @ManyToOne
    @JoinColumn(name = "proj_vers_license_check_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK))
    @Getter
    @Setter
    private ProjectVersionLicenseCheck projectVersionLicenseCheck;

    @OneToMany(mappedBy = "projectVersionLicense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<ProjectVersionLicenseHint> projectVersionLicenseHints;

    public ProjectVersionLicense() {
        this.projectVersionLicenseHints = new HashSet<ProjectVersionLicenseHint>();
    }

    public void addProjectVersionLicenseHint(ProjectVersionLicenseHint projectVersionLicenseHint) {
        this.projectVersionLicenseHints.add(projectVersionLicenseHint);
        projectVersionLicenseHint.setProjectVersionLicense(this);
    }

    public static class Builder {

        private Integer id;
        private String scope;
        private License license;
        private ProjectVersionLicenseCheck projectVersionLicenseCheck;
        private Set<ProjectVersionLicenseHint> projectVersionLicenseHints;

        private Builder() {
            this.projectVersionLicenseHints = new HashSet<ProjectVersionLicenseHint>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder license(License license) {
            this.license = license;
            return this;
        }

        public Builder projectVersionLicenseCheck(ProjectVersionLicenseCheck projectVersionLicenseCheck) {
            this.projectVersionLicenseCheck = projectVersionLicenseCheck;
            return this;
        }

        public Builder projectVersionLicenseHints(Set<ProjectVersionLicenseHint> projectVersionLicenseHints) {
            this.projectVersionLicenseHints = projectVersionLicenseHints;
            return this;
        }

        public ProjectVersionLicense build() {
            ProjectVersionLicense projectVersionLicense = new ProjectVersionLicense();
            projectVersionLicense.id = this.id;
            projectVersionLicense.setScope(scope);
            projectVersionLicense.setLicense(license);
            projectVersionLicense.setProjectVersionLicenseCheck(projectVersionLicenseCheck);
            projectVersionLicense.setProjectVersionLicenseHints(projectVersionLicenseHints);

            // Set bi-directional mappings
            license.addProjectVersionLicense(projectVersionLicense);
            projectVersionLicenseCheck.addProjectVersionLicense(projectVersionLicense);
            projectVersionLicenseHints.stream().forEach(projectVersionLicenseHint -> {
                projectVersionLicenseHint.setProjectVersionLicense(projectVersionLicense);
            });
            return projectVersionLicense;
        }
    }

}
