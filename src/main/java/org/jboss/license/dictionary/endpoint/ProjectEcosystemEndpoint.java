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

import static org.jboss.license.dictionary.utils.Mappers.fullMapper;

import java.util.List;

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

import org.jboss.license.dictionary.ProjectLicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.api.ProjectEcosystemRest;
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
@Api(tags = { "Project_Ecosystem" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.PROJECT_ECOSYSTEM_ENDPOINT)
public class ProjectEcosystemEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(ProjectEcosystemEndpoint.class);

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @ApiOperation(value = "Get all project ecosystems", response = ProjectEcosystemRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @GET
    public Response getAllProjectEcosystem() {
        log.debug("Get all project ecosystems ...");

        List<ProjectEcosystemRest> results = projectLicenseStore.getAllProjectEcosystem();
        return paginated(results, results.size(), 0);
    }

    @ApiOperation(value = "Get a project ecosystem by id", response = ProjectEcosystemRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project ecosystem id not provided"),
            @ApiResponse(code = 404, message = "Project ecosystem not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificProjectEcosystem(
            @ApiParam(value = "Project ecosystem id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get project ecosystem with %d", id);

        if (id == null) {
            throw new BadRequestException("Project ecosystem id must be provided");
        }

        ProjectEcosystemRest entity = projectLicenseStore.getProjectEcosystemById(id)
                .orElseThrow(() -> new NotFoundException("No project ecosystem found for id " + id));

        return Response.ok().entity(entity).build();
    }

    @ApiOperation(value = "Create a new project ecosystem", response = ProjectEcosystemRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project ecosystem successfully created") })
    @POST
    @Transactional
    public Response createNewProjectEcosystem(ProjectEcosystemRest projectEcosystemRest, @Context UriInfo uriInfo) {
        log.debugf("Creating new project ecosystems %s", projectEcosystemRest);

        projectEcosystemRest = projectLicenseStore.saveProjectEcosystem(projectEcosystemRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectEcosystemRest.getId())).entity(projectEcosystemRest).build();
    }

    @ApiOperation(value = "Get a project ecosystem by name", response = ProjectEcosystemRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project ecosystem name not provided"),
            @ApiResponse(code = 404, message = "Project ecosystem not found") })
    @GET
    public Response getProjectEcosystemByName(
            @ApiParam(value = "Project ecosystem name", required = true) @QueryParam("name") String name) {
        log.debugf("Finding ecosystem with name '%s'", name);

        if (name == null) {
            throw new BadRequestException("Project ecosystem name must be provided");
        }

        ProjectEcosystemRest entity = projectLicenseStore.getProjectEcosystemByName(name)
                .orElseThrow(() -> new NotFoundException("No ecosystem found for name '" + name + "'"));

        return Response.ok().entity(fullMapper.map(entity, ProjectEcosystemRest.class)).build();
    }

}
