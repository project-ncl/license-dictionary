package org.jboss.license.dictionary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicenseHint")
@Table(name = "project_version_license_hint", indexes = {
        @Index(name = ProjectVersionLicenseHint.IDX_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE, columnList = "project_version_license_id"),
        @Index(name = ProjectVersionLicenseHint.IDX_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE, columnList = "license_hint_type_id") })

@ToString
@EqualsAndHashCode
public class ProjectVersionLicenseHint {

    public static final String SEQUENCE_NAME = "project_version_license_hint_id_seq";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE = "idx_projverlichint_projverlic";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE = "idx_projverlichint_lichinttype";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE = "fk_projverlichint_projverlic";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE = "fk_projverlichint_lichinttype";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @Column(name = "value")
    @Getter
    @Setter
    private String value;

    @ManyToOne
    @JoinColumn(name = "project_version_license_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE))
    @Getter
    @Setter
    private ProjectVersionLicense projectVersionLicense;

    @ManyToOne
    @JoinColumn(name = "license_hint_type_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE))
    @Getter
    @Setter
    private LicenseHintType licenseHintType;

    public static class Builder {

        private Integer id;
        private String value;
        private ProjectVersionLicense projectVersionLicense;
        private LicenseHintType licenseHintType;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder projectVersionLicense(ProjectVersionLicense projectVersionLicense) {
            this.projectVersionLicense = projectVersionLicense;
            return this;
        }

        public Builder licenseHintType(LicenseHintType licenseHintType) {
            this.licenseHintType = licenseHintType;
            return this;
        }

        public ProjectVersionLicenseHint build() {
            ProjectVersionLicenseHint projectVersionLicenseHint = new ProjectVersionLicenseHint();
            projectVersionLicenseHint.id = this.id;
            projectVersionLicenseHint.setValue(value);
            projectVersionLicenseHint.setProjectVersionLicense(projectVersionLicense);
            projectVersionLicenseHint.setLicenseHintType(licenseHintType);

            // Set bi-directional mappings
            projectVersionLicense.addProjectVersionLicenseHint(projectVersionLicenseHint);
            licenseHintType.addProjectVersionLicenseHint(projectVersionLicenseHint);

            return projectVersionLicenseHint;
        }
    }

}
