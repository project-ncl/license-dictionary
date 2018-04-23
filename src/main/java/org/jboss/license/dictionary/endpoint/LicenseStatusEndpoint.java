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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.jboss.license.dictionary.api.LicenseApprovalStatusRest;
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Api(tags = { "License_Status" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_STATUS_ENDPOINT)
public class LicenseStatusEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseStatusEndpoint.class);

    @Inject
    private LicenseStore licenseStore;

    @ApiOperation(value = "Get all license approval status types", response = LicenseApprovalStatusRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Error processing RSQL query parameter") })
    @GET
    public Response getAllLicenseApprovalStatus(
            @ApiParam(value = "RSQL-type search query (e.g. name=='APPROVED')", required = false) @QueryParam("query") String query) {
        log.debugf("Get all license approval status with rsql search '%s'", query);

        try {

            List<LicenseApprovalStatusRest> results = licenseStore.getAllLicenseApprovalStatus(Optional.ofNullable(query));
            return paginated(results, results.size(), 0);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error processing RSQL query parameter");
        }
    }

    @ApiOperation(value = "Get a license approval status by id", response = LicenseApprovalStatusRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "License approval status id not provided"),
            @ApiResponse(code = 404, message = "License approval status not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificLicenseApprovalStatus(
            @ApiParam(value = "License approval status id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get license approval status with %d", id);

        if (id == null) {
            throw new BadRequestException("License approval status id must be provided");
        }

        LicenseApprovalStatusRest entity = licenseStore.getLicenseApprovalStatusById(id)
                .orElseThrow(() -> new NotFoundException("No license status found for id " + id));

        return Response.ok().entity(entity).build();
    }

    @ApiOperation(value = "Create a new license approval status", response = LicenseApprovalStatusRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "License approval status successfully created"),
            @ApiResponse(code = 400, message = "License approval status id not null") })
    @POST
    @Transactional
    public Response createNewLicenseApprovalStatus(LicenseApprovalStatusRest licenseApprovalStatusRest,
            @Context UriInfo uriInfo) {
        log.debugf("Creating new license approval status %s", licenseApprovalStatusRest);

        if (licenseApprovalStatusRest.getId() != null) {
            throw new BadRequestException("License approval status id must be null");
        }
        licenseApprovalStatusRest = licenseStore.saveLicenseApprovalStatus(licenseApprovalStatusRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(licenseApprovalStatusRest.getId())).entity(licenseApprovalStatusRest).build();
    }

}
