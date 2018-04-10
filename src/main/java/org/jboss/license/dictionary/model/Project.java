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
import javax.persistence.FetchType;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "Project")
@Table(name = "project", indexes = {
        @Index(name = Project.IDX_NAME_PROJECT_ECOSYSTEM_KEY, columnList = "project_ecosystem_id,key") }, uniqueConstraints = {
                @UniqueConstraint(name = Project.UC_NAME_PROJECT_ECOSYSTEM_KEY, columnNames = { "project_ecosystem_id",
                        "key" }) })
@NamedQueries({
        @NamedQuery(name = Project.QUERY_FIND_BY_ECOSYSTEM_KEY_UNORDERED, query = "SELECT p FROM Project p WHERE p.projectEcosystem.name = :ecosystem AND p.key = :key"),
        @NamedQuery(name = Project.QUERY_FIND_BY_ECOSYSTEM_UNORDERED, query = "SELECT p FROM Project p WHERE p.projectEcosystem.name = :ecosystem"),
        @NamedQuery(name = Project.QUERY_FIND_ALL_UNORDERED, query = "SELECT p FROM Project p") })

@ToString(exclude = { "projectVersions" })
@EqualsAndHashCode(exclude = { "projectVersions" })
public class Project {

    public static final String QUERY_FIND_ALL_UNORDERED = "Project.findAllUnordered";
    public static final String QUERY_FIND_BY_ECOSYSTEM_KEY_UNORDERED = "Project.findByEcosystemKeyUnordered";
    public static final String QUERY_FIND_BY_ECOSYSTEM_UNORDERED = "Project.findByEcosystemUnordered";

    public static final String SEQUENCE_NAME = "project_id_seq";
    public static final String IDX_NAME_PROJECT_ECOSYSTEM_KEY = "idx_project_ecosyskey";
    public static final String FK_NAME_PROJECT_PROJECT_ECOSYSTEM = "fk_project_projectecosystem";
    public static final String UC_NAME_PROJECT_ECOSYSTEM_KEY = "uc_project_ecosyskey";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
    private Integer id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "project_ecosystem_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_PROJECT_PROJECT_ECOSYSTEM))
    @Getter
    @Setter
    private ProjectEcosystem projectEcosystem;

    @NotNull
    @Column(name = "key")
    @Getter
    @Setter
    private String key;

    @OneToMany(mappedBy = "project")
    @Getter
    @Setter
    private Set<ProjectVersion> projectVersions;

    public Project() {
        this.projectVersions = new HashSet<ProjectVersion>();
    }

    public void addProjectVersion(ProjectVersion projectVersion) {
        this.projectVersions.add(projectVersion);
        projectVersion.setProject(this);
    }

    public static class Builder {

        private Integer id;
        private ProjectEcosystem projectEcosystem;
        private String key;
        private Set<ProjectVersion> projectVersions;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder ecosystem(ProjectEcosystem ecosystem) {
            this.projectEcosystem = ecosystem;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder projectVersions(Set<ProjectVersion> projectVersions) {
            this.projectVersions = projectVersions;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.setProjectEcosystem(projectEcosystem);
            project.setKey(key);
            project.setProjectVersions(projectVersions);

            // Set bi-directional mappings
            projectVersions.stream().forEach(projectVersion -> {
                projectVersion.setProject(project);
            });

            return project;
        }
    }

}
