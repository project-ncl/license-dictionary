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
package org.jboss.license.dictionary.endpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.ProjectLicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.api.LicenseAliasRest;
import org.jboss.license.dictionary.api.LicenseRest;
import org.jboss.license.dictionary.imports.JsonLicense;
import org.jboss.license.dictionary.imports.JsonProjectLicense;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/13/17
 */
@Api(tags = { "Import" })
@Path(RestApplication.REST_VERS_1 + RestApplication.IMPORT_ENDPOINT)
public class ImportEndpoint {

    private static final Logger log = Logger.getLogger(ImportEndpoint.class);

    @Inject
    private LicenseStore store;

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @ApiOperation(value = "Import the license json file", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Path(RestApplication.IMPORT_ENDPOINT_IMPORT_LICENSE_API)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void importLicenses(Map<String, JsonLicense> rhLicenses) {
        log.debug("Import licenses ...");

        Multimap<String, LicenseRest> licensesByName = ArrayListMultimap.create();
        rhLicenses.forEach((alias, license) -> {

            // Pre-validating input so that the json import file can be fixed
            boolean valid = license.isValid();
            if (valid) {
                LicenseRest licenseData = license.toLicenseRest();
                if (alias.startsWith("#")) {
                    log.debugf("skipping a commented license", licenseData);
                } else {
                    licensesByName.put(alias, licenseData);
                }
            }
        });

        isSingleName(licensesByName.asMap());

        // Collection<LicenseRest> entities = licensesByName.asMap().values().stream().map(this::pickFirstEntry)
        // .peek(LicenseRest::checkIntegrity).collect(Collectors.toList());

        store.replaceAllLicensesWith(licensesByName.asMap());
    }

    @ApiOperation(value = "Import the license alias json file", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Path(RestApplication.IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void importLicenseAliases(Map<String, String[]> rhAlias) {
        log.debug("Import license aliases ...");

        Map<LicenseRest, Collection<LicenseRest>> primaryVsSecondaryLicensesMap = new HashMap<LicenseRest, Collection<LicenseRest>>();
        rhAlias.forEach((alias, aliases) -> {

            try {
                if (alias.startsWith("#")) {
                    log.debugf("skipping the commented alias: %s", alias);
                } else if (aliases == null || aliases.length == 0) {
                    log.debugf("skipping empty list of secondary aliases for primary alias: %s", alias);
                } else {

                    // Every license has been imported with an alias corresponding to its entry in the *url_rh_license.json file
                    Optional<LicenseRest> maybeLicense = findLicenseByAlias(alias);
                    if (maybeLicense.isPresent()) {

                        LicenseRest licenseRest = maybeLicense.get();
                        List<LicenseRest> secondaryLicenses = new ArrayList<LicenseRest>();

                        log.infof("LicenseRest --> findLicenseByAlias found: %s for primary alias: %s", licenseRest, alias);

                        Arrays.stream(aliases).forEach(secondaryAlias -> {
                            Optional<LicenseRest> secondaryLicense = findLicenseByAlias(secondaryAlias);
                            if (secondaryLicense.isPresent()) {
                                // Secondary alias with an entry in *url_rh_license.json file
                                secondaryLicenses.add(secondaryLicense.get());
                            } else {
                                log.infof("Secondary AliasName NOT FOUND!!! %s", secondaryAlias);
                                // Secondary alias without an entry in *url_rh_license.json file (can be e.g. a SPDX
                                // abbreviation)
                                LicenseRest fakeSecondaryLicense = LicenseRest.Builder.newBuilder().build();
                                fakeSecondaryLicense
                                        .addAlias(LicenseAliasRest.Builder.newBuilder().aliasName(secondaryAlias).build());
                                secondaryLicenses.add(fakeSecondaryLicense);
                            }
                        });

                        if (!secondaryLicenses.isEmpty()) {
                            primaryVsSecondaryLicensesMap.put(licenseRest, secondaryLicenses);
                        }
                    } else {
                        log.infof("Primary AliasName NOT FOUND!!! %s", alias);
                    }
                }
            } catch (NoSuchElementException exc) {
                log.infof("No license found with alias %s", alias);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        store.replaceAllLicenseAliasesWith(primaryVsSecondaryLicensesMap);
    }

    @ApiOperation(value = "Import the project icense json file", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Path(RestApplication.IMPORT_ENDPOINT_IMPORT_PROJECT_LICENSE_API)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void importProjectLicense(JsonProjectLicense[] jsonProjectLicenses) {
        log.debug("Import project license ...");

        projectLicenseStore.importProjectLicenses(jsonProjectLicenses);
    }

    private Optional<LicenseRest> findLicenseByAlias(String alias) {
        return store.getLicenseForNameAlias(alias);
    }

    private Optional<LicenseRest> findLicenseByName(String alias) {

        // Look for Fedora Name
        Optional<LicenseRest> optionalLicenseRest = store.getLicenseForFedoraName(alias);

        if (!optionalLicenseRest.isPresent()) {
            // Look for Fedora abbreviation
            optionalLicenseRest = store.getLicenseForFedoraAbbreviation(alias);
        }

        if (!optionalLicenseRest.isPresent()) {
            // Look for SPDX Name
            optionalLicenseRest = store.getLicenseForSpdxName(alias);
        }

        if (!optionalLicenseRest.isPresent()) {
            // Look for SPDX abbreviation
            optionalLicenseRest = store.getLicenseForSpdxAbbreviation(alias);
        }

        // Look for aliasName (for licenses with empty Fedora and SPDX names)
        if (!optionalLicenseRest.isPresent()) {
            optionalLicenseRest = store.getLicenseForNameAlias(alias);
        }

        return optionalLicenseRest;
    }

    private void isSingleName(Map<String, Collection<LicenseRest>> licenseDataByName) {
        licenseDataByName.keySet().stream().forEach(key -> {
            Collection<LicenseRest> licenses = licenseDataByName.get(key);
            if (licenses.size() > 1) {
                licenses.stream().forEach(lic -> {
                    log.info("## multiple version of name " + key + "found for license " + lic);
                });
                throw new BadRequestException("Duplicated names found ");
            }
        });
    }

}
