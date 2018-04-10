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

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = { "notes" })
@EqualsAndHashCode(exclude = { "notes" })
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
