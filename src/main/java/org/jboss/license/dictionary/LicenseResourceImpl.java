package org.jboss.license.dictionary;

import api.FullLicenseData;
import api.License;
import api.LicenseResource;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.ErrorDto;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.jboss.license.dictionary.utils.Mappers.*;
import static org.jboss.license.dictionary.utils.ResponseUtils.valueOrNotFound;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/30/17
 */
@Path(LicenseResource.LICENSES)
@ApplicationScoped
public class LicenseResourceImpl implements LicenseResource {

    private static final Logger log = Logger.getLogger(LicenseResourceImpl.class);

    @Inject
    private LicenseStore licenseStore;

    @PostConstruct
    public void init() {
        licenseStore.init();
    }

    @Override
    public Response getLicenses(
            String name,
            String url,
            String nameAlias,
            String urlAlias,
            String searchTerm,
            Integer maxCount,
            Integer offset) {
        if (offset == null) {
            offset = 0;
        }

        long singleResultIndicatorCount = nonNullCount(name, url, nameAlias, urlAlias);
        if (singleResultIndicatorCount > 1) {
            throw new BadRequestException("Not more than one query parameter " +
                    "{name, url, nameAlias, urlAlias} should be provided");
        }

        if (singleResultIndicatorCount > 0) {
            if (searchTerm != null) {
                throw new BadRequestException("searchTerm cannot be mixed " +
                        "with neither of {name, url, nameAlias, urlAlias} query parameters");
            }

            FullLicenseData entity;
            if (name != null) {
                entity = valueOrNotFound(licenseStore.getForName(name), "No license was found for name %s", name);
            } else if (url != null) {
                entity = valueOrNotFound(licenseStore.getForUrl(url), "No license was found for url %s", url);
            } else if (nameAlias != null) {
                entity = valueOrNotFound(licenseStore.getForNameAlias(nameAlias), "Could not find license for nameAlias %s", nameAlias);
            } else {
                entity = valueOrNotFound(licenseStore.getForUrlAlias(urlAlias), "Could not find license for urlAlias %s", urlAlias);
            }
            return paginated(singletonList(limitedMapper.map(entity, License.class)), 1, 0);
        } else {
            List<FullLicenseData> results;
            if (searchTerm != null) {
                results = licenseStore.findBySearchTerm(searchTerm)
                        .stream().collect(Collectors.toList());
            } else {
                results = licenseStore.getAll();
            }

            int totalCount = results.size();

            if (maxCount != null) {
                maxCount += offset;
                results = results.subList(offset,
                        results.size() < maxCount
                                ? results.size()
                                : maxCount);
            }

            List<License> resultList = limitedMapper.map(results, licenseListType);
            return paginated(resultList, totalCount, offset);
        }
    }

    private static <T> Response paginated(T content, int totalCount, int offset) {
        return Response.ok().header("totalCount", totalCount)
                .header("offset", offset)
                .entity(content)
                .build();
    }

    @Override
    @Transactional
    public License updateLicense(Integer licenseId, FullLicenseData license) {
        Optional<FullLicenseData> maybeLicense = licenseStore.getById(licenseId);

        FullLicenseData licenseData = maybeLicense.orElseThrow(
                () -> new NotFoundException("No license found for id " + licenseId));
        fullMapper.map(license, licenseData);

        return limitedMapper.map(licenseData, License.class);
    }

    @Override
    public void deleteLicense(Integer licenseId) {
        log.info("deleting license: " + licenseId);
        if (!licenseStore.delete(licenseId)) {
            throw new NotFoundException("No license found for id " + licenseId);
        }
    }

    @Override
    public License getLicense(Integer licenseId) {
        FullLicenseData entity =
                licenseStore.getById(licenseId)
                        .orElseThrow(() -> new NotFoundException("No license found for id " + licenseId));
        return fullMapper.map(entity, License.class);
    }

    @Override
    @Transactional
    public License addLicense(FullLicenseData license) {
        validate(license);
        license.getTextUrl();// mstodo fetch content and set to entity
        return licenseStore.save(license);
    }

    // mstodo: this does not work!
    private void validate(FullLicenseData license) {
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
