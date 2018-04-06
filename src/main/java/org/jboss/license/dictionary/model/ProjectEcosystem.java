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

@Entity(name = "ProjectEcosystem")
@Table(name = "project_ecosystem", indexes = {
        @Index(name = ProjectEcosystem.IDX_NAME_PROJECT_ECOSYSTEM_NAME, columnList = "name") }, uniqueConstraints = {
                @UniqueConstraint(name = ProjectEcosystem.UC_NAME_PROJECT_ECOSYSTEM_NAME, columnNames = { "name" }) })
@NamedQueries({ @NamedQuery(name = ProjectEcosystem.QUERY_FIND_ALL_UNORDERED, query = "SELECT pe FROM ProjectEcosystem pe"),
        @NamedQuery(name = ProjectEcosystem.QUERY_FIND_BY_NAME_UNORDERED, query = "SELECT pe FROM ProjectEcosystem pe WHERE pe.name = :name") })

@ToString(exclude = { "projects" })
@EqualsAndHashCode(exclude = { "projects" })
public class ProjectEcosystem {

    public static final String QUERY_FIND_ALL_UNORDERED = "ProjectEcosystem.findAllUnordered";
    public static final String QUERY_FIND_BY_NAME_UNORDERED = "ProjectEcosystem.findByNameUnordered";

    public static final String IDX_NAME_PROJECT_ECOSYSTEM_NAME = "idx_project_ecosystem_name";
    public static final String SEQUENCE_NAME = "project_ecosystem_id_seq";
    public static final String UC_NAME_PROJECT_ECOSYSTEM_NAME = "uc_project_ecosystem_name";

    public static final String MAVEN = "mvn";
    public static final String NPM = "npm";
    public static final String NUGET = "nuget";
    public static final String PYPI = "pypi";
    public static final String GEM = "gem";
    public static final String GITHUB = "github";

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
    private String name;

    @OneToMany(mappedBy = "projectEcosystem", orphanRemoval = false)
    @Getter
    @Setter
    private Set<Project> projects;

    public ProjectEcosystem() {
        this.projects = new HashSet<Project>();
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public void setName(String name) {
        this.name = (name == null ? "" : name.toLowerCase());
    }

    public static class Builder {

        private Integer id;
        private String name;
        private Set<Project> projects;

        private Builder() {
            this.projects = new HashSet<Project>();
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

        public Builder projects(Set<Project> projects) {
            this.projects = projects;
            return this;
        }

        public ProjectEcosystem build() {
            ProjectEcosystem projectEcosystem = new ProjectEcosystem();
            projectEcosystem.id = this.id;
            projectEcosystem.setName(name);
            projectEcosystem.setProjects(projects);

            // Set bi-directional mappings
            projects.stream().forEach(project -> {
                project.setProjectEcosystem(projectEcosystem);
            });

            return projectEcosystem;
        }
    }

}
