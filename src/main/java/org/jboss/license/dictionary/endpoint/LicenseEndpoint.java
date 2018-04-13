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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.jboss.license.dictionary.api.LicenseRest;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;
import org.modelmapper.ValidationException;
import org.modelmapper.spi.ErrorMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Api(tags = { "License" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_ENDPOINT)
public class LicenseEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseEndpoint.class);

    @Inject
    private LicenseStore licenseStore;

    @ApiOperation(value = "Get licenses by Fedora name or SPDX name or code or alias, OR by generic search string", notes = "Finds licenses matching specifically Fedora, SPDX, alias, code OR any of them by providing a generic search string", response = LicenseRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "More than one among Fedora name, SPDX name, code or alias provided, or search term provided with Fedora name, SPDX name, code or alias"),
            @ApiResponse(code = 404, message = "License not found with Fedora name, SPDX name, code or alias provided") })
    @GET
    public Response getLicenses(
            @ApiParam(value = "License Fedora name", required = false) @QueryParam("fedoraName") String fedoraName,
            @ApiParam(value = "License Spdx name", required = false) @QueryParam("spdxName") String spdxName,
            @ApiParam(value = "License code name", required = false) @QueryParam("code") String code,
            @ApiParam(value = "License alias name", required = false) @QueryParam("nameAlias") String nameAlias,
            @ApiParam(value = "Generic seach term", required = false) @QueryParam("searchTerm") String searchTerm,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf(
                "Finding licenses with fedoraName '%s', spdxName '%s', code '%s', alias '%s', searchTerm '%s', numResults '%d', offset '%d'",
                fedoraName, spdxName, code, nameAlias, searchTerm, resultCount, offset);

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
                entity = valueOrNotFound(licenseStore.getLicenseForFedoraName(fedoraName),
                        "No license was found for Fedora name %s", fedoraName);
            } else if (spdxName != null) {
                entity = valueOrNotFound(licenseStore.getLicenseForSpdxName(spdxName), "No license was found for SPDX name %s",
                        spdxName);
            } else if (nameAlias != null) {
                entity = valueOrNotFound(licenseStore.getLicenseForNameAlias(nameAlias),
                        "Could not find license for nameAlias %s", nameAlias);
            } else {
                entity = valueOrNotFound(licenseStore.getLicenseForCode(code), "Could not find license for code %s", code);
            }
            return paginated(singletonList(limitedMapper.map(entity, LicenseRest.class)), 1, 0);
        } else {
            List<LicenseRest> results;
            if (searchTerm != null) {
                results = licenseStore.findBySearchTerm(searchTerm).stream().collect(Collectors.toList());
            } else {
                results = licenseStore.getAllLicense();
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

    @ApiOperation(value = "Update license by id", response = LicenseRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "License id not provided"),
            @ApiResponse(code = 404, message = "License to update not found") })
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateLicense(@ApiParam(value = "License id", required = true) @PathParam("id") Integer id,
            LicenseRest license) {
        log.debugf("Updating license with %d with entity %s", id, license);

        if (id == null) {
            throw new BadRequestException("License id must be provided");
        }

        Optional<LicenseRest> maybeLicense = licenseStore.getLicenseById(id);
        LicenseRest licenseData = maybeLicense.orElseThrow(() -> new NotFoundException("No license found for id " + id));
        fullMapper.map(license, licenseData);

        licenseData = licenseStore.updateLicense(licenseData);
        return Response.ok().entity(licenseData).build();
    }

    @ApiOperation(value = "Delete license by id", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "License id not provided") })
    @DELETE
    @Path("/{id}")
    public Response deleteLicense(@ApiParam(value = "License id", required = true) @PathParam("id") Integer id) {
        log.debugf("Deleting license with %d", id);

        if (id == null) {
            throw new BadRequestException("License id must be provided");
        }

        if (!licenseStore.deleteLicense(id)) {
            throw new NotFoundException("No license found for id " + id);
        }
        return Response.ok().build();
    }

    @ApiOperation(value = "Create a new license", response = LicenseRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "License successfully created") })
    @POST
    @Transactional
    public Response createNewLicense(LicenseRest license, @Context UriInfo uriInfo) {
        log.debugf("Creating new license %s", license);

        validate(license);
        license = licenseStore.saveLicense(license);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(license.getId())).entity(license).build();
    }

    @ApiOperation(value = "Get a license by id", response = LicenseRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "License id not provided"),
            @ApiResponse(code = 404, message = "License not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificLicense(@ApiParam(value = "License id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get license with %d", id);

        if (id == null) {
            throw new BadRequestException("License id must be provided");
        }

        LicenseRest entity = licenseStore.getLicenseById(id)
                .orElseThrow(() -> new NotFoundException("No license found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, LicenseRest.class)).build();
    }

    @PostConstruct
    public void init() {
        licenseStore.init();
    }

    private void validate(LicenseRest license) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

        if (license.getFedoraName() != null && !license.getFedoraName().isEmpty()) {
            licenseStore.getLicenseForFedoraName(license.getFedoraName()).ifPresent(l -> errors
                    .add(new ErrorMessage("License with the same Fedora name found. Conflicting license id: " + l.getId())));
        }

        if (license.getSpdxName() != null && !license.getSpdxName().isEmpty()) {
            licenseStore.getLicenseForSpdxName(license.getSpdxName()).ifPresent(l -> errors
                    .add(new ErrorMessage("License with the same SPDX name found. Conflicting license id: " + l.getId())));
        }

        if (license.getCode() != null && !license.getCode().isEmpty()) {
            licenseStore.getLicenseForCode(license.getCode()).ifPresent(l -> errors
                    .add(new ErrorMessage("License with the same code found. Conflicting license id: " + l.getId())));
        }
        license.getAliasNames().forEach(alias -> licenseStore.getLicenseForNameAlias(alias).ifPresent(l -> errors
                .add(new ErrorMessage("License with the same name alias found. Conflicting license id: " + l.getId()))));

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
