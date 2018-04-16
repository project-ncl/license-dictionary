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
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseHintRestListType;
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseRestListType;
import static org.jboss.license.dictionary.utils.ResponseUtils.listOrNotFound;

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
import org.jboss.license.dictionary.api.ProjectVersionLicenseHintRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseRest;
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
@Api(tags = { "Project_Version_License" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.PROJECT_VERSION_LICENSE_ENDPOINT)
public class ProjectVersionLicenseEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(ProjectVersionLicenseEndpoint.class);

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @ApiOperation(value = "Get project version license by id", response = ProjectVersionLicenseRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project version license id not provided"),
            @ApiResponse(code = 404, message = "Project version license not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificProjectVersionLicense(
            @ApiParam(value = "Project version license id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get project version license with %d", id);

        if (id == null) {
            throw new BadRequestException("Project version license id must be provided");
        }

        ProjectVersionLicenseRest entity = projectLicenseStore.getProjectVersionLicenseById(id)
                .orElseThrow(() -> new NotFoundException("No project version license found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, ProjectVersionLicenseRest.class)).build();
    }

    @ApiOperation(value = "Create a new project version license", response = ProjectVersionLicenseRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project version license successfully created") })
    @POST
    @Transactional
    public Response createNewProjectVersionLicense(ProjectVersionLicenseRest projectVersionLicenseRest,
            @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license %s", projectVersionLicenseRest);

        projectVersionLicenseRest = projectLicenseStore.saveProjectVersionLicense(projectVersionLicenseRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersionLicenseRest.getId())).entity(projectVersionLicenseRest).build();
    }

    @ApiOperation(value = "Get all project version licenses", response = ProjectVersionLicenseRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Project ecosystem name or project key or project version name not provided"),
            @ApiResponse(code = 404, message = "Project version licenses not found") })
    @GET
    public Response getAllProjectVersionLicense(
            @ApiParam(value = "Project ecosystem name", required = true) @QueryParam("ecosystem") String ecosystem,
            @ApiParam(value = "Project key", required = true) @QueryParam("key") String key,
            @ApiParam(value = "Project version name", required = true) @QueryParam("version") String version,
            @ApiParam(value = "Scope", required = false) @QueryParam("scope") String scope,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf(
                "Finding project version license with ecosystem %s, projectKey %s, version %s, scope %s, numResults %d, offset %d",
                ecosystem, key, version, scope, resultCount, offset);

        if (offset == null) {
            offset = 0;
        }

        List<ProjectVersionLicenseRest> results = null;

        long singleResultIndicatorCount = nonNullCount(ecosystem, key, version);
        if (singleResultIndicatorCount > 0) {

            if (ecosystem == null) {
                throw new BadRequestException("Project ecosystem name must be provided");
            }
            if (key == null) {
                throw new BadRequestException("Project key must be provided");
            }
            if (version == null) {
                throw new BadRequestException("Project version name must be provided");
            }

            if (scope == null || scope.isEmpty()) {
                results = listOrNotFound(
                        projectLicenseStore.getProjectVersionLicenseByEcosystemProjKeyVersion(ecosystem, key, version),
                        "No project version license were found for ecosystem %s, projectKey %s, version %s ", ecosystem, key,
                        version);
            } else {
                results = listOrNotFound(
                        projectLicenseStore.getProjectVersionLicenseByEcosystemProjKeyVersionScope(ecosystem, key, version,
                                scope),
                        "No project version license were found for ecosystem %s, projectKey %s, version %s, scope %s",
                        ecosystem, key, version, scope);
            }

        } else {
            results = projectLicenseStore.getAllProjectVersionLicense();
        }

        int totalCount = results.size();

        if (resultCount != null) {
            resultCount += offset;
            results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
        }

        List<ProjectVersionLicenseRest> resultList = limitedMapper.map(results, projectVersionLicenseRestListType);
        return paginated(resultList, totalCount, offset);
    }

    @ApiOperation(value = "Get all project version license hints by project version license id and license hint id and value", response = ProjectVersionLicenseHintRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project version license id not provided") })
    @GET
    @Path("/{projVersLicId}/hint")
    public Response getAllProjectVersionLicenseHint(
            @ApiParam(value = "Project version license id", required = true) @PathParam("projVersLicId") Integer projVersLicId,
            @ApiParam(value = "License hint type id", required = false) @QueryParam("hintId") Integer hintTypeId,
            @ApiParam(value = "License hint value", required = false) @QueryParam("value") String value) {

        log.debugf("Finding all project version license hint of project version license %d with hintTypeId %d, value %s",
                projVersLicId, hintTypeId, value);

        List<ProjectVersionLicenseHintRest> results = null;

        if (projVersLicId == null) {
            throw new BadRequestException("Project version license id must be provided");
        }

        if (hintTypeId == null) {
            results = projectLicenseStore.getAllProjectVersionLicenseHintByProjVersLicId(projVersLicId);
        } else if (value == null || value.isEmpty()) {
            results = projectLicenseStore.getAllProjectVersionLicenseHintByProjVersLicIdLicHintTypeId(projVersLicId,
                    hintTypeId);
        } else {
            results = projectLicenseStore.getAllProjectVersionLicenseHintByValueProjVersLicIdLicHintType(value, projVersLicId,
                    hintTypeId);
        }

        List<ProjectVersionLicenseHintRest> resultList = limitedMapper.map(results, projectVersionLicenseHintRestListType);
        int resultNumber = resultList == null ? 0 : resultList.size();
        return paginated(resultList, resultNumber, 0);
    }

    @ApiOperation(value = "Create a new project version license hint", response = ProjectVersionLicenseHintRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project version license hint successfully created"),
            @ApiResponse(code = 400, message = "Project version license id not provided or not existing") })
    @POST
    @Path("/{projVersLicId}/hint")
    @Transactional
    public Response createNewProjectVersionLicenseHint(
            @ApiParam(value = "Project version license id", required = true) @PathParam("projVersLicId") Integer projVersLicId,
            ProjectVersionLicenseHintRest projectVersionLicenseHintRest, @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license hint %s for project version id %d", projectVersionLicenseHintRest,
                projVersLicId);

        if (projVersLicId == null) {
            throw new BadRequestException("Project version license id must be provided");
        }

        if (projectVersionLicenseHintRest.getProjectVersionLicense() == null) {
            ProjectVersionLicenseRest entity = projectLicenseStore.getProjectVersionLicenseById(projVersLicId)
                    .orElseThrow(() -> new BadRequestException("The project version license id specified does not exist"));
            projectVersionLicenseHintRest.setProjectVersionLicense(entity);
        } else if (!projectVersionLicenseHintRest.getProjectVersionLicense().getId().equals(projVersLicId)) {
            throw new BadRequestException(
                    "The project version license id specified is not matching the value in project version license hint");
        }

        projectVersionLicenseHintRest = projectLicenseStore.saveProjectVersionLicenseHint(projectVersionLicenseHintRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersionLicenseHintRest.getId())).entity(projectVersionLicenseHintRest)
                .build();
    }

}
