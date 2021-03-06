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
package org.jboss.license.dictionary;

import static org.jboss.license.dictionary.utils.Mappers.fullMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.license.dictionary.api.LicenseHintTypeRest;
import org.jboss.license.dictionary.api.LicenseRest;
import org.jboss.license.dictionary.api.ProjectEcosystemRest;
import org.jboss.license.dictionary.api.ProjectRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseCheckRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseHintRest;
import org.jboss.license.dictionary.api.ProjectVersionLicenseRest;
import org.jboss.license.dictionary.api.ProjectVersionRest;
import org.jboss.license.dictionary.imports.JsonProjectLicense;
import org.jboss.license.dictionary.imports.JsonProjectLicenseDeterminationHint;
import org.jboss.license.dictionary.imports.JsonProjectSCMInfo;
import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseDeterminationType;
import org.jboss.license.dictionary.model.LicenseHintType;
import org.jboss.license.dictionary.model.Project;
import org.jboss.license.dictionary.model.ProjectEcosystem;
import org.jboss.license.dictionary.model.ProjectVersion;
import org.jboss.license.dictionary.model.ProjectVersionLicense;
import org.jboss.license.dictionary.model.ProjectVersionLicenseCheck;
import org.jboss.license.dictionary.model.ProjectVersionLicenseHint;
import org.jboss.license.dictionary.utils.QueryUtils;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProjectLicenseStore {

    private static final Logger log = Logger.getLogger(ProjectLicenseStore.class);

    @Inject
    private LicenseDbStore dbStore;

    @Inject
    private LicenseStore licenseStore;

    public ProjectRest saveProject(ProjectRest projectRest) {
        Project entity = fullMapper.map(projectRest, Project.class);
        entity = dbStore.saveProject(entity);

        return fullMapper.map(entity, ProjectRest.class);
    }

    public ProjectRest updateProject(ProjectRest projectRest) {
        Project entity = fullMapper.map(projectRest, Project.class);
        entity = dbStore.updateProject(entity);

        return fullMapper.map(entity, ProjectRest.class);
    }

    @Transactional
    public boolean deleteProject(Integer projectId) {
        return dbStore.deleteProject(projectId);
    }

    public Optional<ProjectRest> getProjectById(Integer projectId) {
        log.debugf("Get project by id %d", projectId);
        Project project = dbStore.getProjectById(projectId);
        if (project == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(project, ProjectRest.class));
    }

    public List<ProjectRest> getAllProject(Optional<String> rsqlSearch) {

        return dbStore.getAllProject(rsqlSearch).stream().map(entity -> fullMapper.map(entity, ProjectRest.class))
                .collect(Collectors.toList());
    }

    //
    public ProjectEcosystemRest saveProjectEcosystem(ProjectEcosystemRest projectEcosystemRest) {
        ProjectEcosystem entity = fullMapper.map(projectEcosystemRest, ProjectEcosystem.class);
        entity = dbStore.saveProjectEcosystem(entity);

        return fullMapper.map(entity, ProjectEcosystemRest.class);
    }

    public Optional<ProjectEcosystemRest> getProjectEcosystemById(Integer projectEcosystemId) {
        log.infof("Get project ecosystems by id %d", projectEcosystemId);

        ProjectEcosystem projectEcosystem = dbStore.getProjectEcosystemById(projectEcosystemId);
        if (projectEcosystem == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(projectEcosystem, ProjectEcosystemRest.class));
    }

    public List<ProjectEcosystemRest> getAllProjectEcosystem(Optional<String> rsqlSearch) {

        return dbStore.getAllProjectEcosystem(rsqlSearch).stream()
                .map(entity -> fullMapper.map(entity, ProjectEcosystemRest.class)).collect(Collectors.toList());
    }

    //
    public ProjectVersionRest saveProjectVersion(ProjectVersionRest projectVersionRest) {
        ProjectVersion entity = fullMapper.map(projectVersionRest, ProjectVersion.class);
        entity = dbStore.saveProjectVersion(entity);

        return fullMapper.map(entity, ProjectVersionRest.class);
    }

    public Optional<ProjectVersionRest> getProjectVersionById(Integer projectVersionId) {
        log.infof("Get project version by id %d", projectVersionId);
        return Optional.ofNullable(fullMapper.map(dbStore.getProjectVersionById(projectVersionId), ProjectVersionRest.class));
    }

    public List<ProjectVersionRest> getAllProjectVersion(Optional<String> rsqlSearch) {

        return dbStore.getAllProjectVersion(rsqlSearch).stream().map(entity -> fullMapper.map(entity, ProjectVersionRest.class))
                .collect(Collectors.toList());
    }

    //

    public List<ProjectVersionLicenseCheckRest> getAllProjectVersionLicenseCheck(Optional<String> rsqlSearch) {

        return dbStore.getAllProjectVersionLicenseCheck(rsqlSearch).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseCheckRest.class)).collect(Collectors.toList());
    }

    public Optional<ProjectVersionLicenseCheckRest> getProjectVersionLicenseCheckById(Integer projectVersionLicenseCheckId) {
        log.infof("Get project version license check by id %d", projectVersionLicenseCheckId);
        return Optional.ofNullable(fullMapper.map(dbStore.getProjectVersionLicenseCheckById(projectVersionLicenseCheckId),
                ProjectVersionLicenseCheckRest.class));
    }

    public ProjectVersionLicenseCheckRest saveProjectVersionLicenseCheck(
            ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest) {
        ProjectVersionLicenseCheck entity = fullMapper.map(projectVersionLicenseCheckRest, ProjectVersionLicenseCheck.class);
        entity = dbStore.saveProjectVersionLicenseCheck(entity);

        return fullMapper.map(entity, ProjectVersionLicenseCheckRest.class);
    }

    //
    public ProjectVersionLicenseRest saveProjectVersionLicense(ProjectVersionLicenseRest projectVersionLicenseRest) {
        ProjectVersionLicense entity = fullMapper.map(projectVersionLicenseRest, ProjectVersionLicense.class);
        entity = dbStore.saveProjectVersionLicense(entity);

        return fullMapper.map(entity, ProjectVersionLicenseRest.class);
    }

    public Optional<ProjectVersionLicenseRest> getProjectVersionLicenseById(Integer projectVersionLicenseId) {
        log.infof("Get project version license by id %d", projectVersionLicenseId);
        return Optional.ofNullable(
                fullMapper.map(dbStore.getProjectVersionLicenseById(projectVersionLicenseId), ProjectVersionLicenseRest.class));
    }

    public List<ProjectVersionLicenseRest> getAllProjectVersionLicense(Optional<String> rsqlSearch) {

        return dbStore.getAllProjectVersionLicense(rsqlSearch).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public Optional<ProjectVersionLicenseHintRest> getProjectVersionLicenseHintById(Integer projectVersionLicenseHintId) {
        log.infof("Get project version license hint by id %d", projectVersionLicenseHintId);
        return Optional.ofNullable(fullMapper.map(dbStore.getProjectVersionLicenseHintById(projectVersionLicenseHintId),
                ProjectVersionLicenseHintRest.class));
    }

    public List<ProjectVersionLicenseHintRest> getAllProjectVersionLicenseHint(Optional<String> rsqlSearch) {

        return dbStore.getAllProjectVersionLicenseHint(rsqlSearch).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseHintRest.class)).collect(Collectors.toList());
    }

    public ProjectVersionLicenseHintRest saveProjectVersionLicenseHint(
            ProjectVersionLicenseHintRest projectVersionLicenseHint) {

        ProjectVersionLicenseHint entity = fullMapper.map(projectVersionLicenseHint, ProjectVersionLicenseHint.class);
        entity = dbStore.saveProjectVersionLicenseHint(entity);

        return fullMapper.map(entity, ProjectVersionLicenseHintRest.class);
    }

    @Transactional
    public void importProjectLicenses(JsonProjectLicense[] jsonProjectLicenses) {
        log.infof("started appending %d Project Licenses...", jsonProjectLicenses.length);

        String rsql = "name=='" + QueryUtils.escapeReservedChars(ProjectEcosystem.MAVEN) + "'";
        List<ProjectEcosystem> ecosystems = dbStore.getAllProjectEcosystem(Optional.of(rsql));
        ProjectEcosystem ecosystem = ecosystems.get(0);
        if (ecosystem == null) {
            ecosystem = dbStore
                    .saveProjectEcosystem(ProjectEcosystem.Builder.newBuilder().name(ProjectEcosystem.MAVEN).build());
        }

        List<ProjectVersionLicenseHintRest> projectVersionLicenseHintRests = new ArrayList<ProjectVersionLicenseHintRest>();

        for (JsonProjectLicense projectLicense : jsonProjectLicenses) {

            List<ProjectVersionLicenseCheckRest> projVersLicenseCheckRests = new ArrayList<ProjectVersionLicenseCheckRest>();
            List<ProjectVersionLicenseRest> projectVersionLicenseRests = new ArrayList<ProjectVersionLicenseRest>();

            ProjectRest projectRest = ProjectRest.Builder.newBuilder()
                    .ecosystem(ProjectEcosystemRest.Builder.newBuilder().name(ProjectEcosystem.MAVEN).build())
                    .key(projectLicense.retrieveGAFromRootPom()).build();

            for (JsonProjectSCMInfo scmInfo : projectLicense.getScmInfo()) {
                ProjectVersionRest projectVersionRest = scmInfo.toProjectVersionRest();
                projectVersionRest.setProject(projectRest);
                projectVersionRest.setVersion(projectLicense.retrieveVFromProject());

                String notes = "scope " + projectLicense.getScope();
                ProjectVersionLicenseCheckRest projVersLicenseCheckRest = ProjectVersionLicenseCheckRest.Builder.newBuilder()
                        .determinedByUser(projectLicense.getDeterminedBy())
                        .determinationDate(projectLicense.retrieveDeterminationDate()).notes(notes)
                        .projectVersion(projectVersionRest)
                        .licenseDeterminationType(projectLicense.getLicenseDeterminationType().toLicenseDeterminationTypeRest())
                        .build();

                projVersLicenseCheckRests.add(projVersLicenseCheckRest);
            }

            for (ProjectVersionLicenseCheckRest projVersLicenseCheckRest : projVersLicenseCheckRests) {

                for (String license : projectLicense.getLicenseList()) {
                    LicenseRest licenseRest = LicenseRest.Builder.newBuilder().spdxName(license).build();

                    String scope = null;
                    if (projectLicense.getGAV() != null && !projectLicense.getGAV().isEmpty()
                            && !projectLicense.getGAV().equalsIgnoreCase(projectLicense.getRootPomGAV())) {
                        scope = projectLicense.retrieveGAFromScopeGAV();
                    }

                    ProjectVersionLicenseRest projectVersionLicenseRest = ProjectVersionLicenseRest.Builder.newBuilder()
                            .scope(scope).license(licenseRest).projectVersionLicenseCheck(projVersLicenseCheckRest).build();

                    projectVersionLicenseRests.add(projectVersionLicenseRest);
                }
            }

            for (ProjectVersionLicenseRest projectVersionLicenseRest : projectVersionLicenseRests) {

                for (JsonProjectLicenseDeterminationHint hints : projectLicense.getLicenseDeterminationHints()) {
                    LicenseHintTypeRest hintTypeRest = LicenseHintTypeRest.Builder.newBuilder().name(hints.getName()).build();
                    if (hints.getValue() != null) {

                        ProjectVersionLicenseHintRest licenseHintRest = ProjectVersionLicenseHintRest.Builder.newBuilder()
                                .licenseHintType(hintTypeRest).projectVersionLicense(projectVersionLicenseRest)
                                .value(hints.getValue()).build();

                        projectVersionLicenseHintRests.add(licenseHintRest);
                    } else if (hints.getValues() != null) {
                        for (String value : hints.getValues()) {
                            ProjectVersionLicenseHintRest licenseHintRest = ProjectVersionLicenseHintRest.Builder.newBuilder()
                                    .licenseHintType(hintTypeRest).projectVersionLicense(projectVersionLicenseRest).value(value)
                                    .build();
                            projectVersionLicenseHintRests.add(licenseHintRest);
                        }
                    } else {
                        ProjectVersionLicenseHintRest licenseHintRest = ProjectVersionLicenseHintRest.Builder.newBuilder()
                                .licenseHintType(hintTypeRest).projectVersionLicense(projectVersionLicenseRest).value("")
                                .build();

                        projectVersionLicenseHintRests.add(licenseHintRest);

                    }
                }
            }
        }
        ;

        for (ProjectVersionLicenseHintRest projectVersionLicenseHintRest : projectVersionLicenseHintRests) {

            // Retrieve the license as first action, so that if no license is found, no data is stored in DB,
            // to avoid incomplete information stored
            String searchRsql = "fedoraName=='"
                    + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "',fedoraAbbreviation=='"
                    + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "',spdxName=='"
                    + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "',spdxAbbreviation=='"
                    + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "',code=='"
                    + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "',aliases.aliasName=='" + QueryUtils.escapeReservedChars(
                            projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    + "'";

            List<LicenseRest> licenses = licenseStore.getAllLicense(Optional.of(searchRsql)).stream()
                    .collect(Collectors.toList());

            if (licenses == null || licenses.isEmpty()) {
                log.infof("\n\n*** NO LICENSE FOUND WITH NAME, SKIPPING ... %s ",
                        projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName());
                continue;
            }

            License license = dbStore.getLicenseById(licenses.get(0).getId());
            log.infof("### FOUND LICENSE: %s ", license);

            // Create or retrieve existing Project
            ProjectVersionLicenseCheckRest projVersLicenseCheckRest = projectVersionLicenseHintRest.getProjectVersionLicense()
                    .getProjectVersionLicenseCheck();
            Project mappedProject = fullMapper.map(projVersLicenseCheckRest.getProjectVersion().getProject(), Project.class);

            log.debugf("### Finding already existing projects with ecosystem: %s and key: %s ",
                    mappedProject.getProjectEcosystem().getName(), mappedProject.getKey());

            String rsqlQuery = "projectEcosystem.name=='"
                    + QueryUtils.escapeReservedChars(mappedProject.getProjectEcosystem().getName()) + "';key=='"
                    + QueryUtils.escapeReservedChars(mappedProject.getKey()) + "'";
            List<Project> projectList = dbStore.getAllProject(Optional.of(rsqlQuery));
            Project project = (projectList != null && !projectList.isEmpty()) ? projectList.get(0) : null;

            if (project == null) {

                log.debugf("### Finding already existing project ecosystems with name: %s ",
                        mappedProject.getProjectEcosystem().getName());

                String rsql2 = "name=='" + QueryUtils.escapeReservedChars(mappedProject.getProjectEcosystem().getName()) + "'";
                List<ProjectEcosystem> ecosystems2 = dbStore.getAllProjectEcosystem(Optional.of(rsql2));
                ProjectEcosystem projectEcosystem = (ecosystems2 != null && !ecosystems2.isEmpty()) ? ecosystems2.get(0) : null;

                if (projectEcosystem == null) {
                    projectEcosystem = dbStore.saveProjectEcosystem(projectEcosystem);
                    log.debugf("# created project ecosystem: %s ", projectEcosystem);
                } else {
                    log.debugf("# retrieved project ecosystem: %s ", projectEcosystem);
                }
                mappedProject.setProjectEcosystem(projectEcosystem);
                project = dbStore.saveProject(mappedProject);

                log.debugf("# created project: %s ", project);
            } else {
                log.debugf("# retrieved project: %s ", project);
            }

            // Create or retrieve existing Project Version
            ProjectVersion mappedProjectVersion = fullMapper.map(projVersLicenseCheckRest.getProjectVersion(),
                    ProjectVersion.class);
            mappedProjectVersion.setProject(project);

            log.debugf("### Finding already existing project versions with version: %s and project id: %d ",
                    mappedProjectVersion.getVersion(), mappedProjectVersion.getProject().getId());

            String rsqlQuery3 = "project.id==" + mappedProjectVersion.getProject().getId() + ";version=='"
                    + QueryUtils.escapeReservedChars(mappedProjectVersion.getVersion()) + "'";

            List<ProjectVersion> projectVersions = dbStore.getAllProjectVersion(Optional.of(rsqlQuery3));
            ProjectVersion projectVersion = (projectVersions != null && !projectVersions.isEmpty()) ? projectVersions.get(0)
                    : null;

            if (projectVersion == null) {
                projectVersion = dbStore.saveProjectVersion(mappedProjectVersion);

                log.debugf("# created project version: %s ", projectVersion);
            } else {
                log.debugf("# retrieved project version: %s ", projectVersion);
            }

            // Create or retrieve existing license determination type
            LicenseDeterminationType mappedLicenseDeterminationType = fullMapper
                    .map(projVersLicenseCheckRest.getLicenseDeterminationType(), LicenseDeterminationType.class);

            log.debugf("### Finding already existing license determination type with id: %d ",
                    mappedLicenseDeterminationType.getId());

            LicenseDeterminationType licenseDeterminationType = dbStore
                    .getLicenseDeterminationTypeById(mappedLicenseDeterminationType.getId());
            if (licenseDeterminationType == null) {
                licenseDeterminationType = dbStore.saveLicenseDeterminationType(mappedLicenseDeterminationType);

                log.debugf("# created license determination type: %s ", licenseDeterminationType);
            } else {
                log.debugf("# retrieved license determination type: %s ", licenseDeterminationType);
            }

            // Create or retrieve existing project version license check
            ProjectVersionLicenseCheck mappedProjectVersionLicenseCheck = fullMapper.map(projVersLicenseCheckRest,
                    ProjectVersionLicenseCheck.class);
            mappedProjectVersionLicenseCheck.setLicenseDeterminationType(licenseDeterminationType);
            mappedProjectVersionLicenseCheck.setProjectVersion(projectVersion);
            mappedProjectVersionLicenseCheck.setDeterminationDate(projVersLicenseCheckRest.getDeterminationDate());

            ProjectVersionLicenseCheck projectVersionLicenseCheck = null;

            log.debugf(
                    "### Finding already existing project version license checks with project version id: %d and license determination type id: %d ",
                    projectVersion.getId(), mappedLicenseDeterminationType.getId());

            String rsqlQuery5 = "projectVersion.id==" + projectVersion.getId() + ";licenseDeterminationType.id=="
                    + licenseDeterminationType.getId();
            List<ProjectVersionLicenseCheck> projectVersionLicenseCheckList = dbStore
                    .getAllProjectVersionLicenseCheck(Optional.of(rsqlQuery5));

            if (projectVersionLicenseCheckList == null || projectVersionLicenseCheckList.isEmpty()) {
                projectVersionLicenseCheck = dbStore.saveProjectVersionLicenseCheck(mappedProjectVersionLicenseCheck);

                log.debugf("# created project version license check: %s ", projectVersionLicenseCheck);
            } else {
                projectVersionLicenseCheck = projectVersionLicenseCheckList.get(0);
                log.debugf("# retrieved project version license check: %s ", projectVersionLicenseCheck);
            }

            // Create or retrieve existing project version license
            ProjectVersionLicense projectVersionLicense = null;

            log.debugf(
                    "### Finding already existing project version license with scope: %s and license id: %d and project version license check id: %d ",
                    projectVersionLicenseHintRest.getProjectVersionLicense().getScope(), license.getId(),
                    projectVersionLicenseCheck.getId());

            List<ProjectVersionLicense> projectVersionLicenseList = null;
            if (projectVersionLicenseHintRest.getProjectVersionLicense().getScope() != null
                    && !projectVersionLicenseHintRest.getProjectVersionLicense().getScope().isEmpty()) {

                String rsqlPVLic = "scope=='"
                        + QueryUtils.escapeReservedChars(projectVersionLicenseHintRest.getProjectVersionLicense().getScope())
                        + "';license.id==" + license.getId() + ";projectVersionLicenseCheck.id=="
                        + projectVersionLicenseCheck.getId();

                projectVersionLicenseList = dbStore.getAllProjectVersionLicense(Optional.of(rsqlPVLic));
            } else {

                String rsqlPVLic = "license.id==" + license.getId() + ";projectVersionLicenseCheck.id=="
                        + projectVersionLicenseCheck.getId();
                projectVersionLicenseList = dbStore.getAllProjectVersionLicense(Optional.of(rsqlPVLic));
            }

            if (projectVersionLicenseList == null || projectVersionLicenseList.isEmpty()) {
                projectVersionLicense = ProjectVersionLicense.Builder.newBuilder()
                        .projectVersionLicenseCheck(projectVersionLicenseCheck).license(license)
                        .scope(projectVersionLicenseHintRest.getProjectVersionLicense().getScope()).build();

                projectVersionLicense = dbStore.saveProjectVersionLicense(projectVersionLicense);

                log.debugf("# created project version license: %s ", projectVersionLicense);
            } else {
                projectVersionLicense = projectVersionLicenseList.get(0);
                log.debugf("# retrieved project version license: %s ", projectVersionLicense);
            }

            // Create or retrieve existing license determination type
            LicenseHintType mappedLicenseHintType = fullMapper.map(projectVersionLicenseHintRest.getLicenseHintType(),
                    LicenseHintType.class);

            log.debugf("### Finding already existing license hint type with name: %s ", mappedLicenseHintType.getName());
            String rsqlQuery2 = "name=='" + QueryUtils.escapeReservedChars(mappedLicenseHintType.getName()) + "'";
            List<LicenseHintType> licenseHintTypeList = dbStore.getAllLicenseHintType(Optional.of(rsqlQuery2));
            LicenseHintType licenseHintType = (licenseHintTypeList != null && !licenseHintTypeList.isEmpty())
                    ? licenseHintTypeList.get(0) : null;

            if (licenseHintType == null) {
                licenseHintType = dbStore.saveLicenseHintType(mappedLicenseHintType);

                log.debugf("# created license hint type: %s ", licenseHintType);
            } else {
                log.debugf("# retrieved license hint type: %s ", licenseHintType);
            }

            // Create or retrieve existing project version license hint
            ProjectVersionLicenseHint projectVersionLicenseHint = null;

            log.debugf(
                    "### Finding already existing project version license hints with value: %s and project version license id: %d and license hint type id: %d ",
                    projectVersionLicenseHintRest.getValue(), projectVersionLicense.getId(), licenseHintType.getId());

            String rsql3 = "projectVersionLicense.id==" + projectVersionLicense.getId() + ";licenseHintType.id=="
                    + licenseHintType.getId() + ";value=='"
                    + QueryUtils.escapeReservedChars(projectVersionLicenseHintRest.getValue()) + "'";
            List<ProjectVersionLicenseHint> projectVersionLicenseHintList = dbStore
                    .getAllProjectVersionLicenseHint(Optional.of(rsql3));
            if (projectVersionLicenseHintList == null || projectVersionLicenseHintList.isEmpty()) {

                projectVersionLicenseHint = ProjectVersionLicenseHint.Builder.newBuilder()
                        .value(projectVersionLicenseHintRest.getValue()).projectVersionLicense(projectVersionLicense)
                        .licenseHintType(licenseHintType).build();
                projectVersionLicenseHint = dbStore.saveProjectVersionLicenseHint(projectVersionLicenseHint);

                log.debugf("# created project version license hint: %s ", projectVersionLicenseHint);
            } else {
                projectVersionLicenseHint = projectVersionLicenseHintList.get(0);

                log.debugf("# retrieved project version license hint: %s ", projectVersionLicenseHint);
            }
        }
    }

}
