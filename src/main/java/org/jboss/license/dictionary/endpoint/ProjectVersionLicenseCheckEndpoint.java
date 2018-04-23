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
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseCheckRestListType;
import static org.jboss.license.dictionary.utils.Mappers.projectVersionLicenseRestListType;

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

import org.jboss.license.dictionary.ProjectLicenseStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.api.ProjectVersionLicenseCheckRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseRest;
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
@Api(tags = { "Project_Version_License_Check" })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.PROJECT_VERSION_LICENSE_CHECK_ENDPOINT)
public class ProjectVersionLicenseCheckEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(ProjectVersionLicenseCheckEndpoint.class);

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @ApiOperation(value = "Create a new project version license check", response = ProjectVersionLicenseCheckRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Project version license check successfully created"),
            @ApiResponse(code = 400, message = "Project version license check id not null") })
    @POST
    @Transactional
    public Response createNewProjectVersionLicenseCheck(ProjectVersionLicenseCheckRest projectVersionLicenseCheck,
            @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license check %s", projectVersionLicenseCheck);

        if (projectVersionLicenseCheck.getId() != null) {
            throw new BadRequestException("Project version license check id must be null");
        }
        projectVersionLicenseCheck = projectLicenseStore.saveProjectVersionLicenseCheck(projectVersionLicenseCheck);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersionLicenseCheck.getId())).entity(projectVersionLicenseCheck)
                .build();
    }

    @ApiOperation(value = "Get project version license check by id", response = ProjectVersionLicenseCheckRest.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Project version license check id not provided"),
            @ApiResponse(code = 404, message = "Project version license check not found") })
    @GET
    @Path("/{id}")
    public Response getSpecificProjectVersionLicenseCheck(
            @ApiParam(value = "Project version license check id", required = true) @PathParam("id") Integer id) {
        log.debugf("Get project version license check with %d", id);

        if (id == null) {
            throw new BadRequestException("Project version license check id must be provided");
        }

        ProjectVersionLicenseCheckRest entity = projectLicenseStore.getProjectVersionLicenseCheckById(id)
                .orElseThrow(() -> new NotFoundException("No project version license check found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, ProjectVersionLicenseCheckRest.class)).build();
    }

    @ApiOperation(value = "Get all project version license checks", response = ProjectVersionLicenseCheck.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Error processing RSQL query parameter") })
    @GET
    public Response getAllProjectVersionLicenseCheck(
            @ApiParam(value = "RSQL-type search query (e.g. id==1;version=='1.2.3*')", required = false) @QueryParam("query") String query,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Get all project version license checks with rsql search '%s'", query);

        if (offset == null) {
            offset = 0;
        }

        try {

            List<ProjectVersionLicenseCheckRest> results = projectLicenseStore
                    .getAllProjectVersionLicenseCheck(Optional.ofNullable(query));
            int totalCount = results.size();

            if (resultCount != null) {
                resultCount += offset;
                results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
            }

            List<ProjectVersionLicenseCheckRest> resultList = limitedMapper.map(results,
                    projectVersionLicenseCheckRestListType);
            return paginated(resultList, totalCount, offset);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error processing RSQL query parameter");
        }
    }

    @ApiOperation(value = "Get all project version licenses", response = ProjectVersionLicenseRest.class, responseContainer = "List", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Error processing RSQL query parameter") })
    @GET
    public Response getAllProjectVersionLicense(
            @ApiParam(value = "RSQL-type search query (e.g. id==1;version=='1.2.3*')", required = false) @QueryParam("query") String query,
            @ApiParam(value = "Number of results to return", required = false) @QueryParam("count") Integer resultCount,
            @ApiParam(value = "Results offset used for pagination", required = false) @QueryParam("offset") Integer offset) {

        log.debugf("Get all project versions with rsql search '%s'", query);

        if (offset == null) {
            offset = 0;
        }

        try {

            List<ProjectVersionLicenseRest> results = projectLicenseStore
                    .getAllProjectVersionLicense(Optional.ofNullable(query));
            int totalCount = results.size();

            if (resultCount != null) {
                resultCount += offset;
                results = results.subList(offset, results.size() < resultCount ? results.size() : resultCount);
            }

            List<ProjectVersionLicenseRest> resultList = limitedMapper.map(results, projectVersionLicenseRestListType);
            return paginated(resultList, totalCount, offset);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Error processing RSQL query parameter");
        }
    }

}
