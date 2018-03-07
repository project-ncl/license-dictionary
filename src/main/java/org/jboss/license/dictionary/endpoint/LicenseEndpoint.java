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

import static java.util.Collections.singletonList;
import static org.jboss.license.dictionary.utils.Mappers.fullMapper;
import static org.jboss.license.dictionary.utils.Mappers.licenseRestListType;
import static org.jboss.license.dictionary.utils.Mappers.limitedMapper;
import static org.jboss.license.dictionary.utils.ResponseUtils.valueOrNotFound;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.ErrorDto;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

import api.LicenseRest;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_ENDPOINT)
public class LicenseEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseEndpoint.class);

    @Inject
    private LicenseStore licenseStore;

    @GET
    public Response getLicenses(@QueryParam("fedoraName") String fedoraName, @QueryParam("spdxName") String spdxName,
            @QueryParam("code") String code, @QueryParam("nameAlias") String nameAlias,
            @QueryParam("searchTerm") String searchTerm, @QueryParam("count") Integer resultCount,
            @QueryParam("offset") Integer offset) {

        if (offset == null) {
            offset = 0;
        }

        long singleResultIndicatorCount = nonNullCount(fedoraName, spdxName, code, nameAlias);
        if (singleResultIndicatorCount > 1) {
            throw new BadRequestException(
                    "Not more than one query parameter " + "{fedoraName, spdxName, code, nameAlias} should be provided");
        }

        if (singleResultIndicatorCount > 0) {
            if (searchTerm != null) {
                throw new BadRequestException("searchTerm cannot be mixed "
                        + "with neither of {fedoraName, spdxName, code, nameAlias} query parameters");
            }

            LicenseRest entity;
            if (fedoraName != null) {
                entity = valueOrNotFound(licenseStore.getForFedoraName(fedoraName), "No license was found for Fedora name %s",
                        fedoraName);
            } else if (spdxName != null) {
                entity = valueOrNotFound(licenseStore.getForSpdxName(spdxName), "No license was found for SPDX name %s",
                        spdxName);
            } else if (nameAlias != null) {
                entity = valueOrNotFound(licenseStore.getForNameAlias(nameAlias), "Could not find license for nameAlias %s",
                        nameAlias);
            } else {
                entity = valueOrNotFound(licenseStore.getForCode(code), "Could not find license for code %s", code);
            }
            return paginated(singletonList(limitedMapper.map(entity, LicenseRest.class)), 1, 0);
        } else {
            List<LicenseRest> results;
            if (searchTerm != null) {
                results = licenseStore.findBySearchTerm(searchTerm).stream().collect(Collectors.toList());
            } else {
                results = licenseStore.getAll();
            }

            int totalCount = results.size();

            if (resultCount != null) {
                resultCount += offset;
                results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
            }

            List<LicenseRest> resultList = limitedMapper.map(results, licenseRestListType);
            return paginated(resultList, totalCount, offset);
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, LicenseRest license) {
        Optional<LicenseRest> maybeLicense = licenseStore.getById(id);
        LicenseRest licenseData = maybeLicense.orElseThrow(() -> new NotFoundException("No license found for id " + id));
        fullMapper.map(license, licenseData);

        licenseData = licenseStore.update(licenseData);
        return Response.ok().entity(licenseData).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        log.info("deleting license: " + id);
        if (!licenseStore.delete(id)) {
            throw new NotFoundException("No license found for id " + id);
        }
        return Response.ok().build();
    }

    @POST
    @Transactional
    public Response createNew(LicenseRest license, @Context UriInfo uriInfo) {
        log.info("creating license: " + license);
        // validate(license);
        // license.getTextUrl();// mstodo fetch content and set to entity
        license = licenseStore.save(license);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(license.getId())).entity(license).build();
    }

    @GET
    @Path("/{id}")
    public Response getSpecific(@PathParam("id") Integer id) {

        LicenseRest entity = licenseStore.getById(id).orElseThrow(() -> new NotFoundException("No license found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, LicenseRest.class)).build();
    }

    @PostConstruct
    public void init() {
        licenseStore.init();
    }

    // mstodo: this does not work!
    private void validate(LicenseRest license) {
        ErrorDto errors = new ErrorDto();
        licenseStore.getForFedoraName(license.getFedoraName()).ifPresent(
                l -> errors.addError("License with the same Fedora name found. Conflicting license id: %d", l.getId()));
        licenseStore.getForSpdxName(license.getSpdxName()).ifPresent(
                l -> errors.addError("License with the same SPDX name found. Conflicting license id: %d", l.getId()));
        licenseStore.getForCode(license.getCode())
                .ifPresent(l -> errors.addError("License with the same code found. Conflicting license id: %d", l.getId()));
        license.getAliasNames().forEach(alias -> licenseStore.getForNameAlias(alias).ifPresent(
                l -> errors.addError("License with the same name alias found. Conflicting license id: %d", l.getId())));
    }

    private long nonNullCount(Object... args) {
        return Stream.of(args).filter(Objects::nonNull).count();
    }
}
