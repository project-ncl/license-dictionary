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
package org.jboss.license.dictionary.imports;

import api.FullLicenseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/13/17
 */
@Path("/import")
public class ImportEndpoint {

    private static final Logger log = Logger.getLogger(ImportEndpoint.class);

    @Inject
    private LicenseStore store;

    @Path("/licenses")
    @Consumes("application/json")
    @POST
    public void importLicenses(Map<String, RhLicense> rhLicenses) {
        ObjectMapper mapper = new ObjectMapper();
        // try {
        // Map<String, RhLicense> rhLicenses = mapper.readValue(content, new TypeReference<Map<String, RhLicense>>() {
        // });

        Multimap<String, FullLicenseData> licensesByName = ArrayListMultimap.create();

        rhLicenses.forEach((alias, license) -> {
            FullLicenseData licenseData = license.toFullLicenseData(alias);
            if (StringUtils.isBlank(licenseData.getName())) {
                throw new BadRequestException("Invalid license: " + licenseData);
            }
            if (licenseData.getName().startsWith("#")) {
                log.debugf("skipping a commented license", licenseData);
            } else {
                licensesByName.put(licenseData.getName(), licenseData);
            }
        });

        verifyLicenseEntries(licensesByName.asMap());
        Collection<FullLicenseData> entities = licensesByName.asMap().values().stream().map(this::mergeEntries)
                .peek(FullLicenseData::checkIntegrity).collect(Collectors.toList());
        
        /*
         * store.replaceAllLicensesWith(entities);
         */
        
        
        // } catch (IOException e) {
        // log.info("Error parsing licenses file", e);
        // throw new BadRequestException("Unable to parse the licenses file: " + e.getMessage());
        // }
    }

    private FullLicenseData mergeEntries(Collection<FullLicenseData> entries) {
        FullLicenseData first = entries.iterator().next();
        Set<String> aliases = entries.stream().flatMap(l -> l.getNameAliases().stream()).collect(Collectors.toSet());
        first.setNameAliases(aliases);
        return first;
    }

    private void verifyLicenseEntries(Map<String, Collection<FullLicenseData>> licenseDataByName) {
        String errors = licenseDataByName.values().stream().peek(this::merge).flatMap(this::listConflictingAttributes)
                .collect(Collectors.joining("\n"));
        log.errorf("Import errors found, the import results will be undeterministic for the mentioned cases: %s", errors);
        // mstodo bring the folowing back
        // throw new BadRequestException("Invalid data, errors: " + errors);
    }

    // mstodo remove
    private void merge(Collection<FullLicenseData> licenseData) {
        FullLicenseData first = licenseData.iterator().next();
        licenseData.stream().skip(1).forEach(other -> first.mergeFrom(other));
    }

    private Stream<String> listConflictingAttributes(Collection<FullLicenseData> licenses) {
        FullLicenseData firstLicense = licenses.iterator().next();
        return licenses.stream().skip(1)
                .map(l -> l.equals(firstLicense) ? null
                        : "Licenses with the same name don't match: " + l + " is not equal to " + firstLicense + ".")
                .filter(Objects::nonNull);
    }

}
