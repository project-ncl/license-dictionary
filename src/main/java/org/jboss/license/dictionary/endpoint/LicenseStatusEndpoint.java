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
import org.jboss.license.dictionary.api.LicenseApprovalStatusRest;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_STATUS_ENDPOINT)
public class LicenseStatusEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseStatusEndpoint.class);

    @Inject
    private LicenseStore licenseStore;

    @GET
    public Response getAllLicenseApprovalStatus() {
        log.debug("Get all license approval status");

        List<LicenseApprovalStatusRest> results = licenseStore.getAllLicenseApprovalStatus();
        return paginated(results, results.size(), 0);
    }

    @GET
    @Path("/{id}")
    public Response getSpecificLicenseApprovalStatus(@PathParam("id") Integer id) {
        log.debugf("Get license approval status with %d", id);

        LicenseApprovalStatusRest entity = licenseStore.getLicenseApprovalStatusById(id)
                .orElseThrow(() -> new NotFoundException("No license status found for id " + id));

        return Response.ok().entity(entity).build();
    }

    @POST
    @Transactional
    public Response createNewLicenseApprovalStatus(LicenseApprovalStatusRest licenseApprovalStatusRest, @Context UriInfo uriInfo) {
        log.debugf("Creating new license approval status %s", licenseApprovalStatusRest);

        licenseApprovalStatusRest = licenseStore.saveLicenseApprovalStatus(licenseApprovalStatusRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(licenseApprovalStatusRest.getId())).entity(licenseApprovalStatusRest).build();
    }

}
