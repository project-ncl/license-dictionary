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

@Entity(name = "LicenseApprovalStatus")
@Table(name = "license_approval_status", indexes = {
        @Index(name = LicenseApprovalStatus.IDX_NAME_LICENSE_STATUS_NAME, columnList = "name") }, uniqueConstraints = {
                @UniqueConstraint(name = LicenseApprovalStatus.UC_NAME_LICENSE_STATUS_NAME, columnNames = { "name" }) })
@NamedQueries({
        @NamedQuery(name = LicenseApprovalStatus.QUERY_FIND_ALL_UNORDERED, query = "SELECT las FROM LicenseApprovalStatus las") })

@ToString(exclude = { "licenses" })
@EqualsAndHashCode(exclude = { "licenses" })
public class LicenseApprovalStatus {

    public static final String QUERY_FIND_ALL_UNORDERED = "LicenseApprovalStatus.findAllUnordered";

    public static final String IDX_NAME_LICENSE_STATUS_NAME = "idx_license_status_name";
    public static final String SEQUENCE_NAME = "license_apprstatus_id_seq";
    public static final String UC_NAME_LICENSE_STATUS_NAME = "uc_license_status_name";

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

    @OneToMany(mappedBy = "licenseApprovalStatus", orphanRemoval = false)
    @Getter
    @Setter
    private Set<License> licenses;

    public LicenseApprovalStatus() {
        this.licenses = new HashSet<License>();
    }

    public void addLicense(License license) {
        this.licenses.add(license);
    }

    public static class Builder {

        private Integer id;
        private String name;
        private Set<License> licenses;

        private Builder() {
            this.licenses = new HashSet<License>();
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

        public Builder licenses(Set<License> licenses) {
            this.licenses = licenses;
            return this;
        }

        public LicenseApprovalStatus build() {
            LicenseApprovalStatus licenseApprovalStatus = new LicenseApprovalStatus();
            licenseApprovalStatus.id = this.id;
            licenseApprovalStatus.setName(name);
            licenseApprovalStatus.setLicenses(licenses);

            // Set bi-directional mappings
            licenses.stream().forEach(license -> {
                license.setLicenseApprovalStatus(licenseApprovalStatus);
            });

            return licenseApprovalStatus;
        }
    }

}
