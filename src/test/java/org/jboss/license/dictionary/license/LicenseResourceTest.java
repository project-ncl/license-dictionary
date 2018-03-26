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
package org.jboss.license.dictionary.license;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.license.dictionary.LicenseStatusStore;
import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.config.KeycloakConfig;
import org.jboss.license.dictionary.endpoint.LicenseEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseStatusEndpoint;
import org.jboss.license.dictionary.imports.RhLicense;
import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseAlias;
import org.jboss.license.dictionary.model.LicenseApprovalStatus;
import org.jboss.license.dictionary.model.LicenseDeterminationType;
import org.jboss.license.dictionary.model.LicenseHintType;
import org.jboss.license.dictionary.model.Project;
import org.jboss.license.dictionary.model.ProjectVersion;
import org.jboss.license.dictionary.model.ProjectVersionLicense;
import org.jboss.license.dictionary.model.ProjectVersionLicenseCheck;
import org.jboss.license.dictionary.model.ProjectVersionLicenseHint;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.ErrorDto;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import api.LicenseAliasRest;
import api.LicenseApprovalStatusRest;
import api.LicenseDeterminationTypeRest;
import api.LicenseHintTypeRest;
import api.LicenseRest;
import api.ProjectRest;
import api.ProjectVersionLicenseCheckRest;
import api.ProjectVersionLicenseHintRest;
import api.ProjectVersionLicenseRest;
import api.ProjectVersionRest;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/3/17
 */
@RunWith(Arquillian.class)
public class LicenseResourceTest {

    public static final String MY_LICENSE_URL = "http://example.com/license/text.txt";
    public static final String MY_LICENSE_NAME = "mylicense";

    @Inject
    private LicenseEndpoint licenseResource;

    @Inject
    private LicenseStatusEndpoint licenseStatusResource;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addPackage(License.class.getPackage())
                .addPackage(LicenseAlias.class.getPackage()).addPackage(LicenseApprovalStatus.class.getPackage())
                .addPackage(LicenseDeterminationType.class.getPackage()).addPackage(LicenseHintType.class.getPackage())
                .addPackage(Project.class.getPackage()).addPackage(ProjectVersion.class.getPackage())
                .addPackage(ProjectVersionLicense.class.getPackage()).addPackage(ProjectVersionLicenseCheck.class.getPackage())
                .addPackage(ProjectVersionLicenseHint.class.getPackage())

                .addPackage(LicenseRest.class.getPackage()).addPackage(LicenseAliasRest.class.getPackage())
                .addPackage(LicenseApprovalStatusRest.class.getPackage())
                .addPackage(LicenseDeterminationTypeRest.class.getPackage()).addPackage(LicenseHintTypeRest.class.getPackage())
                .addPackage(ProjectRest.class.getPackage()).addPackage(ProjectVersionRest.class.getPackage())
                .addPackage(ProjectVersionLicenseRest.class.getPackage())
                .addPackage(ProjectVersionLicenseCheckRest.class.getPackage())
                .addPackage(ProjectVersionLicenseHintRest.class.getPackage())

                .addPackage(LicenseEndpoint.class.getPackage()).addPackage(LicenseStore.class.getPackage())
                .addPackage(LicenseStatusEndpoint.class.getPackage()).addPackage(LicenseStatusStore.class.getPackage())
                .addPackage(KeycloakConfig.class.getPackage())

                .addPackage(ErrorDto.class.getPackage()).addPackage(RhLicense.class.getPackage())
                .addPackage(BadRequestException.class.getPackage()).addAsResource("META-INF/import.sql", "META-INF/import.sql")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml").addAsResource("project-test.yml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        PomEquippedResolveStage stage = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies();
        File[] libs = stage.resolve().withTransitivity().asFile();

        return webArchive.addAsLibraries(libs);
    }

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return new Swarm().withProfile("test");
    }

    @Before
    public void setUp() {

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusResource.getSpecific(1).getEntity();

        if (getLicenses(null, null, null, null, MY_LICENSE_NAME).isEmpty()) {
            LicenseRest license = LicenseRest.Builder.newBuilder().code(MY_LICENSE_NAME).fedoraName(MY_LICENSE_NAME)
                    .url(MY_LICENSE_URL).licenseApprovalStatus(APPROVED).build();

            license = (LicenseRest) licenseResource.createNew(license, getUriInfo()).getEntity();

            license.addAlias(null, "mylicense 1.0", license.getId());
            license.addAlias(null, "mylicense 1", license.getId());

            license = (LicenseRest) licenseResource.update(license.getId(), license).getEntity();
        }
    }

    @Test
    public void shouldGetLicenseByName() {
        LicenseRest mylicense = getLicenses(MY_LICENSE_NAME, null, null, null, null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    private List<LicenseRest> getLicenses(String fedoraName, String spdxName, String code, String nameAlias,
            String searchTerm) {
        return (List<LicenseRest>) licenseResource.getLicenses(fedoraName, spdxName, code, nameAlias, searchTerm, null, null)
                .getEntity();
    }

    @Test
    public void shouldGetLicenseByExactSearchTerm() {
        Collection<LicenseRest> licenses = getLicenses(null, null, null, null, "mylicense");
        assertThat(licenses).hasSize(1);
        LicenseRest mylicense = licenses.iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    @Test
    public void shouldGetLicenseBySubstringSearchTerm() {
        Collection<LicenseRest> licenses = getLicenses(null, null, null, null, "ylicense");
        assertThat(licenses).hasSize(1);
        LicenseRest mylicense = licenses.iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    @Test
    public void shouldGetLicenseById() {
        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusResource
                .getSpecific(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().fedoraName("licenseReadById").url("by-id.example.com")
                .code("licenseReadById").licenseApprovalStatus(APPROVED).build();

        license = (LicenseRest) licenseResource.createNew(license, getUriInfo()).getEntity();

        license.addAlias(null, "alias1", license.getId());
        license.addAlias(null, "alias2", license.getId());

        license = (LicenseRest) licenseResource.update(license.getId(), license).getEntity();

        LicenseRest resultLicense = (LicenseRest) licenseResource.getSpecific(license.getId()).getEntity();
        assertThat(resultLicense.getFedoraName()).isEqualTo(license.getFedoraName());
        assertThat(resultLicense.getUrl()).isEqualTo(license.getUrl());
        assertThat(resultLicense.getAliasNames()).hasSize(2);
        assertThat(resultLicense.getAliasNames()).containsExactlyInAnyOrder("alias1", "alias2");
    }

    @Test
    public void shouldNotDuplicateLicenseName() {
        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusResource
                .getSpecific(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().fedoraName(MY_LICENSE_NAME).url("by-id.example.com")
                .code("licenseReadById").licenseApprovalStatus(APPROVED).build();

        try {
            licenseResource.createNew(license, getUriInfo());
            fail("License should not be created with the same Fedora name");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("License with the same Fedora name found. Conflicting license");
        }
    }

    private UriInfo getUriInfo() {
        return new UriInfo() {

            @Override
            public URI resolve(URI arg0) {
                return null;
            }

            @Override
            public URI relativize(URI arg0) {
                return null;
            }

            @Override
            public UriBuilder getRequestUriBuilder() {
                return null;
            }

            @Override
            public URI getRequestUri() {
                try {
                    return new URI("http://localhost:8181/rest/v1/license");
                } catch (URISyntaxException e) {
                    return null;
                }
            }

            @Override
            public MultivaluedMap<String, String> getQueryParameters(boolean arg0) {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getQueryParameters() {
                return null;
            }

            @Override
            public List<PathSegment> getPathSegments(boolean arg0) {
                return null;
            }

            @Override
            public List<PathSegment> getPathSegments() {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getPathParameters(boolean arg0) {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getPathParameters() {
                return null;
            }

            @Override
            public String getPath(boolean arg0) {
                return null;
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public List<String> getMatchedURIs(boolean arg0) {
                return null;
            }

            @Override
            public List<String> getMatchedURIs() {
                return null;
            }

            @Override
            public List<Object> getMatchedResources() {
                return null;
            }

            @Override
            public UriBuilder getBaseUriBuilder() {
                return null;
            }

            @Override
            public URI getBaseUri() {
                return null;
            }

            @Override
            public UriBuilder getAbsolutePathBuilder() {
                return null;
            }

            @Override
            public URI getAbsolutePath() {
                return null;
            }
        };
    }
}