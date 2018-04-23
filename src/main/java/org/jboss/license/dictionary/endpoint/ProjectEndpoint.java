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
import static org.jboss.license.dictionary.utils.Mappers.limitedMapper;
import static org.jboss.license.dictionary.utils.Mappers.projectRestListType;

import java.util.List;
import java.util.Optional;

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

import org.jboss.license.dictionary.ProjectLicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.api.ProjectRest;
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
@Api(tags = { "Project" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.PROJECT_ENDPOINT)
public class ProjectEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(ProjectEndpoint.class);

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @ApiOperation(value = "Create a new project", response = ProjectRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project successfully created"),
            @ApiResponse(code = 400, message = "Project id not null") })
    @POST
    @Transactional
    public Response createNewProject(ProjectRest project, @Context UriInfo uriInfo) {
        log.debugf("Creating new project %s", project);

        if (project.getId() != null) {
            throw new BadRequestException("Project id must be null");
        }
        project = projectLicenseStore.saveProject(project);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(project.getId())).entity(project).build();
    }

    @ApiOperation(value = "Update project by id", response = ProjectRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id not provided"),
            @ApiResponse(code = 404, message = "Project to update not found") })
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateProject(@ApiParam(value = "Project id", required = true) @PathParam("id") Integer id,
            ProjectRest project) {
        log.debugf("Updating project with %d with entity %s", id, project);

        if (id == null) {
            throw new BadRequestException("Project id must be provided");
        }

        Optional<ProjectRest> maybeProject = projectLicenseStore.getProjectById(id);
        ProjectRest projectData = maybeProject.orElseThrow(() -> new NotFoundException("No project found for id " + id));
        fullMapper.map(project, projectData);

        projectData = projectLicenseStore.updateProject(projectData);
        return Response.ok().entity(projectData).build();
    }

    @ApiOperation(value = "Delete project by id", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id not provided") })
    @DELETE
    @Path("/{id}")
    public Response deleteProject(@ApiParam(value = "Project id", required = true) @PathParam("id") Integer id) {
        log.debugf("Deleting project with %d", id);

        if (id == null) {
            throw new BadRequestException("Project id must be provided");
        }

        if (!projectLicenseStore.deleteProject(id)) {
            throw new NotFoundException("No project found for id " + id);
        }
        return Response.ok().build();
    }

    @ApiOperation(value = "Get a project by id", response = ProjectRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id not provided"),
            @ApiResponse(code = 404, message = "Project not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificProject(@ApiParam(value = "Project id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get project with %d", id);

        if (id == null) {
            throw new BadRequestException("Project id must be provided");
        }

        ProjectRest entity = projectLicenseStore.getProjectById(id)
                .orElseThrow(() -> new NotFoundException("No project found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, ProjectRest.class)).build();
    }

    @ApiOperation(value = "Get all projects", response = ProjectRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Error processing RSQL query parameter") })
    @GET
    public Response getAllProject(
            @ApiParam(value = "RSQL-type search query (e.g. projectEcosystem.name=='mvn';key=='org.jboss.pnc:parent' , projectVersions.version=='1.2.3*')", required = false) @QueryParam("query") String query,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Get all projects with rsql search '%s'", query);

        if (offset == null) {
            offset = 0;
        }

        try {

            List<ProjectRest> results = projectLicenseStore.getAllProject(Optional.ofNullable(query));
            int totalCount = results.size();

            if (resultCount != null) {
                resultCount += offset;
                results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
            }

            List<ProjectRest> resultList = limitedMapper.map(results, projectRestListType);
            return paginated(resultList, totalCount, offset);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error processing RSQL query parameter");
        }
    }

}
