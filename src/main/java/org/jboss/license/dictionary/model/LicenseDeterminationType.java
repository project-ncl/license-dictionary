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

@Entity(name = "LicenseDeterminationType")
@Table(name = "license_determination_type")

@ToString(exclude = { "projectVersionLicenseChecks" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseChecks" })
public class LicenseDeterminationType {

    public static final String SEQUENCE_NAME = "license_dettype_id_seq";

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

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @OneToMany(mappedBy = "licenseDeterminationType")
    @Getter
    @Setter
    private Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks;

    public LicenseDeterminationType() {
        this.projectVersionLicenseChecks = new HashSet<ProjectVersionLicenseCheck>();
    }

    public void addProjectVersionLicenseCheck(ProjectVersionLicenseCheck projectVersionLicenseCheck) {
        this.projectVersionLicenseChecks.add(projectVersionLicenseCheck);
        projectVersionLicenseCheck.setLicenseDeterminationType(this);
    }

    public static class Builder {

        private Integer id;
        private String name;
        private String description;
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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder projectVersionLicenseChecks(Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks) {
            this.projectVersionLicenseChecks = projectVersionLicenseChecks;
            return this;
        }

        public LicenseDeterminationType build() {
            LicenseDeterminationType licenseDeterminationType = new LicenseDeterminationType();
            licenseDeterminationType.id = this.id;
            licenseDeterminationType.setName(name);
            licenseDeterminationType.setDescription(description);
            licenseDeterminationType.setProjectVersionLicenseChecks(projectVersionLicenseChecks);

            // Set bi-directional mappings
            projectVersionLicenseChecks.stream().forEach(projectVersionLicenseCheck -> {
                projectVersionLicenseCheck.setLicenseDeterminationType(licenseDeterminationType);
            });

            return licenseDeterminationType;
        }
    }

}
