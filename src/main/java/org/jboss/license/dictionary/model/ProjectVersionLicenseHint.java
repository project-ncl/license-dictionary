/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.license.dictionary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({
        @NamedQuery(name = ProjectVersionLicenseHint.QUERY_FIND_ALL_UNORDERED, query = "SELECT pvlh FROM ProjectVersionLicenseHint pvlh ") })
@ToString
@EqualsAndHashCode
public class ProjectVersionLicenseHint {

    public static final String QUERY_FIND_ALL_UNORDERED = "ProjectVersionLicenseHint.findAllUnordered";

    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE = "idx_projverlichint_projverlic";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE = "idx_projverlichint_lichinttype";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_HINT_PROJECT_VERSION_LICENSE = "fk_projverlichint_projverlic";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_HINT_LICENSE_HINT_TYPE = "fk_projverlichint_lichinttype";
    public static final String SEQUENCE_NAME = "project_version_license_hint_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
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
