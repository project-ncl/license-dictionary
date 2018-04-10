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
