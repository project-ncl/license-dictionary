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

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicense")
@Table(name = "project_version_license", indexes = {
        @Index(name = ProjectVersionLicense.IDX_NAME_PROJECT_VERSION_LICENSE_LICENSE, columnList = "license_id"),
        @Index(name = ProjectVersionLicense.IDX_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK, columnList = "proj_vers_license_check_id") }, uniqueConstraints = {
                @UniqueConstraint(name = ProjectVersionLicense.UC_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK_SCOPE, columnNames = {
                        "proj_vers_license_check_id", "license_id", "scope" }) })
@NamedQueries({
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_ALL_UNORDERED, query = "SELECT pvl FROM ProjectVersionLicense pvl"),
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_BY_PROJVERSLICCHECKID_UNORDERED, query = "SELECT DISTINCT pvl FROM ProjectVersionLicense pvl "
                + "JOIN FETCH pvl.license l JOIN FETCH l.aliases aliases "
                + "WHERE pvl.projectVersionLicenseCheck.id = :projVersLicCheckId"),
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_BY_LICENSEID_PROJVERSLICCHECKID_UNORDERED, query = "SELECT pvl FROM ProjectVersionLicense pvl "
                + "WHERE pvl.license.id = :licenseId AND pvl.projectVersionLicenseCheck.id = :projVersLicCheckId"),
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_BY_SCOPE_LICENSEID_PROJVERSLICCHECKID_UNORDERED, query = "SELECT pvl FROM ProjectVersionLicense pvl "
                + "WHERE pvl.scope = :scope AND pvl.license.id = :licenseId "
                + "AND pvl.projectVersionLicenseCheck.id = :projVersLicCheckId"),
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_UNORDERED, query = "SELECT DISTINCT pvl FROM ProjectVersionLicense pvl "
                + "JOIN FETCH pvl.license l JOIN FETCH l.aliases aliases "
                + "JOIN FETCH pvl.projectVersionLicenseCheck pvlc JOIN FETCH pvlc.projectVersion pv "
                + "JOIN FETCH pv.project p "
                + "WHERE p.projectEcosystem.name = :ecosystem AND p.key = :key AND pv.version = :vers"),
        @NamedQuery(name = ProjectVersionLicense.QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_SCOPE_UNORDERED, query = "SELECT DISTINCT pvl FROM ProjectVersionLicense pvl "
                + "JOIN FETCH pvl.license l JOIN FETCH l.aliases aliases "
                + "JOIN FETCH pvl.projectVersionLicenseCheck pvlc JOIN FETCH pvlc.projectVersion pv "
                + "JOIN FETCH pv.project p "
                + "WHERE p.projectEcosystem.name = :ecosystem AND p.key = :key AND pv.version = :vers AND pvl.scope = :scope") })

@ToString(exclude = { "projectVersionLicenseHints" })
@EqualsAndHashCode(exclude = { "projectVersionLicenseHints" })
public class ProjectVersionLicense {

    public static final String QUERY_FIND_ALL_UNORDERED = "ProjectVersionLicense.findAllUnordered";
    public static final String QUERY_FIND_BY_PROJVERSLICCHECKID_UNORDERED = "ProjectVersionLicense.findByProjVersLicCheckIdUnordered";
    public static final String QUERY_FIND_BY_SCOPE_LICENSEID_PROJVERSLICCHECKID_UNORDERED = "ProjectVersionLicense.findByLicIdProjVersLicCheckIdUnordered";
    public static final String QUERY_FIND_BY_LICENSEID_PROJVERSLICCHECKID_UNORDERED = "ProjectVersionLicense.findByScopeLicIdProjVersLicCheckIdUnordered";
    public static final String QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_UNORDERED = "ProjectVersionLicense.findByEcosystemProjKeyVersionUnordered";
    public static final String QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_SCOPE_UNORDERED = "ProjectVersionLicense.findByEcosystemProjKeyVersionScopeUnordered";

    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_LICENSE = "idx_projverlic_lic";
    public static final String IDX_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK = "idx_projverlic_projverlicchk";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_LICENSE = "fk_projverlic_lic";
    public static final String FK_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK = "fk_projverlic_projverlicchk";
    public static final String SEQUENCE_NAME = "project_version_license_id_seq";
    public static final String UC_NAME_PROJECT_VERSION_LICENSE_PROJECT_VERSION_LICENSE_CHECK_SCOPE = "uc_projverlic_projverlicchk_scope";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
    private Integer id;

    @Column(name = "scope")
    @Getter
    @Setter
    private String scope;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_VERSION_LICENSE_LICENSE))
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
