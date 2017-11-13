package org.jboss.license.dictionary.license;

import org.jboss.license.dictionary.api.License;
import org.jboss.license.dictionary.api.LicenseResource;
import org.jboss.license.dictionary.utils.ErrorDto;
import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.jboss.license.dictionary.utils.ResponseUtils.valueOrNotFound;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/30/17
 */
@Path(LicenseResource.LICENSES)
public class LicenseResourceImpl implements LicenseResource {

    private static final Logger log = Logger.getLogger(LicenseResourceImpl.class);

    private static final Type licenseListType = new TypeToken<List<License>>() {
    }.getType();

    private ModelMapper limitedMapper;
    private ModelMapper fullMapper;
    @Inject
    private LicenseStore licenseStore;

    public LicenseResourceImpl() {
        limitedMapper = new ModelMapper();
        limitedMapper.typeMap(LicenseEntity.class, License.class)
                .addMappings(mapping -> {
                    mapping.skip(License::setUrlAliases);
                    mapping.skip(License::setNameAliases);
                });

        fullMapper = new ModelMapper();
    }

    @Override
    public List<License> getLicenses(
            String name,
            String url,
            String nameAlias,
            String urlAlias,
            String searchTerm) {
        long singleResultIndicatorCount = nonNullCount(name, url, nameAlias, urlAlias);
        if (singleResultIndicatorCount > 1) {
            throw new ClientErrorException(
                    Response.status(SC_BAD_REQUEST)
                            .entity("Not more than one query parameter {name, url, nameAlias, urlAlias} should be provided")
                            .build()
            );
        }

        if (singleResultIndicatorCount > 0) {
            if (searchTerm != null) {
                throw new ClientErrorException(
                        Response.status(SC_BAD_REQUEST)
                                .entity("searchTerm cannot be mixed with neither of {name, url, nameAlias, urlAlias} query parameters")
                                .build()
                );
            }

            LicenseEntity entity;
            if (name != null) {
                entity = valueOrNotFound(licenseStore.getForName(name), "No license was found for name %s", name);
            } else if (url != null) {
                entity = valueOrNotFound(licenseStore.getForUrl(url), "No license was found for url %s", url);
            } else if (nameAlias != null) {
                entity = valueOrNotFound(licenseStore.getForNameAlias(nameAlias), "Could not find license for nameAlias %s", nameAlias);
            } else {
                entity = valueOrNotFound(licenseStore.getForUrlAlias(urlAlias), "Could not find license for urlAlias %s", urlAlias);
            }
            return Collections.singletonList(limitedMapper.map(entity, License.class));
        } else {
            List<LicenseEntity> results;
            if (searchTerm != null) {
                results = licenseStore.findBySearchTerm(searchTerm)
                        .stream().collect(Collectors.toList());
            } else {
                results = licenseStore.getAll();
            }
            return limitedMapper.map(results, licenseListType);
        }
    }

    @GET
    @Path("/{id}")
    public License getLicense(@PathParam("id") Integer licenseId) {
        LicenseEntity entity =
                licenseStore.getById(licenseId)
                        .orElseThrow(() -> new ClientErrorException(Response
                                        .status(SC_NOT_FOUND)
                                        .entity("No license found for id " + licenseId)
                                        .build()
                                )
                        );
        return fullMapper.map(entity, License.class);
    }

    @Override
    @Transactional
    public License addLicense(License license) {
        validate(license);
        license.getTextUrl();// mstodo fetch content and set to entity
        LicenseEntity entity = fullMapper.map(license, LicenseEntity.class);
        licenseStore.save(entity);
        return fullMapper.map(entity, License.class);
    }

    // mstodo: this does not work!
    private void validate(License license) {
        ErrorDto errors = new ErrorDto();
        licenseStore.getForName(license.getName())
                .ifPresent(
                        l -> errors.addError("License with the same name found. Conflicting license id: %d", l.getId())
                );
        licenseStore.getForUrl(license.getUrl())
                .ifPresent(
                        l -> errors.addError("License with the same url found. Conflicting license id: %d", l.getId())
                );

        license.getNameAliases().forEach(
                alias -> licenseStore
                        .getForNameAlias(alias)
                        .ifPresent(
                                l -> errors.addError("License with the same name alias found. Conflicting license id: %d", l.getId())
                        )

        );
        license.getUrlAliases().forEach(
                alias -> licenseStore.getForUrlAlias(alias)
                        .ifPresent(
                                l -> errors.addError("License with the same url alias found. Conflicting license id: %d", l.getId())
                        )

        );
    }

    private long nonNullCount(Object... args) {
        return Stream.of(args).filter(Objects::nonNull).count();
    }


}
