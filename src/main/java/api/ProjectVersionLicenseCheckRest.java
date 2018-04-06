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

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class ProjectVersionLicenseCheckRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String determinedByUser;

    @Getter
    @Setter
    private LocalDateTime determinationDate;

    @Getter
    @Setter
    private String notes;

    @Getter
    @Setter
    private ProjectVersionRest projectVersion;

    @Getter
    @Setter
    private LicenseDeterminationTypeRest licenseDeterminationType;

    public ProjectVersionLicenseCheckRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest = (ProjectVersionLicenseCheckRest) o;

        if (id != null ? !id.equals(projectVersionLicenseCheckRest.id) : projectVersionLicenseCheckRest.id != null)
            return false;
        if (determinedByUser != null ? !determinedByUser.equals(projectVersionLicenseCheckRest.determinedByUser)
                : projectVersionLicenseCheckRest.determinedByUser != null)
            return false;
        if (determinationDate != null ? !determinationDate.equals(projectVersionLicenseCheckRest.determinationDate)
                : projectVersionLicenseCheckRest.determinationDate != null)
            return false;
        if (notes != null ? !notes.equals(projectVersionLicenseCheckRest.notes) : projectVersionLicenseCheckRest.notes != null)
            return false;
        if (projectVersion != null ? !projectVersion.equals(projectVersionLicenseCheckRest.projectVersion)
                : projectVersionLicenseCheckRest.projectVersion != null)
            return false;
        if (licenseDeterminationType != null
                ? !licenseDeterminationType.equals(projectVersionLicenseCheckRest.licenseDeterminationType)
                : projectVersionLicenseCheckRest.licenseDeterminationType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (determinedByUser != null ? determinedByUser.hashCode() : 0);
        result = 31 * result + (determinationDate != null ? determinationDate.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (projectVersion != null ? projectVersion.hashCode() : 0);
        result = 31 * result + (licenseDeterminationType != null ? licenseDeterminationType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseCheckRest{" + "id=" + id + ", determinedByUser='" + determinedByUser + '\''
                + ", determinationDate='" + determinationDate + '\'' + ", notes='" + notes + '\'' + ", projectVersion='"
                + projectVersion + '\'' + ", licenseDeterminationType='" + licenseDeterminationType + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String determinedByUser;
        private LocalDateTime determinationDate;
        private String notes;
        private ProjectVersionRest projectVersion;
        private LicenseDeterminationTypeRest licenseDeterminationType;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder determinedByUser(String determinedByUser) {
            this.determinedByUser = determinedByUser;
            return this;
        }

        public Builder determinationDate(LocalDateTime determinationDate) {
            this.determinationDate = determinationDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder projectVersion(ProjectVersionRest projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder licenseDeterminationType(LicenseDeterminationTypeRest licenseDeterminationType) {
            this.licenseDeterminationType = licenseDeterminationType;
            return this;
        }

        public ProjectVersionLicenseCheckRest build() {
            ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest = new ProjectVersionLicenseCheckRest();
            projectVersionLicenseCheckRest.setId(id);
            projectVersionLicenseCheckRest.setDeterminedByUser(determinedByUser);
            projectVersionLicenseCheckRest.setDeterminationDate(determinationDate);
            projectVersionLicenseCheckRest.setNotes(notes);
            projectVersionLicenseCheckRest.setProjectVersion(projectVersion);
            projectVersionLicenseCheckRest.setLicenseDeterminationType(licenseDeterminationType);

            return projectVersionLicenseCheckRest;
        }
    }

}
