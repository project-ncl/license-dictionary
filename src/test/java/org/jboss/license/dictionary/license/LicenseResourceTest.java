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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.license.dictionary.endpoint.LicenseEndpoint;
import org.jboss.license.dictionary.imports.RhLicense;
import org.jboss.license.dictionary.model.License;
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
import api.LicenseRest;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/3/17
 */
@RunWith(Arquillian.class)
public class LicenseResourceTest {

    public static final String MY_LICENSE_URL = "http://example.com/license/text.txt";
    public static final String MY_LICENSE_NAME = "mylicense";
    @Inject
    private LicenseEndpoint resource;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addPackage(License.class.getPackage())
                .addPackage(ErrorDto.class.getPackage()).addPackage(RhLicense.class.getPackage())
                .addPackage(LicenseRest.class.getPackage()).addPackage(BadRequestException.class.getPackage())
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
        if (getLicenses(null, null, null, null, MY_LICENSE_NAME).isEmpty()) {
            LicenseRest license = new LicenseRest();
            license.setFedoraName(MY_LICENSE_NAME);
            license.setUrl(MY_LICENSE_URL);
            license.getAliasNames().add("mylicense 1.0");
            license.getAliasNames().add("mylicense 1");

            resource.addLicense(license);
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
        return (List<LicenseRest>) resource.getLicenses(fedoraName, spdxName, code, nameAlias, searchTerm, null, null)
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
        LicenseRest license = new LicenseRest();
        license.setFedoraName("licenseReadById");
        license.setUrl("by-id.example.com");
        LicenseAliasRest lar1 = new LicenseAliasRest();
        lar1.setAliasName("alias1");
        LicenseAliasRest lar2 = new LicenseAliasRest();
        lar2.setAliasName("alias2");
        license.setAliases(new TreeSet<>(asList(lar1, lar2)));
        Integer id = resource.addLicense(license).getId();

        LicenseRest resultLicense = resource.getLicense(id);
        assertThat(resultLicense.getFedoraName()).isEqualTo(license.getFedoraName());
        assertThat(resultLicense.getUrl()).isEqualTo(license.getUrl());
        assertThat(resultLicense.getAliasNames()).hasSize(2);
        assertThat(resultLicense.getAliasNames()).containsExactlyInAnyOrder("alias1", "alias2");
    }
}