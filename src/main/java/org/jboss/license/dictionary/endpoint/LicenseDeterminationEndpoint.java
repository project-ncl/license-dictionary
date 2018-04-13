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

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.license.dictionary.LicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.api.LicenseDeterminationTypeRest;
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
@Api(tags = { "License_Determination" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_DETERMINATION_ENDPOINT)
public class LicenseDeterminationEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseDeterminationEndpoint.class);

    @Inject
    private LicenseStore licenseStore;

    @ApiOperation(value = "Get all license determination type", response = LicenseDeterminationTypeRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @GET
    public Response getAllLicenseDeterminationType() {
        log.debug("Get all license determination type");

        List<LicenseDeterminationTypeRest> results = licenseStore.getAllLicenseDeterminationType();
        return paginated(results, results.size(), 0);
    }

    @ApiOperation(value = "Get a license determination type by id", response = LicenseDeterminationTypeRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "License determination type id not provided"),
            @ApiResponse(code = 404, message = "License determination type not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificLicenseDeterminationType(
            @ApiParam(value = "License determination type id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get license determination type with %d", id);

        if (id == null) {
            throw new BadRequestException("License determination type id must be provided");
        }

        LicenseDeterminationTypeRest entity = licenseStore.getLicenseDeterminationTypeById(id)
                .orElseThrow(() -> new NotFoundException("No license determination type found for id " + id));

        return Response.ok().entity(entity).build();
    }

    @ApiOperation(value = "Create a new license determination type", response = LicenseDeterminationTypeRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "License determination type successfully created") })
    @POST
    @Transactional
    public Response createNewLicenseDeterminationType(LicenseDeterminationTypeRest licenseDeterminationTypeRest,
            @Context UriInfo uriInfo) {
        log.debugf("Creating new license determination type %s", licenseDeterminationTypeRest);

        licenseDeterminationTypeRest = licenseStore.saveLicenseDeterminationType(licenseDeterminationTypeRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(licenseDeterminationTypeRest.getId())).entity(licenseDeterminationTypeRest)
                .build();
    }

}
