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
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "LicenseHintType")
@Table(name = "license_hint_type", indexes = {
        @Index(name = LicenseHintType.IDX_NAME_LICENSE_HINT_TYPE_NAME, columnList = "name") }, uniqueConstraints = {
                @UniqueConstraint(name = LicenseHintType.UC_NAME_LICENSE_HINT_TYPE_NAME, columnNames = { "name" }) })
@NamedQueries({ @NamedQuery(name = LicenseHintType.QUERY_FIND_ALL_UNORDERED, query = "SELECT lht FROM LicenseHintType lht") })

@ToString(exclude = { "projectVersionLicenseHints" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseHints" })
public class LicenseHintType {

    public static final String QUERY_FIND_ALL_UNORDERED = "LicenseHintType.findAllUnordered";

    public static final String IDX_NAME_LICENSE_HINT_TYPE_NAME = "idx_license_hint_type_name";
    public static final String SEQUENCE_NAME = "license_hinttype_id_seq";
    public static final String UC_NAME_LICENSE_HINT_TYPE_NAME = "uc_license_hint_type_name";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
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
