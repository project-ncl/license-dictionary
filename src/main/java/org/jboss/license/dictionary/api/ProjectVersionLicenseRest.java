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
