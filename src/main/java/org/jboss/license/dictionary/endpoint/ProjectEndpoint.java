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
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseCheckRestListType;
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseRestListType;
import static org.jboss.license.dictionary.utils.Mappers.projectVersionRestListType;

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
import org.jboss.license.dictionary.api.ProjectVersionLicenseCheckRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseRest;
import org.jboss.license.dictionary.api.ProjectVersionRest;
import org.jboss.license.dictionary.model.ProjectVersionLicenseCheck;
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
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project successfully created") })
    @POST
    @Transactional
    public Response createNewProject(ProjectRest project, @Context UriInfo uriInfo) {
        log.debugf("Creating new project %s", project);

        project = projectLicenseStore.saveProject(project);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(project.getId())).entity(project).build();
    }

    @ApiOperation(value = "Update project by id", response = ProjectRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id not provided"),
            @ApiResponse(code = 404, message = "Project to update not found") })
    @PUT
    @Path("/{projectId}")
    @Transactional
    public Response updateProject(@ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer id,
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
    @Path("/{projectId}")
    public Response deleteProject(@ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer id) {
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
    @Path("/{projectId}")
    public Response getSpecificProject(@ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer id) {
        log.debugf("Get project with %d", id);

        if (id == null) {
            throw new BadRequestException("Project id must be provided");
        }

        ProjectRest entity = projectLicenseStore.getProjectById(id)
                .orElseThrow(() -> new NotFoundException("No project found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, ProjectRest.class)).build();
    }

    @ApiOperation(value = "Get a project by ecosystem and key", response = ProjectRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project ecosystem name or project key not provided"),
            @ApiResponse(code = 404, message = "Project not found") })
    @GET
    public Response getProjectByEcosystemKey(
            @ApiParam(value = "Project ecosystem name", required = true) @QueryParam("ecosystem") String ecosystemName,
            @ApiParam(value = "Project key", required = true) @QueryParam("key") String key) {
        log.debugf("Finding project with ecosystem %s, key %s", ecosystemName, key);

        if (ecosystemName == null) {
            throw new BadRequestException("Project ecosystem name must be provided");
        }
        if (key == null) {
            throw new BadRequestException("Project key must be provided");
        }

        ProjectRest entity = projectLicenseStore.getProjectByEcosystemKey(ecosystemName, key).orElseThrow(
                () -> new NotFoundException("No project found for ecosystem '" + ecosystemName + "', key '" + key + "'"));

        return Response.ok().entity(fullMapper.map(entity, ProjectRest.class)).build();
    }

    @ApiOperation(value = "Get all projects", response = ProjectRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @GET
    public Response getAllProject(
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Finding all project with maxResults %d, offset %d", resultCount, offset);

        List<ProjectRest> results = projectLicenseStore.getAllProject();

        int totalCount = results.size();

        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectRest> resultList = limitedMapper.map(results, projectRestListType);
        return paginated(resultList, totalCount, offset);
    }

    @ApiOperation(value = "Get all projects by ecosystem", response = ProjectRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project ecosystem name not provided") })
    @GET
    public Response getAllProjectByEcosystem(
            @ApiParam(value = "Project ecosystem name", required = true) @QueryParam("ecosystem") String ecosystemName,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {
        log.debugf("Finding project with ecosystem %s, maxResults %d, offset %d", ecosystemName, resultCount, offset);

        if (ecosystemName == null) {
            throw new BadRequestException("Project ecosystem name must be provided");
        }

        List<ProjectRest> results = projectLicenseStore.getAllProjectByEcosystem(ecosystemName);

        int totalCount = results.size();
        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectRest> resultList = limitedMapper.map(results, projectRestListType);
        return paginated(resultList, totalCount, offset);
    }

    @ApiOperation(value = "Create a new project version", response = ProjectVersionRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project version successfully created"),
            @ApiResponse(code = 400, message = "Project id not provided") })
    @POST
    @Path("/{projectId}/version")
    @Transactional
    public Response createNewProjectVersion(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            ProjectVersionRest projectVersion, @Context UriInfo uriInfo) {
        log.debugf("Creating new project version %s for project %d", projectVersion, projectId);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }

        if (projectVersion.getProject() == null) {
            ProjectRest entity = projectLicenseStore.getProjectById(projectId)
                    .orElseThrow(() -> new BadRequestException("The project id specified does not exist"));
            projectVersion.setProject(entity);
        } else if (!projectVersion.getProject().getId().equals(projectId)) {
            throw new BadRequestException("The project id specified is not matching the value in project version");
        }

        projectVersion = projectLicenseStore.saveProjectVersion(projectVersion);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersion.getId())).entity(projectVersion).build();
    }

    @ApiOperation(value = "Get a project version by project id and project version id", response = ProjectVersionRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id or project version id not provided"),
            @ApiResponse(code = 404, message = "Project version not found") })
    @GET
    @Path("/{projectId}/version/{projVersId}")
    public Response getSpecificProjectVersion(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Project version id", required = true) @PathParam("projVersId") Integer projVersId) {
        log.debugf("Get project version with %d", projVersId);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }
        if (projVersId == null) {
            throw new BadRequestException("Project version id must be provided");
        }

        ProjectVersionRest entity = projectLicenseStore.getProjectVersionById(projVersId)
                .orElseThrow(() -> new NotFoundException("No project version found for id " + projVersId));

        return Response.ok().entity(fullMapper.map(entity, ProjectVersionRest.class)).build();
    }

    @ApiOperation(value = "Get all project versions by project id", response = ProjectVersionRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id not provided") })
    @GET
    @Path("/{projectId}/version")
    public Response getAllProjectVersion(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {
        log.debugf("Finding all project version of project %d with maxResults %d, offset %d", projectId, resultCount, offset);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }

        List<ProjectVersionRest> results = projectLicenseStore.getAllProjectVersionByProjectId(projectId);

        int totalCount = results.size();

        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectVersionRest> resultList = limitedMapper.map(results, projectVersionRestListType);
        return paginated(resultList, totalCount, offset);
    }

    @ApiOperation(value = "Get a project version by project id and version name", response = ProjectVersionRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id or version name not provided"),
            @ApiResponse(code = 404, message = "Project version not found") })
    @GET
    @Path("/{projectId}/version")
    public Response getProjectVersionByProjectIdVersion(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Project version name", required = true) @QueryParam("name") String name) {

        log.debugf("Finding project version of project %d with name %s", projectId, name);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }
        if (name == null) {
            throw new BadRequestException("Project version name must be provided");
        }

        ProjectVersionRest entity = projectLicenseStore.getProjectVersionByVersionProjectId(name, projectId).orElseThrow(
                () -> new NotFoundException("No project version found for projectId " + projectId + " and name " + name));

        return Response.ok().entity(fullMapper.map(entity, ProjectVersionRest.class)).build();
    }

    @ApiOperation(value = "Get all project version license checks by project id and project version id", response = ProjectVersionLicenseCheck.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project id or project version id not provided") })
    @GET
    @Path("/{projectId}/version/{projVersId}/check")
    public Response getAllProjectVersionLicenseCheckByProjVersId(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Project version id", required = true) @PathParam("projVersId") Integer projVersId,
            @ApiParam(value = "License determination type id", required = false) @QueryParam("detTypeId") Integer licDeterminationTypeId,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Get all project version license check with project id %d and project version %d", projectId, projVersId);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }
        if (projVersId == null) {
            throw new BadRequestException("Project version id must be provided");
        }

        List<ProjectVersionLicenseCheckRest> results = null;
        if (licDeterminationTypeId == null) {
            results = projectLicenseStore.getAllProjectVersionLicenseCheckByProjVersId(projVersId);
        } else {
            results = projectLicenseStore.getAllProjectVersionLicenseCheckByProjVersIdLicDeteTypeId(projVersId,
                    licDeterminationTypeId);
        }

        int totalCount = results.size();

        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectVersionLicenseCheck> resultList = limitedMapper.map(results, projectVersionLicenseCheckRestListType);
        return paginated(resultList, totalCount, offset);
    }

    @ApiOperation(value = "Create a new project version license check", response = ProjectVersionLicenseCheckRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project version license check successfully created"),
            @ApiResponse(code = 400, message = "Project id or project version id not provided, or project version id specified does not exist") })
    @POST
    @Path("/{projectId}/version/{projVersId}/check")
    @Transactional
    public Response createNewProjectVersionLicenseCheck(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Project version id", required = true) @PathParam("projVersId") Integer projVersId,
            ProjectVersionLicenseCheckRest projectVersionLicenseCheck, @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license check %s for project %d and project version %d",
                projectVersionLicenseCheck, projectId, projVersId);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }
        if (projVersId == null) {
            throw new BadRequestException("Project version id must be provided");
        }

        if (projectVersionLicenseCheck.getProjectVersion() == null) {
            ProjectVersionRest entity = projectLicenseStore.getProjectVersionById(projVersId)
                    .orElseThrow(() -> new BadRequestException("The project version id specified does not exist"));
            projectVersionLicenseCheck.setProjectVersion(entity);
        } else if (!projectVersionLicenseCheck.getProjectVersion().getId().equals(projVersId)) {
            throw new BadRequestException(
                    "The project version id specified is not matching the value in project version license check");
        }

        projectVersionLicenseCheck = projectLicenseStore.saveProjectVersionLicenseCheck(projectVersionLicenseCheck);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersionLicenseCheck.getId())).entity(projectVersionLicenseCheck)
                .build();
    }

    @ApiOperation(value = "Get all project version license by project id and project version id and project version license check id", response = ProjectVersionLicenseRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Project id or project version id or project version license check id not provided") })
    @GET
    @Path("/{projectId}/version/{projVersId}/check/{projVersLicCheckId}/license")
    public Response getAllProjectVersionLicenseByProjVersLicCheckId(
            @ApiParam(value = "Project id", required = true) @PathParam("projectId") Integer projectId,
            @ApiParam(value = "Project version id", required = true) @PathParam("projVersId") Integer projVersId,
            @ApiParam(value = "Project version license check id", required = true) @PathParam("projVersLicCheckId") Integer projVersLicCheckId,
            @ApiParam(value = "License id", required = false) @QueryParam("licId") Integer licenseId,
            @ApiParam(value = "Scope", required = false) @QueryParam("scope") String scope,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Get all project version license with project version %d and check id %d", projVersId, projVersLicCheckId);

        if (projectId == null) {
            throw new BadRequestException("Project id must be provided");
        }
        if (projVersId == null) {
            throw new BadRequestException("Project version id must be provided");
        }
        if (projVersLicCheckId == null) {
            throw new BadRequestException("Project version license check id must be provided");
        }

        List<ProjectVersionLicenseRest> results = null;
        if (licenseId == null) {
            results = projectLicenseStore.getAllProjectVersionLicenseByProjVersLicCheckId(projVersLicCheckId);
        } else if (scope == null) {
            results = projectLicenseStore.getAllProjectVersionLicenseByLicIdProjVersLicCheckcId(projVersLicCheckId, licenseId);
        } else if (scope != null) {
            results = projectLicenseStore.getAllProjectVersionLicenseByScopeLicIdProjVersLicId(scope, licenseId,
                    projVersLicCheckId);
        }

        int totalCount = results.size();

        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectVersionLicenseRest> resultList = limitedMapper.map(results, projectVersionLicenseRestListType);
        return paginated(resultList, totalCount, offset);
    }

}
