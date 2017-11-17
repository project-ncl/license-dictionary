package org.jboss.license.dictionary.license.imports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jboss.license.dictionary.api.FullLicenseData;
import org.jboss.license.dictionary.license.LicenseEntity;
import org.jboss.license.dictionary.license.LicenseStore;
import org.jboss.license.dictionary.utils.Mappers;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/13/17
 */
@Path("/import")
public class ImportEndpoint {

    private static final Logger log = Logger.getLogger(ImportEndpoint.class);

    @Inject
    private LicenseStore store;

    @Path("/licenses")
    @Consumes("text/plain")
    @POST
    public void importLicenses(String content) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, RhLicense> rhLicenses = mapper.readValue(content, new TypeReference<Map<String, RhLicense>>() {
            });

            Multimap<String, FullLicenseData> licensesByName = ArrayListMultimap.create();

            rhLicenses.forEach(
                    (alias, license) -> {
                        FullLicenseData licenseData = license.toFullLicenseData(alias);
                        licensesByName.put(license.getName(), licenseData);
                    }
            );

            verifyLicenseEntries(licensesByName.asMap());
            Collection<LicenseEntity> entities = licensesByName.asMap().values()
                    .stream()
                    .map(this::mergeEntries)
                    .map(l -> Mappers.fullMapper.map(l, LicenseEntity.class))
                    .collect(Collectors.toList());

            store.replaceAllLicensesWith(entities);
        } catch (IOException e) {
            log.info("Error parsing licenses file", e);
            throw new BadRequestException("Unable to parse the licenses file: " + e.getMessage());
        }
    }

    private FullLicenseData mergeEntries(Collection<FullLicenseData> entries) {
        FullLicenseData first = entries.iterator().next();
        Set<String> aliases = entries.stream()
                .flatMap(l -> l.getNameAliases().stream())
                .collect(Collectors.toSet());
        first.setNameAliases(aliases);
        return first;
    }

    private void verifyLicenseEntries(Map<String, Collection<FullLicenseData>> licenseDataByName) {
        licenseDataByName.values().stream()
                .flatMap(this::listConflictingAttributes)
                .collect(Collectors.joining("\n"));
    }

    private Stream<String> listConflictingAttributes(Collection<FullLicenseData> licenses) {
        FullLicenseData firstLicense = licenses.iterator().next();
        return licenses.stream()
                .skip(1)
                .map(l -> l.equals(firstLicense)
                        ? null
                        : "Licenses with the same name don't match: " + l + " is not equal to " + firstLicense + "."
                ).filter(Objects::nonNull);
    }

}
