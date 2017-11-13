package org.jboss.license.dictionary.license;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.license.dictionary.api.License;
import org.jboss.license.dictionary.utils.ErrorDto;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/3/17
 */
@RunWith(Arquillian.class)
public class LicenseResourceTest {

    public static final String MY_LICENSE_URL = "http://example.com/license/text.txt";
    public static final String MY_LICENSE_NAME = "mylicense";
    @Inject
    private LicenseResourceImpl resource;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
                .addPackage(LicenseEntity.class.getPackage())
                .addPackage(ErrorDto.class.getPackage())
                .addPackage(License.class.getPackage())
                .addPackage(License.class.getPackage())
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        PomEquippedResolveStage stage = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies();
        File[] libs = stage.resolve().withTransitivity().asFile();


        return webArchive.addAsLibraries(libs);
    }


    @Before
    public void setUp() {

        if (resource.getLicenses(null, null, null, null, MY_LICENSE_NAME).isEmpty()) {
            License license = new License();
            license.setName(MY_LICENSE_NAME);
            license.setUrl(MY_LICENSE_URL);
            license.setContent("foffoofososfoasfoaosf");
            license.getNameAliases().add("mylicense 1.0");
            license.getNameAliases().add("mylicense 1");

            resource.addLicense(license);
        }
    }

    @Test
    public void shouldGetLicenseByName() {
        License mylicense = resource.getLicenses(MY_LICENSE_NAME, null, null, null, null).get(0);
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    @Test
    public void shouldGetLicenseByExactSearchTerm() {
        List<License> licenses = resource.getLicenses(null, null, null, null, "mylicense");
        assertThat(licenses).hasSize(1);
        License mylicense = licenses.get(0);
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    @Test
    public void shouldGetLicenseBySubstringSearchTerm() {
        List<License> licenses = resource.getLicenses(null, null, null, null, "ylicense");
        assertThat(licenses).hasSize(1);
        License mylicense = licenses.get(0);
        assertThat(mylicense).isNotNull();
        assertThat(mylicense.getUrl()).isEqualTo(MY_LICENSE_URL);
    }

    @Test
    public void shouldGetLicenseById() {
        License license = new License();
        license.setName("licenseReadById");
        license.setUrl("by-id.example.com");
        license.setNameAliases(new TreeSet<>(asList("alias1", "alias2")));
        Integer id = resource.addLicense(license).getId();

        License resultLicense = resource.getLicense(id);
        assertThat(resultLicense.getName()).isEqualTo(license.getName());
        assertThat(resultLicense.getUrl()).isEqualTo(license.getUrl());
        assertThat(resultLicense.getNameAliases()).hasSize(2);
        assertThat(resultLicense.getNameAliases()).containsExactlyInAnyOrder("alias1", "alias2");
    }
}