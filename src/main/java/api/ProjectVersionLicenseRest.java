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

public class ProjectVersionLicenseRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String scope;

    @Getter
    @Setter
    private LicenseRest license;

    @Getter
    @Setter
    private ProjectVersionLicenseCheckRest projectVersionLicenseCheck;

    public ProjectVersionLicenseRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseRest projectVersionLicenseRest = (ProjectVersionLicenseRest) o;

        if (id != null ? !id.equals(projectVersionLicenseRest.id) : projectVersionLicenseRest.id != null)
            return false;
        if (scope != null ? !scope.equals(projectVersionLicenseRest.scope) : projectVersionLicenseRest.scope != null)
            return false;
        if (license != null ? !license.equals(projectVersionLicenseRest.license) : projectVersionLicenseRest.license != null)
            return false;
        if (projectVersionLicenseCheck != null
                ? !projectVersionLicenseCheck.equals(projectVersionLicenseRest.projectVersionLicenseCheck)
                : projectVersionLicenseRest.projectVersionLicenseCheck != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (license != null ? license.hashCode() : 0);
        result = 31 * result + (projectVersionLicenseCheck != null ? projectVersionLicenseCheck.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseRest{" + "id=" + id + ", scope='" + scope + '\'' + ", license='" + license + '\''
                + ", projectVersionLicenseCheck='" + projectVersionLicenseCheck + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String scope;
        private LicenseRest license;
        private ProjectVersionLicenseCheckRest projectVersionLicenseCheck;

        private Builder() {
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

        public Builder license(LicenseRest license) {
            this.license = license;
            return this;
        }

        public Builder projectVersionLicenseCheck(ProjectVersionLicenseCheckRest projectVersionLicenseCheck) {
            this.projectVersionLicenseCheck = projectVersionLicenseCheck;
            return this;
        }

        public ProjectVersionLicenseRest build() {
            ProjectVersionLicenseRest projectVersionLicenseRest = new ProjectVersionLicenseRest();
            projectVersionLicenseRest.setId(id);
            projectVersionLicenseRest.setScope(scope);
            projectVersionLicenseRest.setLicense(license);
            projectVersionLicenseRest.setProjectVersionLicenseCheck(projectVersionLicenseCheck);
            return projectVersionLicenseRest;
        }
    }
}
