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
import org.jboss.license.dictionary.utils.BadRequestException;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

import api.ProjectVersionLicenseHintRest;
import api.ProjectVersionLicenseRest;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.PROJECT_VERSION_LICENSE_ENDPOINT)
public class ProjectVersionLicenseEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(ProjectVersionLicenseEndpoint.class);

    @Inject
    private ProjectLicenseStore projectLicenseStore;

    @GET
    @Path("/{id}")
    public Response getSpecificProjectVersionLicense(@PathParam("id") Integer id) {
        log.debugf("Get project version license with %d", id);

        ProjectVersionLicenseRest entity = projectLicenseStore.getProjectVersionLicenseById(id)
                .orElseThrow(() -> new NotFoundException("No project version license found for id " + id));

        return Response.ok().entity(fullMapper.map(entity, ProjectVersionLicenseRest.class)).build();
    }

    @POST
    @Transactional
    public Response createNewProjectVersionLicense(ProjectVersionLicenseRest projectVersionLicenseRest,
            @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license %s", projectVersionLicenseRest);

        projectVersionLicenseRest = projectLicenseStore.saveProjectVersionLicense(projectVersionLicenseRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(projectVersionLicenseRest.getId())).entity(projectVersionLicenseRest).build();
    }

    @GET
    public Response getAllProjectVersionLicense(@QueryParam("ecosystem") String ecosystem,
            @QueryParam("projectKey") String projectKey, @QueryParam("version") String version,
            @QueryParam("scope") String scope, @QueryParam("count") Integer resultCount, @QueryParam("offset") Integer offset) {

        log.debugf(
                "Finding project version license with ecosystem %s, projectKey %s, version %s, scope %s, numResults %d, offset %d",
                ecosystem, projectKey, version, scope, resultCount, offset);

        if (offset == null) {
            offset = 0;
        }

        List<ProjectVersionLicenseRest> results = null;

        long singleResultIndicatorCount = nonNullCount(ecosystem, projectKey, version);
        if (singleResultIndicatorCount > 0) {

            if (ecosystem == null) {
                throw new BadRequestException("The ecosystem was not specified");
            }
            if (projectKey == null) {
                throw new BadRequestException("The project identifier was not specified");
            }
            if (version == null) {
                throw new BadRequestException("The project version name was not specified");
            }

            if (scope == null || scope.isEmpty()) {
                results = listOrNotFound(
                        projectLicenseStore.getProjectVersionLicenseByEcosystemProjKeyVersion(ecosystem, projectKey, version),
                        "No project version license were found for ecosystem %s, projectKey %s, version %s ", ecosystem,
                        projectKey, version);
            } else {
                results = listOrNotFound(
                        projectLicenseStore.getProjectVersionLicenseByEcosystemProjKeyVersionScope(ecosystem, projectKey,
                                version, scope),
                        "No project version license were found for ecosystem %s, projectKey %s, version %s, scope %s",
                        ecosystem, projectKey, version, scope);
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

    @GET
    @Path("/{projVersLicId}/hint")
    public Response getAllProjectVersionLicenseHint(@PathParam("projVersLicId") Integer projVersLicId,
            @QueryParam("hintId") Integer hintTypeId, @QueryParam("value") String value) {

        log.debugf("Finding all project version license hint of project version license %d with hintTypeId %d, value %s",
                projVersLicId, hintTypeId, value);

        List<ProjectVersionLicenseHintRest> results = null;

        if (projVersLicId == null) {
            throw new BadRequestException("The project version license id was not specified");
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

    @POST
    @Path("/{projVersLicId}/hint")
    @Transactional
    public Response createNewProjectVersionLicenseHint(@PathParam("projVersLicId") Integer projVersLicId,
            ProjectVersionLicenseHintRest projectVersionLicenseHintRest, @Context UriInfo uriInfo) {
        log.debugf("Creating new project version license hint %s for project version id %d", projectVersionLicenseHintRest,
                projVersLicId);

        if (projVersLicId == null) {
            throw new BadRequestException("The project version license id was not specified");
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
