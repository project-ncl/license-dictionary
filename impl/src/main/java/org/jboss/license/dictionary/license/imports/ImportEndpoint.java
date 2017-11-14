package org.jboss.license.dictionary.license.imports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.license.dictionary.license.LicenseEntity;
import org.jboss.license.dictionary.license.LicenseStore;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            List<LicenseEntity> entities = rhLicenses.entrySet()
                    .stream()
                    .skip(1) // skipping the header
                    .map(e -> e.getValue().toLicenseEntity(e.getKey()))
                    .map(this::fillDefaultValues)
                    .collect(Collectors.toList());
            store.replaceAllLicensesWith(entities);
        } catch (IOException e) {
            log.info("Error parsing licenses file", e);
            throw new BadRequestException("Unable to parse the licenses file: " + e.getMessage());
        }
    }

    private LicenseEntity fillDefaultValues(LicenseEntity licenseEntity) {
        licenseEntity.setUrl(
                firstNonNull(licenseEntity.getUrl(), licenseEntity.getSpdxUrl())
        );
        licenseEntity.setName(
                firstNonNull(licenseEntity.getName(), licenseEntity.getFedoraName(), licenseEntity.getSpdxName())
        );
        licenseEntity.setAbbreviation(
                firstNonNull(licenseEntity.getFedoraAbbrevation(), licenseEntity.getSpdxAbbreviation())
        );
        return licenseEntity;
    }

    private static <T> T firstNonNull(T... values) {
        return Arrays.stream(values)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
