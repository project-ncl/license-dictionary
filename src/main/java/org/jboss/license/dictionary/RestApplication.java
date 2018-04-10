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
package org.jboss.license.dictionary;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.license.dictionary.endpoint.ConfigEndpoint;
import org.jboss.license.dictionary.endpoint.ExportEndpoint;
import org.jboss.license.dictionary.endpoint.ImportEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseDeterminationEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseHintEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseStatusEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectEcosystemEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectVersionLicenseEndpoint;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/10/17
 */
@ApplicationPath("/rest")
public class RestApplication extends Application {

    /*
     * KEEP IN SYNC WITH rest-config.service.ts
     */

    public static final String REST_VERS_1 = "/v1";

    public static final String CONFIG_ENDPOINT = "/config";
    public static final String EXPORT_ENDPOINT = "/export";
    public static final String IMPORT_ENDPOINT = "/import";

    public static final String LICENSE_STATUS_ENDPOINT = "/license-status";
    public static final String LICENSE_DETERMINATION_ENDPOINT = "/license-determination";
    public static final String LICENSE_HINT_ENDPOINT = "/license-hint";
    public static final String LICENSE_ENDPOINT = "/license";

    public static final String PROJECT_ENDPOINT = "/project";
    public static final String PROJECT_ECOSYSTEM_ENDPOINT = "/project-ecosystem";
    public static final String PROJECT_VERSION_LICENSE_ENDPOINT = "/project-version-license";

    public static final String IMPORT_ENDPOINT_IMPORT_LICENSE_API = "/licenses";
    public static final String IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API = "/licenses-alias";
    public static final String IMPORT_ENDPOINT_IMPORT_PROJECT_LICENSE_API = "/project-licenses";

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        addEndpoints(resources);
        return resources;
    }

    private void addEndpoints(Set<Class<?>> resources) {
        resources.add(ConfigEndpoint.class);
        resources.add(ImportEndpoint.class);
        resources.add(ExportEndpoint.class);
        resources.add(LicenseStatusEndpoint.class);
        resources.add(LicenseEndpoint.class);
        resources.add(LicenseDeterminationEndpoint.class);
        resources.add(LicenseHintEndpoint.class);
        resources.add(ProjectEndpoint.class);
        resources.add(ProjectVersionLicenseEndpoint.class);
        resources.add(ProjectEcosystemEndpoint.class);
    }
}
