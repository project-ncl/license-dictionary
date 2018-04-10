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
package org.jboss.license.dictionary.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ProjectVersionRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String scmUrl;

    @Getter
    @Setter
    private String scmRevision;

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    private ProjectRest project;

    public ProjectVersionRest() {
    }

    public static class Builder {

        private Integer id;
        private String scmUrl;
        private String scmRevision;
        private String version;
        private ProjectRest project;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder scmUrl(String scmUrl) {
            this.scmUrl = scmUrl;
            return this;
        }

        public Builder scmRevision(String scmRevision) {
            this.scmRevision = scmRevision;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder project(ProjectRest project) {
            this.project = project;
            return this;
        }

        public ProjectVersionRest build() {
            ProjectVersionRest projectVersionRest = new ProjectVersionRest();
            projectVersionRest.setId(id);
            projectVersionRest.setScmUrl(scmUrl);
            projectVersionRest.setScmRevision(scmRevision);
            projectVersionRest.setVersion(version);
            projectVersionRest.setProject(project);
            return projectVersionRest;
        }
    }

}
