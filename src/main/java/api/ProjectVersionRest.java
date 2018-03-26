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
package api;

import lombok.Getter;
import lombok.Setter;

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
    private ProjectRest project;

    public ProjectVersionRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionRest projectVersionRest = (ProjectVersionRest) o;

        if (id != null ? !id.equals(projectVersionRest.id) : projectVersionRest.id != null)
            return false;
        if (scmUrl != null ? !scmUrl.equals(projectVersionRest.scmUrl) : projectVersionRest.scmUrl != null)
            return false;
        if (scmRevision != null ? !scmRevision.equals(projectVersionRest.scmRevision) : projectVersionRest.scmRevision != null)
            return false;
        if (project != null ? !project.equals(projectVersionRest.project) : projectVersionRest.project != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (scmUrl != null ? scmUrl.hashCode() : 0);
        result = 31 * result + (scmRevision != null ? scmRevision.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionRest{" + "id=" + id + ", scmUrl='" + scmUrl + '\'' + ", scmRevision='" + scmRevision + '\''
                + ", project='" + project + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String scmUrl;
        private String scmRevision;
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

        public Builder project(ProjectRest project) {
            this.project = project;
            return this;
        }

        public ProjectVersionRest build() {
            ProjectVersionRest projectVersionRest = new ProjectVersionRest();
            projectVersionRest.setId(id);
            projectVersionRest.setScmUrl(scmUrl);
            projectVersionRest.setScmRevision(scmRevision);
            projectVersionRest.setProject(project);
            return projectVersionRest;
        }
    }

}
