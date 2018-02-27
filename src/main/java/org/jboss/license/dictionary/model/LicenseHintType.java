package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

@Entity(name = "LicenseHintType")
@Table(name = "license_hint_type")

@ToString(exclude = { "projectVersionLicenseHints" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseHints" })
public class LicenseHintType {

    public static final String SEQUENCE_NAME = "license_hinttype_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100)
    @Getter
    @Setter
    private String name;

    @OneToMany(mappedBy = "licenseHintType")
    @Getter
    @Setter
    private Set<ProjectVersionLicenseHint> projectVersionLicenseHints;

    public LicenseHintType() {
        this.projectVersionLicenseHints = new HashSet<ProjectVersionLicenseHint>();
    }

    public void addProjectVersionLicenseHint(ProjectVersionLicenseHint projectVersionLicenseHint) {
        this.projectVersionLicenseHints.add(projectVersionLicenseHint);
        projectVersionLicenseHint.setLicenseHintType(this);
    }

    public static class Builder {

        private Integer id;
        private String name;
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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder projectVersionLicenseHints(Set<ProjectVersionLicenseHint> projectVersionLicenseHints) {
            this.projectVersionLicenseHints = projectVersionLicenseHints;
            return this;
        }

        public LicenseHintType build() {
            LicenseHintType licenseHintType = new LicenseHintType();
            licenseHintType.id = this.id;
            licenseHintType.setName(name);
            licenseHintType.setProjectVersionLicenseHints(projectVersionLicenseHints);

            // Set bi-directional mappings
            projectVersionLicenseHints.stream().forEach(projectVersionLicenseHint -> {
                projectVersionLicenseHint.setLicenseHintType(licenseHintType);
            });
            return licenseHintType;
        }
    }
}
