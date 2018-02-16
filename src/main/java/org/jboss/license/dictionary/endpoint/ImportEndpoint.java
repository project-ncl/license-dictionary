package org.jboss.license.dictionary.endpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.imports.RhLicense;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import api.LicenseAliasRest;
import api.LicenseRest;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/13/17
 */
@Path(RestApplication.REST_VERS_1 + RestApplication.IMPORT_ENDPOINT)
public class ImportEndpoint {

    private static final Logger log = Logger.getLogger(ImportEndpoint.class);

    @Inject
    private LicenseStore store;

    @Path(RestApplication.IMPORT_ENDPOINT_IMPORT_LICENSE_API)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void importLicenses(Map<String, RhLicense> rhLicenses) {

        Multimap<String, LicenseRest> licensesByName = ArrayListMultimap.create();
        rhLicenses.forEach((alias, license) -> {

            // Pre-validating input so that the json import file can be fixed
            boolean valid = license.isValid();
            if (valid) {
                LicenseRest licenseData = license.toLicenseRest(alias);
                if (alias.startsWith("#")) {
                    log.debugf("skipping a commented license", licenseData);
                } else {
                    licensesByName.put(alias, licenseData);
                }
            }
        });

        isSingleName(licensesByName.asMap());

        Collection<LicenseRest> entities = licensesByName.asMap().values().stream().map(this::pickFirstEntry)
                .peek(LicenseRest::checkIntegrity).collect(Collectors.toList());

        store.replaceAllLicensesWith(entities);
    }

    @Path(RestApplication.IMPORT_ENDPOINT_IMPORT_LICENSE_ALIAS_API)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void importLicenseAliases(Map<String, String[]> rhAlias) {

        Map<String, Collection<LicenseAliasRest>> licensesAliasByName = new HashMap<String, Collection<LicenseAliasRest>>();
        rhAlias.forEach((alias, aliases) -> {

            try {
                LicenseRest licenseRest = pickLicenseByName(alias);
                List<LicenseAliasRest> licenseAliases = new ArrayList<LicenseAliasRest>();
                Arrays.stream(aliases).forEach(al -> {
                    licenseAliases
                            .add(LicenseAliasRest.Builder.newBuilder().licenseId(licenseRest.getId()).aliasName(al).build());
                });

                if (!licensesAliasByName.containsKey(alias)) {
                    licensesAliasByName.put(alias, licenseAliases);
                } else {
                    log.info("## multiple version of alias " + alias);
                }

            } catch (NoSuchElementException exc) {
                log.infof("No license found with alias %s", alias);
            }
        });

        store.replaceAllLicenseAliasesWith(licensesAliasByName);
    }

    private LicenseRest pickLicenseByName(String alias) {
        Optional<LicenseRest> optionalLicenseRest = store.getForFedoraName(alias);
        if (!optionalLicenseRest.isPresent()) {
            optionalLicenseRest = store.getForSpdxName(alias);
        }
        return optionalLicenseRest.get();
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

    private LicenseRest pickFirstEntry(Collection<LicenseRest> entries) {
        return entries.iterator().next();
    }

}
