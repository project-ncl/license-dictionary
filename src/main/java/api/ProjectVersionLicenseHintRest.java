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

public class ProjectVersionLicenseHintRest {

    @Setter
    @Getter
    private Integer id;

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private ProjectVersionLicenseRest projectVersionLicense;

    @Getter
    @Setter
    private LicenseHintTypeRest licenseHintType;

    public ProjectVersionLicenseHintRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseHintRest projectVersionLicenseHintRest = (ProjectVersionLicenseHintRest) o;

        if (id != null ? !id.equals(projectVersionLicenseHintRest.id) : projectVersionLicenseHintRest.id != null)
            return false;
        if (value != null ? !value.equals(projectVersionLicenseHintRest.value) : projectVersionLicenseHintRest.value != null)
            return false;
        if (projectVersionLicense != null ? !projectVersionLicense.equals(projectVersionLicenseHintRest.projectVersionLicense)
                : projectVersionLicenseHintRest.projectVersionLicense != null)
            return false;
        if (licenseHintType != null ? !licenseHintType.equals(projectVersionLicenseHintRest.licenseHintType)
                : projectVersionLicenseHintRest.licenseHintType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (projectVersionLicense != null ? projectVersionLicense.hashCode() : 0);
        result = 31 * result + (licenseHintType != null ? licenseHintType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseHintRest{" + "id=" + id + ", value='" + value + '\'' + ", projectVersionLicense='"
                + projectVersionLicense + '\'' + ", licenseHintType='" + licenseHintType + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String value;
        private ProjectVersionLicenseRest projectVersionLicense;
        private LicenseHintTypeRest licenseHintType;

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

        public Builder projectVersionLicense(ProjectVersionLicenseRest projectVersionLicense) {
            this.projectVersionLicense = projectVersionLicense;
            return this;
        }

        public Builder licenseHintType(LicenseHintTypeRest licenseHintType) {
            this.licenseHintType = licenseHintType;
            return this;
        }

        public ProjectVersionLicenseHintRest build() {
            ProjectVersionLicenseHintRest projectVersionLicenseHintRest = new ProjectVersionLicenseHintRest();
            projectVersionLicenseHintRest.setId(id);
            projectVersionLicenseHintRest.setValue(value);
            projectVersionLicenseHintRest.setProjectVersionLicense(projectVersionLicense);
            projectVersionLicenseHintRest.setLicenseHintType(licenseHintType);

            return projectVersionLicenseHintRest;
        }
    }

}
