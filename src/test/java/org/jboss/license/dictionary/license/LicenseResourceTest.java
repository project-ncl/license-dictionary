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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.api.LicenseAliasRest;
import org.jboss.license.dictionary.api.LicenseApprovalStatusRest;
import org.jboss.license.dictionary.api.LicenseDeterminationTypeRest;
import org.jboss.license.dictionary.api.LicenseHintTypeRest;
import org.jboss.license.dictionary.api.LicenseRest;
import org.jboss.license.dictionary.api.ProjectEcosystemRest;
import org.jboss.license.dictionary.api.ProjectRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseCheckRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseHintRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseRest;
import org.jboss.license.dictionary.api.ProjectVersionRest;
import org.jboss.license.dictionary.config.KeycloakConfig;
import org.jboss.license.dictionary.endpoint.ImportEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseDeterminationEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseHintEndpoint;
import org.jboss.license.dictionary.endpoint.LicenseStatusEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectEcosystemEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectVersionEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectVersionLicenseCheckEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectVersionLicenseEndpoint;
import org.jboss.license.dictionary.endpoint.ProjectVersionLicenseHintEndpoint;
import org.jboss.license.dictionary.imports.JsonLicense;
import org.jboss.license.dictionary.imports.JsonProjectLicense;
import org.jboss.license.dictionary.imports.JsonProjectLicenseDeterminationHint;
import org.jboss.license.dictionary.imports.JsonProjectLicenseDeterminationType;
import org.jboss.license.dictionary.imports.JsonProjectSCMInfo;
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
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.license.dictionary.utils.QueryUtils;
import org.jboss.license.dictionary.utils.rsql.CustomizedJpaPredicateVisitor;
import org.jboss.license.dictionary.utils.rsql.CustomizedPredicateBuilder;
import org.jboss.license.dictionary.utils.rsql.CustomizedPredicateBuilderStrategy;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/3/17
 */
@RunWith(Arquillian.class)
public class LicenseResourceTest {

    // public static final String MY_LICENSE_URL = "http://example.com/license/text.txt";
    // public static final String MY_LICENSE_NAME = "mylicense";

    public static final String TEST_APACHE_20_FEDORA_ABBR = "TEST_ASL 2.0";
    public static final String TEST_APACHE_20_FEDORA_NAME = "TEST_Apache Software License 2.0";
    public static final String TEST_APACHE_20_SPDX_ABBR = "TEST_Apache-2.0";
    public static final String TEST_APACHE_20_SPDX_NAME = "TEST_Apache License 2.0";
    public static final String TEST_APACHE_20_URL = "TEST_http://www.apache.org/licenses/LICENSE-2.0";

    @Inject
    private LicenseEndpoint licenseEndpoint;

    @Inject
    private LicenseStatusEndpoint licenseStatusEndpoint;

    @Inject
    private LicenseDeterminationEndpoint licenseDeterminationEndpoint;

    @Inject
    private LicenseHintEndpoint licenseHintEndpoint;

    @Inject
    private ProjectEcosystemEndpoint projectEcosystemEndpoint;

    @Inject
    private ProjectEndpoint projectEndpoint;

    @Inject
    private ProjectVersionEndpoint projectVersionEndpoint;

    @Inject
    private ProjectVersionLicenseEndpoint projectVersionLicenseEndpoint;

    @Inject
    private ProjectVersionLicenseCheckEndpoint projectVersionLicenseCheckEndpoint;

    @Inject
    private ProjectVersionLicenseHintEndpoint projectVersionLicenseHintEndpoint;

    @Inject
    private ImportEndpoint importEndpoint;

    // @Drone
    // private WebDriver browser;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addPackage(License.class.getPackage())
                .addPackage(LicenseAlias.class.getPackage()).addPackage(LicenseApprovalStatus.class.getPackage())
                .addPackage(LicenseDeterminationType.class.getPackage()).addPackage(LicenseHintType.class.getPackage())
                .addPackage(Project.class.getPackage()).addPackage(ProjectVersion.class.getPackage())
                .addPackage(ProjectVersionLicense.class.getPackage()).addPackage(ProjectVersionLicenseCheck.class.getPackage())
                .addPackage(ProjectVersionLicenseHint.class.getPackage())

                .addPackage(CustomizedPredicateBuilder.class.getPackage())
                .addPackage(CustomizedPredicateBuilderStrategy.class.getPackage())
                .addPackage(CustomizedJpaPredicateVisitor.class.getPackage())

                .addPackage(LicenseRest.class.getPackage()).addPackage(LicenseAliasRest.class.getPackage())
                .addPackage(LicenseApprovalStatusRest.class.getPackage())
                .addPackage(LicenseDeterminationTypeRest.class.getPackage()).addPackage(LicenseHintTypeRest.class.getPackage())
                .addPackage(ProjectRest.class.getPackage()).addPackage(ProjectVersionRest.class.getPackage())
                .addPackage(ProjectVersionLicenseRest.class.getPackage())
                .addPackage(ProjectVersionLicenseCheckRest.class.getPackage())
                .addPackage(ProjectVersionLicenseHintRest.class.getPackage())

                .addPackage(JsonProjectLicense.class.getPackage())
                .addPackage(JsonProjectLicenseDeterminationHint.class.getPackage())
                .addPackage(JsonProjectLicenseDeterminationType.class.getPackage())
                .addPackage(JsonProjectSCMInfo.class.getPackage())

                .addPackage(LicenseEndpoint.class.getPackage()).addPackage(LicenseDeterminationEndpoint.class.getPackage())
                .addPackage(LicenseHintEndpoint.class.getPackage()).addPackage(LicenseStore.class.getPackage())
                .addPackage(LicenseStatusEndpoint.class.getPackage()).addPackage(ImportEndpoint.class.getPackage())
                .addPackage(KeycloakConfig.class.getPackage()).addPackage(ProjectEndpoint.class.getPackage())
                .addPackage(ProjectVersionEndpoint.class.getPackage())
                .addPackage(ProjectVersionLicenseEndpoint.class.getPackage())
                .addPackage(ProjectVersionLicenseCheckEndpoint.class.getPackage())
                .addPackage(ProjectVersionLicenseHintEndpoint.class.getPackage())
                .addPackage(ProjectEcosystemEndpoint.class.getPackage())

                .addPackage(ErrorDto.class.getPackage()).addPackage(JsonLicense.class.getPackage())
                .addPackage(BadRequestException.class.getPackage()).addPackage(QueryUtils.class.getPackage())
                .addAsResource("META-INF/import.sql", "META-INF/import.sql")
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml").addAsResource("project-test.yml")
                .addAsResource("test_import_license_aliases.json").addAsResource("test_import_url_rh_license.json")
                .addAsResource("test_project-licenses_full.json").addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

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

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(1).getEntity();

        if (getLicenses(null, null, null, null, TEST_APACHE_20_FEDORA_NAME).isEmpty()) {
            LicenseRest license = LicenseRest.Builder.newBuilder().code(TEST_APACHE_20_SPDX_ABBR)
                    .fedoraName(TEST_APACHE_20_FEDORA_NAME).fedoraAbbreviation(TEST_APACHE_20_FEDORA_ABBR)
                    .spdxName(TEST_APACHE_20_SPDX_NAME).spdxAbbreviation(TEST_APACHE_20_SPDX_ABBR).url(TEST_APACHE_20_URL)
                    .licenseApprovalStatus(APPROVED).build();

            license = (LicenseRest) licenseEndpoint.createNewLicense(license, getUriInfo()).getEntity();

            license.addAlias(null, "TEST_The Apache License, Version 2.0", license.getId());
            license.addAlias(null, "TEST_apache-2.0", license.getId());
            license.addAlias(null, "TEST_Apache License Version 2.0", license.getId());
            license.addAlias(null, "TEST_Apache Software License, Version 2.0", license.getId());
            license.addAlias(null, "TEST_Apache v2", license.getId());
            license.addAlias(null, "TEST_The Apache Software License, Version 2.0", license.getId());
            license.addAlias(null, "TEST_Apache License, Version 2.0", license.getId());
            license.addAlias(null, "TEST_Apache-2.0", license.getId());
            license.addAlias(null, "TEST_Apache Software License 2.0", license.getId());

            license = (LicenseRest) licenseEndpoint.updateLicense(license.getId(), license).getEntity();
        }
    }

    /*
     * Intervals for sequential ordering of tests
     * 
     * 000-99: LICENSE and LICENSE ALIAS
     * 
     * 100-149: LICENSE APPROVAL STATUS
     * 
     * 150-174: LICENSE DETERMINATION TYPE
     * 
     * 175-200: LICENSE HINT TYPE
     * 
     * 200-200: PROJECT ECOSYSTEM
     * 
     * 300-399: PROJECT
     * 
     * 400-499: PROJECT VERSION
     * 
     * 500-599: PROJECT VERSION LICENSE CHECK
     * 
     * 600-699: PROJECT VERSION LICENSE
     * 
     * 700-799: PROJECT VERSION LICENSE HINT
     * 
     * 1000-1100: IMPORT
     */

    /*
     * Examples of rsql queries: finds courses which...
     * 
     * /courses?query=code==MI-MDW - code matches MI-MDW
     * 
     * /courses?query=name==*services* - name contains "services"
     * 
     * /courses?query=name=='web services*' - name begins with "web services"
     * 
     * /courses?query=credits>3 - is for more than 3 credits
     * 
     * /courses?query=name==*web*;season==WINTER;(completion==CLFD_CREDIT,completion==CREDIT_EXAM) - name contains "web" and
     * season is "WINTER" and completion is CLFD_CREDIT or CREDIT_EXAM
     * 
     * /courses?query=department.id==18102 - is related with department id 18102
     * 
     * /courses?query=department.name==*engineering - is guaranteed by the department that name ends to "engineering"
     * 
     * /courses?query=name==*services*&orderBy=name&maxResults=50 - name contains "services", order by name and limit output to
     * maximum 50 results
     * 
     */

    @Test
    @InSequence(1)
    public void shouldGetLicenseByFedoraName() {
        System.out.println("=== shouldGetLicenseByFedoraName");

        LicenseRest mylicense = getLicenses(TEST_APACHE_20_FEDORA_NAME, null, null, null, null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(2)
    public void shouldGetLicenseBySpdxName() {
        System.out.println("=== shouldGetLicenseBySpdxName");

        LicenseRest mylicense = getLicenses(null, TEST_APACHE_20_SPDX_NAME, null, null, null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(3)
    public void shouldGetLicenseByCode() {
        System.out.println("=== shouldGetLicenseByCode");

        LicenseRest mylicense = getLicenses(null, null, TEST_APACHE_20_SPDX_ABBR, null, null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(4)
    public void shouldGetLicenseByFedoraAbbreviation() {
        System.out.println("=== shouldGetLicenseByFedoraAbbreviation");

        LicenseRest mylicense = getLicenses(null, null, null, null, TEST_APACHE_20_FEDORA_ABBR).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(5)
    public void shouldGetLicenseBySpdxAbbreviation() {
        System.out.println("=== shouldGetLicenseBySpdxAbbreviation");

        LicenseRest mylicense = getLicenses(null, null, null, null, TEST_APACHE_20_SPDX_ABBR).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(6)
    public void shouldGetLicenseByAliases() {
        System.out.println("=== shouldGetLicenseByAliases");

        LicenseRest mylicense = getLicenses(null, null, null, "TEST_The Apache License, Version 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_apache-2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache License Version 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache Software License, Version 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache v2", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_The Apache Software License, Version 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache License, Version 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache-2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

        mylicense = getLicenses(null, null, null, "TEST_Apache Software License 2.0", null).iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);

    }

    @SuppressWarnings("unchecked")
    private List<LicenseRest> getLicenses(String fedoraName, String spdxName, String code, String nameAlias,
            String searchTerm) {

        if (searchTerm != null && !searchTerm.isEmpty()) {
            return (List<LicenseRest>) licenseEndpoint.getAllLicense(null, searchTerm, null, null).getEntity();
        }

        String rsqlSearch = "";
        if (fedoraName != null && !fedoraName.isEmpty()) {
            rsqlSearch = "fedoraName=='" + QueryUtils.escapeReservedChars(fedoraName) + "'";
        }
        if (spdxName != null && !spdxName.isEmpty()) {
            rsqlSearch = "spdxName=='" + QueryUtils.escapeReservedChars(spdxName) + "'";
        }
        if (code != null && !code.isEmpty()) {
            rsqlSearch = "code=='" + QueryUtils.escapeReservedChars(code) + "'";
        }
        if (nameAlias != null && !nameAlias.isEmpty()) {
            rsqlSearch = "aliases.aliasName=='" + QueryUtils.escapeReservedChars(nameAlias) + "'";
        }

        return (List<LicenseRest>) licenseEndpoint.getAllLicense(rsqlSearch, null, null, null).getEntity();
    }

    @Test
    @InSequence(7)
    public void shouldGetLicenseByExactSearchTerm() {
        System.out.println("=== shouldGetLicenseByExactSearchTerm");

        Collection<LicenseRest> licenses = getLicenses(null, null, null, null, TEST_APACHE_20_FEDORA_NAME);
        assertThat(licenses).hasSize(1);
        LicenseRest mylicense = licenses.iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(8)
    public void shouldGetLicenseBySubstringSearchTerm() {
        System.out.println("=== shouldGetLicenseBySubstringSearchTerm");

        String subString = "*" + TEST_APACHE_20_FEDORA_NAME.substring(1, TEST_APACHE_20_FEDORA_NAME.length() - 1) + "*";

        Collection<LicenseRest> licenses = getLicenses(null, null, null, null, subString);
        assertThat(licenses).hasSize(1);
        LicenseRest mylicense = licenses.iterator().next();
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(TEST_APACHE_20_URL);
    }

    @Test
    @InSequence(9)
    public void shouldGetLicenseById() {
        System.out.println("=== shouldGetLicenseById");

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().fedoraName("licenseReadById").url("by-id.example.com")
                .code("licenseReadById").licenseApprovalStatus(APPROVED).build();

        license = (LicenseRest) licenseEndpoint.createNewLicense(license, getUriInfo()).getEntity();

        license.addAlias(null, "alias1", license.getId());
        license.addAlias(null, "alias2", license.getId());

        license = (LicenseRest) licenseEndpoint.updateLicense(license.getId(), license).getEntity();

        LicenseRest resultLicense = (LicenseRest) licenseEndpoint.getSpecificLicense(license.getId()).getEntity();
        assertThat(resultLicense.getFedoraName()).isEqualTo(license.getFedoraName());
        assertThat(resultLicense.getUrl()).isEqualTo(license.getUrl());
        assertThat(resultLicense.getAliasNames()).hasSize(2);
        assertThat(resultLicense.getAliasNames()).containsExactlyInAnyOrder("alias1", "alias2");
    }

    @Test
    @InSequence(10)
    public void shouldNotCreateDuplicatedLicenseName() {
        System.out.println("=== shouldNotCreateDuplicatedLicenseName");

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().fedoraName(TEST_APACHE_20_FEDORA_NAME).url("by-id.example.com")
                .code("licenseReadById").licenseApprovalStatus(APPROVED).build();

        try {
            licenseEndpoint.createNewLicense(license, getUriInfo());
            fail("License should not be created with the same Fedora name");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("License with the same Fedora name found. Conflicting license");
        }
    }

    @Test
    @InSequence(11)
    public void shouldNotPreventCreatingADuplicateEmptyFedoraLicenseName() {
        System.out.println("=== shouldNotPreventCreatingADuplicateEmptyFedoraLicenseName");

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().fedoraName("").url("by-id.example.com").code("code-1")
                .licenseApprovalStatus(APPROVED).build();

        try {
            licenseEndpoint.createNewLicense(license, getUriInfo());
        } catch (Exception e) {
            fail("License with empty Fedora name should be created");
        }

        license = LicenseRest.Builder.newBuilder().fedoraName("").url("by-id.example.com").code("code-2")
                .licenseApprovalStatus(APPROVED).build();

        try {
            licenseEndpoint.createNewLicense(license, getUriInfo());
        } catch (Exception e) {
            fail("Another license with empty Fedora name but different code should be created");
        }
    }

    @Test
    @InSequence(12)
    public void shouldNotPreventCreatingADuplicateEmptySpdxLicenseName() {
        System.out.println("=== shouldNotPreventCreatingADuplicateEmptySpdxLicenseName");

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().spdxName("").url("by-id.example.com").code("code-3")
                .licenseApprovalStatus(APPROVED).build();

        try {
            licenseEndpoint.createNewLicense(license, getUriInfo());
        } catch (Exception e) {
            fail("License with empty SPDX name should be created");
        }

        license = LicenseRest.Builder.newBuilder().spdxName("").url("by-id.example.com").code("code-4")
                .licenseApprovalStatus(APPROVED).build();

        try {
            licenseEndpoint.createNewLicense(license, getUriInfo());
        } catch (Exception e) {
            fail("Another license with empty SPDX name but different code should be created");
        }
    }

    @Test
    @InSequence(13)
    public void shouldDeleteLicenseById() {
        System.out.println("=== shouldDeleteLicenseById");

        LicenseApprovalStatusRest APPROVED = (LicenseApprovalStatusRest) licenseStatusEndpoint
                .getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId()).getEntity();
        LicenseRest license = LicenseRest.Builder.newBuilder().spdxName("TOBEDELETED").url("to-be-deleted.com")
                .code("TOBEDELETED").licenseApprovalStatus(APPROVED).build();

        try {
            Response response = licenseEndpoint.createNewLicense(license, getUriInfo());
            license = (LicenseRest) response.getEntity();
            assertThat(license.getId()).isNotNull();
        } catch (Exception e) {
            fail("License should have been created");
        }

        try {
            licenseEndpoint.deleteLicense(license.getId());
        } catch (Exception e) {
            fail("License should have been deleted");
        }

        try {
            licenseEndpoint.getSpecificLicense(license.getId());
            fail("License with id " + license.getId() + " should not be found");
        } catch (NotFoundException nfe) {
        }
    }

    @Test
    @InSequence(14)
    public void shouldUpdateLicense() {
        System.out.println("=== shouldUpdateLicense");

        LicenseRest license = getLicenses(null, null, "code-3", null, null).iterator().next();
        assertThat(license).isNotNull();
        Integer licenseId = license.getId();

        try {
            license.setCode("code-33");
            licenseEndpoint.updateLicense(licenseId, license).getEntity();
        } catch (NotFoundException nfe) {
            fail("Could not update license with id " + licenseId);
        }

        LicenseRest updatedLicense = getLicenses(null, null, "code-33", null, null).iterator().next();
        assertThat(updatedLicense).isNotNull();

        List<LicenseRest> results = getLicenses(null, null, "code-3", null, null);
        if (results != null && !results.isEmpty()) {
            fail("License with old code should not be found");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(100)
    public void shouldGetAllLicenseApprovalStatus() {
        System.out.println("=== shouldGetAllLicenseApprovalStatus");

        try {
            Response res = licenseStatusEndpoint.getAllLicenseApprovalStatus(null);
            assertThat(((List<LicenseApprovalStatusRest>) res.getEntity())).isNotNull();
            assertThat(((List<LicenseApprovalStatusRest>) res.getEntity()))
                    .hasAtLeastOneElementOfType(LicenseApprovalStatusRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any license approval status");
        }
    }

    @Test
    @InSequence(101)
    public void shouldGetLicenseApprovalStatusById() {
        System.out.println("=== shouldGetLicenseApprovalStatusById");

        try {
            Response res = licenseStatusEndpoint.getSpecificLicenseApprovalStatus(LicenseApprovalStatusRest.APPROVED.getId());
            assertThat(((LicenseApprovalStatusRest) res.getEntity()).getName())
                    .isEqualTo(LicenseApprovalStatusRest.APPROVED.getName());
        } catch (NotFoundException nfe) {
            fail("License approval status with id " + LicenseApprovalStatusRest.APPROVED.getId() + " could not be found");
        }
    }

    @Test
    @InSequence(102)
    public void shouldCreateLicenseApprovalStatus() {
        System.out.println("=== shouldCreateLicenseApprovalStatus");

        LicenseApprovalStatusRest licenseApprovalStatusRest = LicenseApprovalStatusRest.Builder.newBuilder()
                .name("PENDING_APPROVAL").build();

        try {
            licenseStatusEndpoint.createNewLicenseApprovalStatus(licenseApprovalStatusRest, getUriInfo());
        } catch (Exception e) {
            fail("License approval status could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(150)
    public void shouldGetAllLicenseDeterminationType() {
        System.out.println("=== shouldGetAllLicenseDeterminationType");

        try {
            Response res = licenseDeterminationEndpoint.getAllLicenseDeterminationType(null);
            assertThat(((List<LicenseDeterminationTypeRest>) res.getEntity())).isNotNull();
            assertThat(((List<LicenseDeterminationTypeRest>) res.getEntity()))
                    .hasAtLeastOneElementOfType(LicenseDeterminationTypeRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any license determination type");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(151)
    public void shouldGetLicenseDeterminationTypeById() {
        System.out.println("=== shouldGetLicenseDeterminationTypeById");

        try {
            List<LicenseDeterminationTypeRest> determinationTypes = (List<LicenseDeterminationTypeRest>) licenseDeterminationEndpoint
                    .getAllLicenseDeterminationType(null).getEntity();
            LicenseDeterminationTypeRest storedDetType = determinationTypes.get(0);

            LicenseDeterminationTypeRest detType = (LicenseDeterminationTypeRest) licenseDeterminationEndpoint
                    .getSpecificLicenseDeterminationType(storedDetType.getId()).getEntity();
            assertThat(storedDetType.getId()).isEqualTo(detType.getId());
            assertThat(storedDetType.getName()).isEqualTo(detType.getName());
            assertThat(storedDetType.getDescription()).isEqualTo(detType.getDescription());
        } catch (NotFoundException nfe) {
            fail("License determination type with specific id could not be found");
        }
    }

    @Test
    @InSequence(152)
    public void shouldCreateLicenseDeterminationType() {
        System.out.println("=== shouldCreateLicenseDeterminationType");

        LicenseDeterminationTypeRest licenseDeterminationTypeRest = LicenseDeterminationTypeRest.Builder.newBuilder()
                .name("FABRIC8 ANALYTICS FINDER")
                .description("This is the most waited for functionality from Fabric8 Analytics project").build();

        try {
            licenseDeterminationEndpoint.createNewLicenseDeterminationType(licenseDeterminationTypeRest, getUriInfo());
        } catch (Exception e) {
            fail("License determination type could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(175)
    public void shouldGetAllLicenseHintType() {
        System.out.println("=== shouldGetAllLicenseHintType");

        try {
            Response res = licenseHintEndpoint.getAllLicenseHintType(null);
            assertThat(((List<LicenseHintTypeRest>) res.getEntity())).isNotNull();
            assertThat(((List<LicenseHintTypeRest>) res.getEntity())).hasAtLeastOneElementOfType(LicenseHintTypeRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any license hint type");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(176)
    public void shouldGetLicenseHintTypeById() {
        System.out.println("=== shouldGetLicenseHintTypeById");

        try {
            List<LicenseHintTypeRest> hintTypes = (List<LicenseHintTypeRest>) licenseHintEndpoint.getAllLicenseHintType(null)
                    .getEntity();
            LicenseHintTypeRest storedHintType = hintTypes.get(0);

            LicenseHintTypeRest hintType = (LicenseHintTypeRest) licenseHintEndpoint
                    .getSpecificLicenseHintType(storedHintType.getId()).getEntity();
            assertThat(storedHintType.getId()).isEqualTo(hintType.getId());
            assertThat(storedHintType.getName()).isEqualTo(hintType.getName());
        } catch (NotFoundException nfe) {
            fail("License hint type with specific id could not be found");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(177)
    public void shouldGetLicenseHintTypeByName() {
        System.out.println("=== shouldGetLicenseHintTypeByName");

        try {
            List<LicenseHintTypeRest> hintTypes = (List<LicenseHintTypeRest>) licenseHintEndpoint.getAllLicenseHintType("")
                    .getEntity();
            LicenseHintTypeRest storedHintType = hintTypes.get(0);

            LicenseHintTypeRest hintType = ((List<LicenseHintTypeRest>) licenseHintEndpoint
                    .getAllLicenseHintType("name=='" + QueryUtils.escapeReservedChars(storedHintType.getName()) + "'")
                    .getEntity()).get(0);
            assertThat(storedHintType.getId()).isEqualTo(hintType.getId());
            assertThat(storedHintType.getName()).isEqualTo(hintType.getName());
        } catch (NotFoundException nfe) {
            fail("License hint type with specific name could not be found");
        }
    }

    @Test
    @InSequence(178)
    public void shouldCreateLicenseHintType() {
        System.out.println("=== shouldCreateLicenseHintType");

        LicenseHintTypeRest licenseHintTypeRest = LicenseHintTypeRest.Builder.newBuilder().name("*.spec").build();
        try {
            licenseHintEndpoint.createNewLicenseHintType(licenseHintTypeRest, getUriInfo());
        } catch (Exception e) {
            fail("License hint type could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(200)
    public void shouldGetAllProjectEcosystem() {
        System.out.println("=== shouldGetAllProjectEcosystem");

        try {
            Response res = projectEcosystemEndpoint.getAllProjectEcosystem(null);
            assertThat(((List<ProjectEcosystemRest>) res.getEntity())).isNotNull();
            assertThat(((List<ProjectEcosystemRest>) res.getEntity())).hasAtLeastOneElementOfType(ProjectEcosystemRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any project ecosystem");
        }
    }

    @Test
    @InSequence(201)
    public void shouldGetProjectEcosystemById() {
        System.out.println("=== shouldGetProjectEcosystemById");

        try {
            Response res = projectEcosystemEndpoint.getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId());
            assertThat(((ProjectEcosystemRest) res.getEntity()).getName()).isEqualTo(ProjectEcosystemRest.MAVEN.getName());
        } catch (NotFoundException nfe) {
            fail("Project ecosystem with id " + ProjectEcosystemRest.MAVEN.getId() + " could not be found");
        }
    }

    @Test
    @InSequence(202)
    public void shouldCreateProjectEcosystem() {
        System.out.println("=== shouldCreateProjectEcosystem");

        ProjectEcosystemRest projectEcosystemRest = ProjectEcosystemRest.Builder.newBuilder().name("COCOAPODS").build();
        try {
            projectEcosystemEndpoint.createNewProjectEcosystem(projectEcosystemRest, getUriInfo());
        } catch (Exception e) {
            fail("Project ecosystem could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(203)
    public void shouldGetProjectEcosystemByName() {
        System.out.println("=== shouldGetProjectEcosystemByName");

        try {
            Response res = projectEcosystemEndpoint.getAllProjectEcosystem(
                    "name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName()) + "'");
            List<ProjectEcosystemRest> projectEcosystemRests = (List<ProjectEcosystemRest>) res.getEntity();

            assertThat((projectEcosystemRests.get(0)).getId()).isEqualTo(ProjectEcosystemRest.MAVEN.getId());
        } catch (NotFoundException nfe) {
            fail("Project ecosystem with name " + ProjectEcosystemRest.MAVEN.getName() + " could not be found");
        }
    }

    @Test
    @InSequence(300)
    public void shouldCreateProject() {
        System.out.println("=== shouldCreateProject");

        ProjectEcosystemRest MAVEN = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("groupId:artifactId").ecosystem(MAVEN).build();

        try {
            projectEndpoint.createNewProject(projectRest, getUriInfo());
        } catch (Exception e) {
            fail("Project with ecosystem " + MAVEN.getName() + " could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(301)
    public void shouldGetAllProject() {
        System.out.println("=== shouldGetAllProject");

        try {
            Response res = projectEndpoint.getAllProject(null, 10, 0);
            assertThat(((List<ProjectRest>) res.getEntity())).isNotNull();
            assertThat(((List<ProjectRest>) res.getEntity())).hasAtLeastOneElementOfType(ProjectRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any project");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(302)
    public void shouldGetAllProjectByEcosystem() {
        System.out.println("=== shouldGetAllProjectByEcosystem");

        ProjectEcosystemRest NPM = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId()).getEntity();
        try {
            String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(NPM.getName()) + "'";
            Response res = projectEndpoint.getAllProject(rsqlQuery, 10, 0);
            assertThat(((List<ProjectRest>) res.getEntity())).isNotNull();
            assertThat(((List<ProjectRest>) res.getEntity())).hasAtLeastOneElementOfType(ProjectRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any project");
        }
    }

    @Test
    @InSequence(303)
    public void shouldGetProjectById() {
        System.out.println("=== shouldGetProjectById");

        ProjectEcosystemRest NPM = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.NPM.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("license-reporter").ecosystem(NPM).build();

        projectRest = (ProjectRest) projectEndpoint.createNewProject(projectRest, getUriInfo()).getEntity();

        ProjectRest resultProject = (ProjectRest) projectEndpoint.getSpecificProject(projectRest.getId()).getEntity();
        assertThat(resultProject.getKey()).isEqualTo("license-reporter");
        assertThat(resultProject.getProjectEcosystem().getName()).isEqualTo(NPM.getName());
    }

    @Test
    @InSequence(304)
    public void shouldDeleteProject() {
        System.out.println("=== shouldDeleteProject");

        ProjectEcosystemRest MAVEN = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("groupId:artifactId-toBeDeleted").ecosystem(MAVEN)
                .build();

        try {
            Response response = projectEndpoint.createNewProject(projectRest, getUriInfo());
            projectRest = (ProjectRest) response.getEntity();
            assertThat(projectRest.getId()).isNotNull();
        } catch (Exception e) {
            fail("Project should have been created");
        }

        try {
            projectEndpoint.deleteProject(projectRest.getId());
        } catch (Exception e) {
            fail("Project should have been deleted");
        }

        try {
            projectEndpoint.getSpecificProject(projectRest.getId());
            fail("Project with id " + projectRest.getId() + " should not be found");
        } catch (NotFoundException nfe) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(305)
    public void shouldUpdateProject() {
        System.out.println("=== shouldUpdateProject");

        ProjectEcosystemRest MAVEN = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("groupId:artifactId-toBeUpdated").ecosystem(MAVEN)
                .build();

        try {
            Response response = projectEndpoint.createNewProject(projectRest, getUriInfo());
            projectRest = (ProjectRest) response.getEntity();
            assertThat(projectRest.getId()).isNotNull();
        } catch (Exception e) {
            fail("Project should have been created");
        }

        try {
            projectRest.setKey("groupId:artifactId-Updated");
            projectEndpoint.updateProject(projectRest.getId(), projectRest);
        } catch (NotFoundException nfe) {
            fail("Could not update project with id " + projectRest.getId());
        }

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(MAVEN.getName()) + "';key=='"
                + QueryUtils.escapeReservedChars("groupId:artifactId-toBeUpdated") + "'";
        Response res = projectEndpoint.getAllProject(rsqlQuery, 10, 0);
        List<ProjectRest> projects = (List<ProjectRest>) res.getEntity();
        assertThat(projects).hasSize(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(306)
    public void shouldGetProjectByEcosystemKey() {
        System.out.println("=== shouldGetProjectByEcosystemKey");

        ProjectEcosystemRest NPM = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.NPM.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("validate-npm-package-license").ecosystem(NPM).build();

        projectRest = (ProjectRest) projectEndpoint.createNewProject(projectRest, getUriInfo()).getEntity();

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(NPM.getName()) + "';key=='"
                + QueryUtils.escapeReservedChars(projectRest.getKey()) + "'";
        Response res = projectEndpoint.getAllProject(rsqlQuery, 10, 0);
        List<ProjectRest> projects = (List<ProjectRest>) res.getEntity();
        assertThat(projects).hasSize(1);
        ProjectRest resultProject = projects.get(0);
        assertThat(resultProject.getKey()).isEqualTo("validate-npm-package-license");
        assertThat(resultProject.getProjectEcosystem().getName()).isEqualTo(NPM.getName());
    }

    @Test
    @InSequence(400)
    public void shouldCreateProjectVersion() {
        System.out.println("=== shouldCreateProjectVersion");

        ProjectEcosystemRest MAVEN = (ProjectEcosystemRest) projectEcosystemEndpoint
                .getSpecificProjectEcosystem(ProjectEcosystemRest.MAVEN.getId()).getEntity();
        ProjectRest projectRest = ProjectRest.Builder.newBuilder().key("org.jboss.pnc:parent").ecosystem(MAVEN).build();

        try {
            Response response = projectEndpoint.createNewProject(projectRest, getUriInfo());
            projectRest = (ProjectRest) response.getEntity();
            assertThat(projectRest.getId()).isNotNull();
        } catch (Exception e) {
            fail("Project should have been created");
        }

        ProjectVersionRest projectVersionRest = ProjectVersionRest.Builder.newBuilder().project(projectRest)
                .scmRevision("eabb17a4e4ac69232e3e3d4dc958b5c27f5ce53b").scmUrl("git@github.com:project-ncl/pnc.git")
                .version("1.4.0-SNAPSHOT").build();

        try {
            projectVersionEndpoint.createNewProjectVersion(projectVersionRest, getUriInfo());
        } catch (Exception e) {
            fail("Project version with url " + projectVersionRest.getScmUrl() + " could not be created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(401)
    public void shouldGetAllProjectVersion() {
        System.out.println("=== shouldGetAllProjectVersion");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        try {
            String rsqlQuery2 = "project.id==" + pncProject.getId();
            Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
            assertThat(((List<ProjectVersionRest>) res.getEntity())).isNotNull();
            assertThat(((List<ProjectVersionRest>) res.getEntity())).hasAtLeastOneElementOfType(ProjectVersionRest.class);
        } catch (NotFoundException nfe) {
            fail("Could not find any project version");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(402)
    public void shouldGetProjectVersionById() {
        System.out.println("=== shouldGetProjectVersionById");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
        List<ProjectVersionRest> projectVersionRest = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRest).isNotNull();
        assertThat(projectVersionRest).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRest.get(0);

        ProjectVersionRest projVers = (ProjectVersionRest) projectVersionEndpoint
                .getSpecificProjectVersion(projVersFromList.getId()).getEntity();
        assertThat(projVersFromList.getId()).isEqualTo(projVers.getId());
        assertThat(projVersFromList.getProject()).isEqualTo(projVers.getProject());
        assertThat(projVersFromList.getScmRevision()).isEqualTo(projVers.getScmRevision());
        assertThat(projVersFromList.getScmUrl()).isEqualTo(projVers.getScmUrl());
        assertThat(projVersFromList.getVersion()).isEqualTo(projVers.getVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(403)
    public void shouldGetProjectVersionByVersionProjectId() {
        System.out.println("=== shouldGetProjectVersionByVersionProjectId");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
        List<ProjectVersionRest> projectVersionRest = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRest).isNotNull();
        assertThat(projectVersionRest).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRest.get(0);

        String rsqlQuery3 = "project.id==" + pncProject.getId() + ";version=='"
                + QueryUtils.escapeReservedChars(projVersFromList.getVersion()) + "'";
        Response res2 = projectVersionEndpoint.getAllProjectVersion(rsqlQuery3, 10, 0);
        ProjectVersionRest projVers = ((List<ProjectVersionRest>) res2.getEntity()).get(0);

        assertThat(projVersFromList.getId()).isEqualTo(projVers.getId());
        assertThat(projVersFromList.getProject()).isEqualTo(projVers.getProject());
        assertThat(projVersFromList.getScmRevision()).isEqualTo(projVers.getScmRevision());
        assertThat(projVersFromList.getScmUrl()).isEqualTo(projVers.getScmUrl());
        assertThat(projVersFromList.getVersion()).isEqualTo(projVers.getVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(500)
    public void shouldCreateProjectVersionLicenseCheck() {
        System.out.println("=== shouldCreateProjectVersionLicenseCheck");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
        List<ProjectVersionRest> projectVersionRestList = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRestList).isNotNull();
        assertThat(projectVersionRestList).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRestList.get(0);

        Response res2 = licenseDeterminationEndpoint.getAllLicenseDeterminationType(null);
        List<LicenseDeterminationTypeRest> determinationTypeRestList = (List<LicenseDeterminationTypeRest>) res2.getEntity();
        assertThat(determinationTypeRestList).isNotNull();
        assertThat(determinationTypeRestList).hasAtLeastOneElementOfType(LicenseDeterminationTypeRest.class);

        LicenseDeterminationTypeRest licenseDeterminationTypeRest1 = determinationTypeRestList.get(0);
        LicenseDeterminationTypeRest licenseDeterminationTypeRest2 = determinationTypeRestList.get(1);

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest1 = ProjectVersionLicenseCheckRest.Builder.newBuilder()
                .projectVersion(projVersFromList).determinationDate(LocalDateTime.now())
                .determinedByUser("andrea.vibelli@gmail.com").licenseDeterminationType(licenseDeterminationTypeRest1).build();

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest2 = ProjectVersionLicenseCheckRest.Builder.newBuilder()
                .projectVersion(projVersFromList).determinationDate(LocalDateTime.now().minusDays(3))
                .determinedByUser("andrea.vibelli@gmail.com").licenseDeterminationType(licenseDeterminationTypeRest2).build();

        try {
            Response response1 = projectVersionLicenseCheckEndpoint
                    .createNewProjectVersionLicenseCheck(projectVersionLicenseCheckRest1, getUriInfo());
            projectVersionLicenseCheckRest1 = (ProjectVersionLicenseCheckRest) response1.getEntity();
            assertThat(projectVersionLicenseCheckRest1.getId()).isNotNull();

            Response response2 = projectVersionLicenseCheckEndpoint
                    .createNewProjectVersionLicenseCheck(projectVersionLicenseCheckRest2, getUriInfo());
            projectVersionLicenseCheckRest2 = (ProjectVersionLicenseCheckRest) response2.getEntity();
            assertThat(projectVersionLicenseCheckRest2.getId()).isNotNull();
        } catch (Exception e) {
            fail("Project version license checks should have been created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(501)
    public void shouldNotCreateProjectVersionLicenseCheckWithNotNullId() {
        System.out.println("=== shouldNotCreateProjectVersionLicenseCheckWithNotNullId");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
        List<ProjectVersionRest> projectVersionRestList = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRestList).isNotNull();
        assertThat(projectVersionRestList).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRestList.get(0);

        Response res2 = licenseDeterminationEndpoint.getAllLicenseDeterminationType(null);
        List<LicenseDeterminationTypeRest> determinationTypeRestList = (List<LicenseDeterminationTypeRest>) res2.getEntity();
        assertThat(determinationTypeRestList).isNotNull();
        assertThat(determinationTypeRestList).hasAtLeastOneElementOfType(LicenseDeterminationTypeRest.class);

        LicenseDeterminationTypeRest licenseDeterminationTypeRest = determinationTypeRestList.get(0);

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest = ProjectVersionLicenseCheckRest.Builder.newBuilder()
                .projectVersion(projVersFromList).determinationDate(LocalDateTime.now())
                .determinedByUser("andrea.vibelli@gmail.com").licenseDeterminationType(licenseDeterminationTypeRest).id(13)
                .build();

        try {
            projectVersionLicenseCheckEndpoint.createNewProjectVersionLicenseCheck(projectVersionLicenseCheckRest,
                    getUriInfo());
            fail("Project version license checks with not null id should not have been created");
        } catch (BadRequestException e) {

        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(502)
    public void shouldGetAllProjectVersionLicenseCheckByProjVersId() {
        System.out.println("=== shouldGetAllProjectVersionLicenseCheckByProjVersId");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);
        List<ProjectVersionRest> projectVersionRest = (List<ProjectVersionRest>) res.getEntity();
        ProjectVersionRest projVersFromList = projectVersionRest.get(0);

        String rsqlQuery3 = "project.id==" + projVersFromList.getProject().getId() + ";version=='"
                + QueryUtils.escapeReservedChars(projVersFromList.getVersion()) + "'";
        Response res2 = projectVersionEndpoint.getAllProjectVersion(rsqlQuery3, 10, 0);
        ProjectVersionRest projVers = ((List<ProjectVersionRest>) res2.getEntity()).get(0);

        String rsqlQuery4 = "projectVersion.id==" + projVers.getId();
        List<ProjectVersionLicenseCheckRest> projectVersionLicenseCheckRestList = (List<ProjectVersionLicenseCheckRest>) projectVersionLicenseCheckEndpoint
                .getAllProjectVersionLicenseCheck(rsqlQuery4, 100, 0).getEntity();

        assertThat(projectVersionLicenseCheckRestList).isNotNull();
        assertThat(projectVersionLicenseCheckRestList).hasAtLeastOneElementOfType(ProjectVersionLicenseCheckRest.class);

        List<LicenseDeterminationTypeRest> determinationTypeRestList = (List<LicenseDeterminationTypeRest>) licenseDeterminationEndpoint
                .getAllLicenseDeterminationType(null).getEntity();
        assertThat(determinationTypeRestList).isNotNull();
        assertThat(determinationTypeRestList).hasAtLeastOneElementOfType(LicenseDeterminationTypeRest.class);

        LicenseDeterminationTypeRest licenseDeterminationTypeRest = determinationTypeRestList.get(0);

        String rsqlQuery5 = "projectVersion.id==" + projVers.getId() + ";licenseDeterminationType.id=="
                + licenseDeterminationTypeRest.getId();
        List<ProjectVersionLicenseCheckRest> projectVersionLicenseCheckRestFilteredList = (List<ProjectVersionLicenseCheckRest>) projectVersionLicenseCheckEndpoint
                .getAllProjectVersionLicenseCheck(rsqlQuery5, 100, 0).getEntity();

        assertThat(projectVersionLicenseCheckRestFilteredList).isNotNull();
        assertThat(projectVersionLicenseCheckRestFilteredList).hasAtLeastOneElementOfType(ProjectVersionLicenseCheckRest.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(600)
    public void shouldCreateProjectVersionLicense() {
        System.out.println("=== shouldCreateProjectVersionLicense");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);

        List<ProjectVersionRest> projectVersionRestList = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRestList).isNotNull();
        assertThat(projectVersionRestList).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRestList.get(0);

        String rsqlQuery3 = "projectVersion.id==" + projVersFromList.getId();
        Response res3 = projectVersionLicenseCheckEndpoint.getAllProjectVersionLicenseCheck(rsqlQuery3, 100, 0);
        List<ProjectVersionLicenseCheckRest> projVersLicenseCheckRestList = (List<ProjectVersionLicenseCheckRest>) res3
                .getEntity();
        assertThat(projVersLicenseCheckRestList).isNotNull();
        assertThat(projVersLicenseCheckRestList).hasAtLeastOneElementOfType(ProjectVersionLicenseCheckRest.class);

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest1 = projVersLicenseCheckRestList.get(0);
        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest2 = projVersLicenseCheckRestList.get(1);

        String subString = "*" + TEST_APACHE_20_FEDORA_NAME.substring(1, TEST_APACHE_20_FEDORA_NAME.length() - 1) + "*";

        Collection<LicenseRest> licenses = getLicenses(null, null, null, null, subString);
        assertThat(licenses).hasSize(1);
        LicenseRest mylicense = licenses.iterator().next();

        ProjectVersionLicenseRest projectVersionLicenseRest1 = ProjectVersionLicenseRest.Builder.newBuilder().scope(null)
                .license(mylicense).projectVersionLicenseCheck(projectVersionLicenseCheckRest1).build();

        ProjectVersionLicenseRest projectVersionLicenseRest2 = ProjectVersionLicenseRest.Builder.newBuilder()
                .scope("org.jboss.pnc:rest-model").license(mylicense)
                .projectVersionLicenseCheck(projectVersionLicenseCheckRest2).build();

        try {
            Response response1 = projectVersionLicenseEndpoint.createNewProjectVersionLicense(projectVersionLicenseRest1,
                    getUriInfo());
            projectVersionLicenseRest1 = (ProjectVersionLicenseRest) response1.getEntity();
            assertThat(projectVersionLicenseRest1.getId()).isNotNull();

            Response response2 = projectVersionLicenseEndpoint.createNewProjectVersionLicense(projectVersionLicenseRest2,
                    getUriInfo());
            projectVersionLicenseRest2 = (ProjectVersionLicenseRest) response2.getEntity();
            assertThat(projectVersionLicenseRest2.getId()).isNotNull();
        } catch (Exception e) {
            fail("Project version license should have been created");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(601)
    public void shouldGetAllProjectVersionLicense() {
        System.out.println("=== shouldGetAllProjectVersionLicense");

        String rsqlQuery = "projectEcosystem.name=='" + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';key=='" + QueryUtils.escapeReservedChars("org.jboss.pnc:parent") + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest pncProject = projects.get(0);

        String rsqlQuery2 = "project.id==" + pncProject.getId();
        Response res = projectVersionEndpoint.getAllProjectVersion(rsqlQuery2, 10, 0);

        List<ProjectVersionRest> projectVersionRestList = (List<ProjectVersionRest>) res.getEntity();
        assertThat(projectVersionRestList).isNotNull();
        assertThat(projectVersionRestList).hasAtLeastOneElementOfType(ProjectVersionRest.class);

        ProjectVersionRest projVersFromList = projectVersionRestList.get(0);

        String rsqlQuery3 = "projectVersion.id==" + projVersFromList.getId();
        Response res3 = projectVersionLicenseCheckEndpoint.getAllProjectVersionLicenseCheck(rsqlQuery3, 100, 0);
        List<ProjectVersionLicenseCheckRest> projVersLicenseCheckRestList = (List<ProjectVersionLicenseCheckRest>) res3
                .getEntity();
        assertThat(projVersLicenseCheckRestList).isNotNull();
        assertThat(projVersLicenseCheckRestList).hasAtLeastOneElementOfType(ProjectVersionLicenseCheckRest.class);

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest1 = projVersLicenseCheckRestList.get(0);

        try {
            String rsql3 = "projectVersionLicenseCheck.id==" + projectVersionLicenseCheckRest1.getId();
            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql3, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense = results.get(0);

            Response response2 = projectVersionLicenseEndpoint.getSpecificProjectVersionLicense(projectVersionLicense.getId());
            ProjectVersionLicenseRest specificProjectVersionLicense = (ProjectVersionLicenseRest) response2.getEntity();

            assertThat(projectVersionLicense.getId()).isEqualTo(specificProjectVersionLicense.getId());
            assertThat(projectVersionLicense.getLicense()).isEqualTo(specificProjectVersionLicense.getLicense());
            assertThat(projectVersionLicense.getProjectVersionLicenseCheck())
                    .isEqualTo(specificProjectVersionLicense.getProjectVersionLicenseCheck());
            assertThat(projectVersionLicense.getScope()).isEqualTo(specificProjectVersionLicense.getScope());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Project version license should have been found");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(602)
    public void shouldGetProjectVersionLicensesWithEcosystemProjectKeyVersion() {
        System.out.println("=== shouldGetProjectVersionLicensesWithEcosystemProjectKeyVersion");

        String ecosystem = ProjectEcosystemRest.MAVEN.getName();
        String projectKey = "org.jboss.pnc:parent";
        String version = "1.4.0-SNAPSHOT";

        try {
            String rsql = "projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(ecosystem) + "';projectVersionLicenseCheck.projectVersion.project.key=='"
                    + QueryUtils.escapeReservedChars(projectKey) + "';projectVersionLicenseCheck.projectVersion.version=='"
                    + QueryUtils.escapeReservedChars(version) + "'";
            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense1 = results.get(0);
            ProjectVersionLicenseRest projectVersionLicense2 = results.get(1);

            assertThat(projectVersionLicense1.getProjectVersionLicenseCheck().getProjectVersion().getProject()
                    .getProjectEcosystem().getName()).isEqualTo(ecosystem);
            assertThat(projectVersionLicense1.getProjectVersionLicenseCheck().getProjectVersion().getProject().getKey())
                    .isEqualTo(projectKey);
            assertThat(projectVersionLicense1.getProjectVersionLicenseCheck().getProjectVersion().getVersion())
                    .isEqualTo(version);

            assertThat(projectVersionLicense1.getScope()).isIn(null, "org.jboss.pnc:rest-model");
            assertThat(projectVersionLicense2.getScope()).isIn(null, "org.jboss.pnc:rest-model");

        } catch (Exception e) {
            fail("Project version license with ecosystem, projectKey, version should have been found");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(603)
    public void shouldGetProjectVersionLicensesWithEcosystemProjectKeyVersionScope() {
        System.out.println("=== shouldGetProjectVersionLicensesWithEcosystemProjectKeyVersionScope");

        String ecosystem = ProjectEcosystemRest.MAVEN.getName();
        String projectKey = "org.jboss.pnc:parent";
        String version = "1.4.0-SNAPSHOT";

        try {
            String scope = "org.jboss.pnc:rest-model";
            String rsql = "scope=='" + QueryUtils.escapeReservedChars(scope)
                    + "';projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(ecosystem) + "';projectVersionLicenseCheck.projectVersion.project.key=='"
                    + QueryUtils.escapeReservedChars(projectKey) + "';projectVersionLicenseCheck.projectVersion.version=='"
                    + QueryUtils.escapeReservedChars(version) + "'";

            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results.size()).isEqualTo(1);
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense = results.get(0);

            assertThat(projectVersionLicense.getProjectVersionLicenseCheck().getProjectVersion().getProject()
                    .getProjectEcosystem().getName()).isEqualTo(ecosystem);
            assertThat(projectVersionLicense.getProjectVersionLicenseCheck().getProjectVersion().getProject().getKey())
                    .isEqualTo(projectKey);
            assertThat(projectVersionLicense.getProjectVersionLicenseCheck().getProjectVersion().getVersion())
                    .isEqualTo(version);
            assertThat(projectVersionLicense.getScope()).isEqualTo("org.jboss.pnc:rest-model");

        } catch (Exception e) {
            fail("Project version license with ecosystem, projectKey, version, scope should have been found");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(700)
    public void shouldNotCreateProjectVersionLicenseHintWithNonEmptyId() {
        System.out.println("=== shouldNotCreateProjectVersionLicenseHintWithEmptyProjectVersionLicenseId");

        String ecosystem = ProjectEcosystemRest.MAVEN.getName();
        String projectKey = "org.jboss.pnc:parent";
        String version = "1.4.0-SNAPSHOT";

        try {

            String rsql = "projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(ecosystem) + "';projectVersionLicenseCheck.projectVersion.project.key=='"
                    + QueryUtils.escapeReservedChars(projectKey) + "';projectVersionLicenseCheck.projectVersion.version=='"
                    + QueryUtils.escapeReservedChars(version) + "'";

            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense1 = results.get(0);

            Response hintTypesResponse = licenseHintEndpoint.getAllLicenseHintType(null);
            List<LicenseHintTypeRest> hintTypesList = (List<LicenseHintTypeRest>) hintTypesResponse.getEntity();

            assertThat(hintTypesList).isNotNull();
            assertThat(hintTypesList).hasAtLeastOneElementOfType(LicenseHintTypeRest.class);

            LicenseHintTypeRest licenseHintTypeRest1 = hintTypesList.get(0);

            ProjectVersionLicenseHintRest projectVersionLicenseHintRest1 = ProjectVersionLicenseHintRest.Builder.newBuilder()
                    .projectVersionLicense(projectVersionLicense1).licenseHintType(licenseHintTypeRest1).id(13).build();

            projectVersionLicenseHintEndpoint.createNewProjectVersionLicenseHint(projectVersionLicenseHintRest1, getUriInfo());

            fail("Should not have created project version license hint");

        } catch (BadRequestException e) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(701)
    public void shouldCreateProjectVersionLicenseHint() {
        System.out.println("=== shouldCreateProjectVersionLicenseHint");

        String ecosystem = ProjectEcosystemRest.MAVEN.getName();
        String projectKey = "org.jboss.pnc:parent";
        String version = "1.4.0-SNAPSHOT";

        try {
            String rsql = "projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(ecosystem) + "';projectVersionLicenseCheck.projectVersion.project.key=='"
                    + QueryUtils.escapeReservedChars(projectKey) + "';projectVersionLicenseCheck.projectVersion.version=='"
                    + QueryUtils.escapeReservedChars(version) + "'";

            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense1 = results.get(0);
            ProjectVersionLicenseRest projectVersionLicense2 = results.get(1);

            Response hintTypesResponse = licenseHintEndpoint.getAllLicenseHintType(null);
            List<LicenseHintTypeRest> hintTypesList = (List<LicenseHintTypeRest>) hintTypesResponse.getEntity();

            assertThat(hintTypesList).isNotNull();
            assertThat(hintTypesList).hasAtLeastOneElementOfType(LicenseHintTypeRest.class);

            LicenseHintTypeRest licenseHintTypeRest1 = hintTypesList.get(0);
            LicenseHintTypeRest licenseHintTypeRest2 = hintTypesList.get(1);

            ProjectVersionLicenseHintRest projectVersionLicenseHintRest1 = ProjectVersionLicenseHintRest.Builder.newBuilder()
                    .projectVersionLicense(projectVersionLicense1).licenseHintType(licenseHintTypeRest1).build();

            ProjectVersionLicenseHintRest projectVersionLicenseHintRest2 = ProjectVersionLicenseHintRest.Builder.newBuilder()
                    .projectVersionLicense(projectVersionLicense2).licenseHintType(licenseHintTypeRest2).value("LICENSE.TXT")
                    .build();

            projectVersionLicenseHintEndpoint.createNewProjectVersionLicenseHint(projectVersionLicenseHintRest1, getUriInfo());

            projectVersionLicenseHintEndpoint.createNewProjectVersionLicenseHint(projectVersionLicenseHintRest2, getUriInfo());

        } catch (Exception e) {
            fail("Could not create project version license hint");
        }
    }

    @Test
    @InSequence(702)
    public void shouldNotGetAllProjectVersionLicenseHint() {
        System.out.println("=== shouldNotGetAllProjectVersionLicenseHint");

        try {
            projectVersionLicenseHintEndpoint.getAllProjectVersionLicenseHint("fake.argument==1", 100, 0);
            fail("Should not get project version license hint");

        } catch (BadRequestException e) {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(703)
    public void shouldGetAllProjectVersionLicenseHint() {
        System.out.println("=== shouldGetAllProjectVersionLicenseHint");

        String ecosystem = ProjectEcosystemRest.MAVEN.getName();
        String projectKey = "org.jboss.pnc:parent";
        String version = "1.4.0-SNAPSHOT";

        try {
            String rsql = "projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(ecosystem) + "';projectVersionLicenseCheck.projectVersion.project.key=='"
                    + QueryUtils.escapeReservedChars(projectKey) + "';projectVersionLicenseCheck.projectVersion.version=='"
                    + QueryUtils.escapeReservedChars(version) + "'";

            Response response1 = projectVersionLicenseEndpoint.getAllProjectVersionLicense(rsql, null, 100, 0);

            List<ProjectVersionLicenseRest> results = (List<ProjectVersionLicenseRest>) response1.getEntity();
            assertThat(results).isNotNull();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results).hasAtLeastOneElementOfType(ProjectVersionLicenseRest.class);

            ProjectVersionLicenseRest projectVersionLicense1 = results.get(0);
            ProjectVersionLicenseRest projectVersionLicense2 = results.get(1);

            Response hintTypesResponse = licenseHintEndpoint.getAllLicenseHintType(null);
            List<LicenseHintTypeRest> hintTypesList = (List<LicenseHintTypeRest>) hintTypesResponse.getEntity();

            assertThat(hintTypesList).isNotNull();
            assertThat(hintTypesList).hasAtLeastOneElementOfType(LicenseHintTypeRest.class);

            LicenseHintTypeRest licenseHintTypeRest1 = hintTypesList.get(0);
            LicenseHintTypeRest licenseHintTypeRest2 = hintTypesList.get(1);

            String rsql1 = "projectVersionLicense.id==" + projectVersionLicense1.getId();
            List<ProjectVersionLicenseHintRest> projectVersLicHintRestListByProjVerLic = (List<ProjectVersionLicenseHintRest>) projectVersionLicenseHintEndpoint
                    .getAllProjectVersionLicenseHint(rsql1, 100, 0).getEntity();
            assertThat(projectVersLicHintRestListByProjVerLic).isNotNull();
            assertThat(projectVersLicHintRestListByProjVerLic.size()).isEqualTo(1);
            assertThat(projectVersLicHintRestListByProjVerLic).hasAtLeastOneElementOfType(ProjectVersionLicenseHintRest.class);
            ProjectVersionLicenseHintRest hintRest1 = projectVersLicHintRestListByProjVerLic.get(0);
            assertThat(projectVersionLicense1.getId()).isEqualTo(hintRest1.getProjectVersionLicense().getId());

            String rsql2 = "projectVersionLicense.id==" + projectVersionLicense1.getId() + ";licenseHintType.id=="
                    + licenseHintTypeRest1.getId();
            List<ProjectVersionLicenseHintRest> projectVersLicHintRestListByProjVerLicHint = (List<ProjectVersionLicenseHintRest>) projectVersionLicenseHintEndpoint
                    .getAllProjectVersionLicenseHint(rsql2, 100, 0).getEntity();

            assertThat(projectVersLicHintRestListByProjVerLicHint).isNotNull();
            assertThat(projectVersLicHintRestListByProjVerLicHint.size()).isEqualTo(1);
            assertThat(projectVersLicHintRestListByProjVerLicHint)
                    .hasAtLeastOneElementOfType(ProjectVersionLicenseHintRest.class);
            ProjectVersionLicenseHintRest hintRest2 = projectVersLicHintRestListByProjVerLicHint.get(0);
            assertThat(projectVersionLicense1.getId()).isEqualTo(hintRest2.getProjectVersionLicense().getId());
            assertThat(licenseHintTypeRest1.getId()).isEqualTo(hintRest2.getLicenseHintType().getId());

            String rsql3 = "projectVersionLicense.id==" + projectVersionLicense2.getId() + ";licenseHintType.id=="
                    + licenseHintTypeRest2.getId() + ";value=='" + QueryUtils.escapeReservedChars("LICENSE.TXT") + "'";

            List<ProjectVersionLicenseHintRest> projectVersLicHintRestListByProjVerLicHintValue = (List<ProjectVersionLicenseHintRest>) projectVersionLicenseHintEndpoint
                    .getAllProjectVersionLicenseHint(rsql3, 100, 0).getEntity();
            assertThat(projectVersLicHintRestListByProjVerLicHintValue).isNotNull();
            assertThat(projectVersLicHintRestListByProjVerLicHintValue.size()).isEqualTo(1);
            assertThat(projectVersLicHintRestListByProjVerLicHintValue)
                    .hasAtLeastOneElementOfType(ProjectVersionLicenseHintRest.class);
            ProjectVersionLicenseHintRest hintRest3 = projectVersLicHintRestListByProjVerLicHintValue.get(0);
            assertThat(projectVersionLicense2.getId()).isEqualTo(hintRest3.getProjectVersionLicense().getId());
            assertThat(licenseHintTypeRest2.getId()).isEqualTo(hintRest3.getLicenseHintType().getId());
            assertThat("LICENSE.TXT").isEqualTo(hintRest3.getValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not get project version license hint by project version license, license hynt type, value");
        }
    }

    @Test
    @InSequence(1000)
    public void shouldImportLicensesFromJsonFile() {
        System.out.println("=== shouldImportLicensesFromJsonFile");

        Map<String, JsonLicense> rhLicenses = null;

        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("test_import_url_rh_license.json");

            String jsonString = new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, JsonLicense>> typeRef = new TypeReference<HashMap<String, JsonLicense>>() {
            };
            rhLicenses = mapper.readValue(jsonString, typeRef);

            JsonLicense glideLicense = rhLicenses.get("3dfx Glide License");

            assertThat(glideLicense).isNotNull();
            assertThat(glideLicense.getApproved()).isEqualTo("yes");
            assertThat(glideLicense.getFedora_abbrev()).isEqualTo("Glide");
            assertThat(glideLicense.getFedora_name()).isEqualTo("3dfx Glide License");
            assertThat(glideLicense.getSpdx_abbrev()).isEqualTo("Glide");
            assertThat(glideLicense.getSpdx_name()).isEqualTo("3dfx Glide License");
            assertThat(glideLicense.getSpdx_license_url()).isEqualTo("https://spdx.org/licenses/Glide.html#licenseText");
            assertThat(glideLicense.getLicense_text_url())
                    .isEqualTo("http://rcm-guest.app.eng.bos.redhat.com/rcm-guest/staging/avibelli/licenses/Glide.txt");
            assertThat(glideLicense.getUrl()).isEqualTo("http://www.users.on.net/~triforce/glidexp/COPYING.txt");

        } catch (Exception e1) {
            fail("License array could not be converted from json string to Map<String, JsonLicense>");
        }

        try {
            importEndpoint.importLicenses(rhLicenses);
        } catch (Exception e1) {
            fail("Converted license list Map<String, JsonLicense from json file could not be imported");
        }

        LicenseRest storedGlideLicense = getLicenses("3dfx Glide License", null, null, null, null).iterator().next();
        assertThat(storedGlideLicense).isNotNull();
        assertThat(storedGlideLicense.getUrl()).isEqualTo("http://www.users.on.net/~triforce/glidexp/COPYING.txt");
    }

    @Test
    @InSequence(1010)
    public void shouldImportLicenseAliasesFromJsonFile() {
        System.out.println("=== shouldImportLicenseAliasesFromJsonFile");

        Map<String, String[]> rhAlias = null;

        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("test_import_license_aliases.json");

            String jsonString = new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String[]>> typeRef = new TypeReference<HashMap<String, String[]>>() {
            };
            rhAlias = mapper.readValue(jsonString, typeRef);

            String[] givenAliases = rhAlias.get("Apache License 1.1");
            assertThat(givenAliases).contains("Apache Software License 1.1", "Apache-1.1");

        } catch (Exception e1) {
            fail("License alias array could not be converted from json string to Map<String, String[]>");
        }

        try {
            importEndpoint.importLicenseAliases(rhAlias);
        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Converted license alias list Map<String, String[] from json file could not be imported");
        }

        LicenseRest storedApache11License = getLicenses(null, null, null, "Apache-1.1", null).iterator().next();
        assertThat(storedApache11License).isNotNull();
        assertThat(storedApache11License.getFedoraName()).isEqualTo("Apache Software License 1.1");
        assertThat(storedApache11License.getFedoraAbbreviation()).isEqualTo("ASL 1.1");
    }

    @Test
    @InSequence(1020)
    public void shouldImportProjectLicenseFromJson() {
        System.out.println("=== shouldImportProjectLicenseFromJson");

        JsonProjectLicense[] projectLicenses = null;
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("test_project-licenses_full.json");

            String jsonString = new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            projectLicenses = mapper.readValue(jsonString, JsonProjectLicense[].class);

            assertThat(projectLicenses).hasSize(6);

            assertThat(projectLicenses[0].getProject()).isEqualTo("antlr2:2.7.7");
            assertThat(projectLicenses[0].getRootPomGAV()).isEqualTo("antlr:antlr:2.7.7");
            assertThat(projectLicenses[0].getLicenseList()[0]).isEqualTo("The Antlr 2.7.7 License");
            assertThat(projectLicenses[0].getScmInfo()[0].getRevision()).isEqualTo("bd0ab97");

            assertThat(projectLicenses[1].getProject()).isEqualTo("asm:3.3.1");
            assertThat(projectLicenses[1].getRootPomGAV()).isEqualTo("asm:asm-project:3.3.1");
            assertThat(projectLicenses[1].getLicenseList()[0]).isEqualTo("The Asm BSD License");
            assertThat(projectLicenses[1].getScmInfo()[0].getRevision()).isEqualTo("83da518331f181dd1a764358308321b48f01fbcc");

        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Project license array could not be converted from json string to JsonProjectLicense[]");
        }

        try {
            importEndpoint.importProjectLicense(projectLicenses);
        } catch (Exception e1) {
            fail("Converted project license list JsonProjectLicense[] from json file could not be imported");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(1030)
    public void shouldHaveCorrectExpectedValuesAfterImport() {
        System.out.println("=== shouldHaveCorrectExpectedValuesAfterImport");

        String jaxbProjectKey = "com.sun.xml.bind.mvn:jaxb-parent";
        String jaxbProjectVersionName = "2.2.11";

        String rsqlQuery = "key=='" + QueryUtils.escapeReservedChars(jaxbProjectKey) + "'";
        List<ProjectRest> projects = (List<ProjectRest>) projectEndpoint.getAllProject(rsqlQuery, 10, 0).getEntity();
        ProjectRest jaxbProject = projects.get(0);

        assertThat(jaxbProject.getKey()).isEqualTo(jaxbProjectKey);
        assertThat(jaxbProject.getProjectEcosystem().getName()).isEqualTo(ProjectEcosystemRest.MAVEN.getName());

        String rsqlQuery2 = "project.id==" + jaxbProject.getId();
        List<ProjectVersionRest> jaxbProjectVersions = (List<ProjectVersionRest>) projectVersionEndpoint
                .getAllProjectVersion(rsqlQuery2, 1000, 0).getEntity();

        // Find specific project versions for the project jaxb
        String rsqlQuery3 = "project.id==" + jaxbProject.getId() + ";version=='"
                + QueryUtils.escapeReservedChars(jaxbProjectVersionName) + "'";
        Response res2 = projectVersionEndpoint.getAllProjectVersion(rsqlQuery3, 10, 0);
        ProjectVersionRest jaxbProjectVersion = ((List<ProjectVersionRest>) res2.getEntity()).get(0);

        // Find all project versions for the project jaxb
        jaxbProjectVersions.stream().forEach(projVers -> {
            System.out.println("++++ projectVersion found: " + projVers.getVersion());
        });

        // Assert that specific project version is contained in the found list
        assertThat(jaxbProjectVersions).contains(jaxbProjectVersion);

        // Find all the project version licenses for project jaxb
        String rsql = "projectVersionLicenseCheck.projectVersion.project.projectEcosystem.name=='"
                + QueryUtils.escapeReservedChars(ProjectEcosystemRest.MAVEN.getName())
                + "';projectVersionLicenseCheck.projectVersion.project.key=='" + QueryUtils.escapeReservedChars(jaxbProjectKey)
                + "';projectVersionLicenseCheck.projectVersion.version=='"
                + QueryUtils.escapeReservedChars(jaxbProjectVersionName) + "'";

        List<ProjectVersionLicenseRest> projectVersionLicenseRestList = (List<ProjectVersionLicenseRest>) projectVersionLicenseEndpoint
                .getAllProjectVersionLicense(rsql, null, 100, 0).getEntity();

        Set<String> mavenKeys = new HashSet<String>();
        Set<String> mavenScopes = new HashSet<String>();

        projectVersionLicenseRestList.stream().forEach(projVersLic -> {

            mavenKeys.add(projVersLic.getProjectVersionLicenseCheck().getProjectVersion().getProject().getKey());
            if (projVersLic.getScope() != null && !projVersLic.getScope().isEmpty()) {
                mavenScopes.add(projVersLic.getScope());
            }

            assertThat(projVersLic.getProjectVersionLicenseCheck().getLicenseDeterminationType().getId()).isIn(3, 5);

            String rsql4 = "projectVersionLicense.id==" + projVersLic.getId();
            List<ProjectVersionLicenseHintRest> projectVersionLicenseHintRests = (List<ProjectVersionLicenseHintRest>) projectVersionLicenseHintEndpoint
                    .getAllProjectVersionLicenseHint(rsql4, 100, 0).getEntity();

            projectVersionLicenseHintRests.stream().forEach(hint -> {

                assertThat(hint.getLicenseHintType().getId()).isIn(1, 2, 3);
            });
        });

        assertThat(mavenKeys).containsExactlyInAnyOrder("com.sun.xml.bind.mvn:jaxb-parent");
        assertThat(mavenScopes).containsExactlyInAnyOrder("com.sun.xml.bind.external:rngom");

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
