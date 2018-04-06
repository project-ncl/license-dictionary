package org.jboss.license.dictionary;

import static org.jboss.license.dictionary.utils.Mappers.fullMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
import org.jboss.logging.Logger;

import api.LicenseHintTypeRest;
import api.LicenseRest;
import api.ProjectEcosystemRest;
import api.ProjectRest;
import api.ProjectVersionLicenseCheckRest;
import api.ProjectVersionLicenseHintRest;
import api.ProjectVersionLicenseRest;
import api.ProjectVersionRest;

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

        projectRest = fullMapper.map(entity, ProjectRest.class);
        return projectRest;
    }

    public ProjectRest updateProject(ProjectRest projectRest) {
        Project entity = fullMapper.map(projectRest, Project.class);
        entity = dbStore.updateProject(entity);

        projectRest = fullMapper.map(entity, ProjectRest.class);
        return projectRest;
    }

    @Transactional
    public boolean deleteProject(Integer projectId) {
        boolean result = dbStore.deleteProject(projectId);
        return result;
    }

    public Optional<ProjectRest> getProjectById(Integer projectId) {
        log.debugf("Get project by id %d", projectId);
        Project project = dbStore.getProjectById(projectId);
        if (project == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(project, ProjectRest.class));
    }

    public Optional<ProjectRest> getProjectByEcosystemKey(String ecosystemName, String key) {
        log.debugf("Get project by ecosystem %s and key %s ...", ecosystemName, key);
        System.out.println("Get project by ecosystem " + ecosystemName + " and key " + key);

        Project project = dbStore.getProjectByEcosystemKey(ecosystemName, key);
        if (project == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(project, ProjectRest.class));
    }

    public List<ProjectRest> getAllProject() {
        log.debug("Get all project ...");

        return dbStore.getAllProject().stream().map(entity -> fullMapper.map(entity, ProjectRest.class))
                .collect(Collectors.toList());
    }

    public List<ProjectRest> getAllProjectByEcosystem(String ecosystemName) {
        log.debugf("Get all project by ecosystem %s ...", ecosystemName);

        return dbStore.getAllProjectByEcosystem(ecosystemName).stream().map(entity -> fullMapper.map(entity, ProjectRest.class))
                .collect(Collectors.toList());
    }

    //
    public ProjectEcosystemRest saveProjectEcosystem(ProjectEcosystemRest projectEcosystemRest) {
        ProjectEcosystem entity = fullMapper.map(projectEcosystemRest, ProjectEcosystem.class);
        entity = dbStore.saveProjectEcosystem(entity);

        projectEcosystemRest = fullMapper.map(entity, ProjectEcosystemRest.class);
        return projectEcosystemRest;
    }

    public Optional<ProjectEcosystemRest> getProjectEcosystemById(Integer projectEcosystemId) {
        log.infof("Get project ecosystems by id %d", projectEcosystemId);

        ProjectEcosystem projectEcosystem = dbStore.getProjectEcosystemById(projectEcosystemId);
        if (projectEcosystem == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(projectEcosystem, ProjectEcosystemRest.class));
    }

    public List<ProjectEcosystemRest> getAllProjectEcosystem() {
        log.info("Get all project ecosystem ...");
        return dbStore.getAllProjectEcosystem().stream().map(entity -> fullMapper.map(entity, ProjectEcosystemRest.class))
                .collect(Collectors.toList());
    }

    public Optional<ProjectEcosystemRest> getProjectEcosystemByName(String name) {
        log.infof("Get project ecosystems by name  %s", name);

        ProjectEcosystem projectEcosystem = dbStore.getProjectEcosystemByName(name);
        if (projectEcosystem == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(fullMapper.map(projectEcosystem, ProjectEcosystemRest.class));
    }

    //
    public ProjectVersionRest saveProjectVersion(ProjectVersionRest projectVersionRest) {
        ProjectVersion entity = fullMapper.map(projectVersionRest, ProjectVersion.class);
        entity = dbStore.saveProjectVersion(entity);

        projectVersionRest = fullMapper.map(entity, ProjectVersionRest.class);
        return projectVersionRest;
    }

    public Optional<ProjectVersionRest> getProjectVersionById(Integer projectVersionId) {
        log.infof("Get project version by id %d", projectVersionId);
        return Optional.ofNullable(fullMapper.map(dbStore.getProjectVersionById(projectVersionId), ProjectVersionRest.class));
    }

    public List<ProjectVersionRest> getAllProjectVersionByProjectId(Integer projectId) {
        log.infof("Get all project versions by project id:  %d", projectId);
        return dbStore.getAllProjectVersionByProjectId(projectId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionRest.class)).collect(Collectors.toList());
    }

    public Optional<ProjectVersionRest> getProjectVersionByVersionProjectId(String version, Integer projectId) {
        log.debugf("Get project version by version %s and projectId %d ...", version, projectId);

        ProjectVersion projectVersion = dbStore.getProjectVersionByVersionProjectId(version, projectId);
        if (projectVersion == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(projectVersion, ProjectVersionRest.class));
    }

    //
    public List<ProjectVersionLicenseCheckRest> getAllProjectVersionLicenseCheckByProjVersId(Integer projectVersionId) {

        log.infof("Get project version license check by projectVersionId %d ...", projectVersionId);
        return dbStore.getAllProjectVersionLicenseCheckByProjVersId(projectVersionId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseCheckRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseCheckRest> getAllProjectVersionLicenseCheckByProjVersIdLicDeteTypeId(
            Integer projectVersionId, Integer licenseDeterminationTypeId) {

        log.infof("Get project version license check by projectVersionId %d and license determination type id %d ...",
                projectVersionId, licenseDeterminationTypeId);

        return dbStore.getAllProjectVersionLicenseCheckByProjVersIdLicDeteTypeId(projectVersionId, licenseDeterminationTypeId)
                .stream().map(entity -> fullMapper.map(entity, ProjectVersionLicenseCheckRest.class))
                .collect(Collectors.toList());
    }

    public ProjectVersionLicenseCheckRest saveProjectVersionLicenseCheck(
            ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest) {
        ProjectVersionLicenseCheck entity = fullMapper.map(projectVersionLicenseCheckRest, ProjectVersionLicenseCheck.class);
        entity = dbStore.saveProjectVersionLicenseCheck(entity);

        projectVersionLicenseCheckRest = fullMapper.map(entity, ProjectVersionLicenseCheckRest.class);
        return projectVersionLicenseCheckRest;
    }

    //
    public List<ProjectVersionLicenseRest> getAllProjectVersionLicense() {

        log.info("Get all project version license ...");
        return dbStore.getAllProjectVersionLicense().stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public Optional<ProjectVersionLicenseRest> getProjectVersionLicenseById(Integer projectVersionLicenseId) {
        log.infof("Get project version license by id %d", projectVersionLicenseId);
        return Optional.ofNullable(
                fullMapper.map(dbStore.getProjectVersionLicenseById(projectVersionLicenseId), ProjectVersionLicenseRest.class));
    }

    public List<ProjectVersionLicenseRest> getAllProjectVersionLicenseByProjVersLicCheckId(Integer projVersLicCheckId) {

        log.infof("Get all project version license by project version license check %d ", projVersLicCheckId);

        return dbStore.getAllProjectVersionLicenseByProjVersLicCheckId(projVersLicCheckId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseRest> getAllProjectVersionLicenseByLicIdProjVersLicCheckcId(Integer licenseId,
            Integer projVersLicCheckId) {

        log.infof("Get all project version license by project version license check %d and license %d ", projVersLicCheckId,
                licenseId);
        return dbStore.getAllProjectVersionLicenseByLicIdProjVersLicCheckId(licenseId, projVersLicCheckId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseRest> getAllProjectVersionLicenseByScopeLicIdProjVersLicId(String scope, Integer licenseId,
            Integer projVersLicCheckId) {

        log.infof("Get all project version license by project version license check %d and license %d and scope %s ",
                projVersLicCheckId, licenseId, scope);
        return dbStore.getAllProjectVersionLicenseByScopeLicIdProjVersLicCheckId(scope, licenseId, projVersLicCheckId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseRest> getProjectVersionLicenseByEcosystemProjKeyVersion(String ecosystem, String key,
            String version) {
        log.infof("Get all project version license by ecosystem %s, key %s and version %s ...", ecosystem, key, version);
        return dbStore.getProjectVersionLicenseByEcosystemProjKeyVersion(ecosystem, key, version).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseRest> getProjectVersionLicenseByEcosystemProjKeyVersionScope(String ecosystem, String key,
            String version, String scope) {
        log.infof("Get all project version license by ecosystem %s, key %s, version %s and scope %s, ...", ecosystem, key,
                version, scope);
        return dbStore.getProjectVersionLicenseByEcosystemProjKeyVersionScope(ecosystem, key, version, scope).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseRest.class)).collect(Collectors.toList());
    }

    public ProjectVersionLicenseRest saveProjectVersionLicense(ProjectVersionLicenseRest projectVersionLicenseRest) {
        ProjectVersionLicense entity = fullMapper.map(projectVersionLicenseRest, ProjectVersionLicense.class);
        entity = dbStore.saveProjectVersionLicense(entity);

        projectVersionLicenseRest = fullMapper.map(entity, ProjectVersionLicenseRest.class);
        return projectVersionLicenseRest;
    }

    public List<ProjectVersionLicenseHintRest> getAllProjectVersionLicenseHintByProjVersLicId(Integer projectVersionLicenseId) {
        log.infof("Get all project version license hint by projectVersionLicenseId %d", projectVersionLicenseId);

        return dbStore.getAllProjectVersionLicenseHintByProjVersLicId(projectVersionLicenseId).stream()
                .map(entity -> fullMapper.map(entity, ProjectVersionLicenseHintRest.class)).collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseHintRest> getAllProjectVersionLicenseHintByProjVersLicIdLicHintTypeId(
            Integer projectVersionLicenseId, Integer licenseHintTypeId) {
        log.infof("Get all project version license hint by projectVersionLicenseId %d, licenseHintTypeId %d",
                projectVersionLicenseId, licenseHintTypeId);

        return dbStore.getAllProjectVersionLicenseHintByProjVersLicIdLicHintTypeId(projectVersionLicenseId, licenseHintTypeId)
                .stream().map(entity -> fullMapper.map(entity, ProjectVersionLicenseHintRest.class))
                .collect(Collectors.toList());
    }

    public List<ProjectVersionLicenseHintRest> getAllProjectVersionLicenseHintByValueProjVersLicIdLicHintType(String value,
            Integer projectVersionLicenseId, Integer licenseHintTypeId) {
        log.debugf("Get all project version license hint by value %s, projectVersionLicenseId %d, licenseHintTypeId %d ...",
                value, projectVersionLicenseId, licenseHintTypeId);

        return dbStore
                .getAllProjectVersionLicenseHintByValueProjVersLicIdLicHintType(value, projectVersionLicenseId,
                        licenseHintTypeId)
                .stream().map(entity -> fullMapper.map(entity, ProjectVersionLicenseHintRest.class))
                .collect(Collectors.toList());
    }

    public ProjectVersionLicenseHintRest saveProjectVersionLicenseHint(
            ProjectVersionLicenseHintRest projectVersionLicenseHint) {

        ProjectVersionLicenseHint entity = fullMapper.map(projectVersionLicenseHint, ProjectVersionLicenseHint.class);
        entity = dbStore.saveProjectVersionLicenseHint(entity);

        projectVersionLicenseHint = fullMapper.map(entity, ProjectVersionLicenseHintRest.class);
        return projectVersionLicenseHint;
    }

    @Transactional
    public void importProjectLicenses(JsonProjectLicense[] jsonProjectLicenses) {
        log.infof("started appending %d Project Licenses...", jsonProjectLicenses.length);

        ProjectEcosystem ecosystem = dbStore.getProjectEcosystemByName(ProjectEcosystem.MAVEN);
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
            List<LicenseRest> licenses = licenseStore
                    .findByExactSearchTerm(projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName())
                    .stream().collect(Collectors.toList());

            if (licenses == null || licenses.isEmpty()) {
                log.infof("\n\n*** NO LICENSE FOUND WITH NAME, SKIPPING ... %s ",
                        projectVersionLicenseHintRest.getProjectVersionLicense().getLicense().getSpdxName());
                continue;
            }

            License license = dbStore.getLicenseById(licenses.get(0).getId());

            // Create or retrieve existing Project
            ProjectVersionLicenseCheckRest projVersLicenseCheckRest = projectVersionLicenseHintRest.getProjectVersionLicense()
                    .getProjectVersionLicenseCheck();
            Project mappedProject = fullMapper.map(projVersLicenseCheckRest.getProjectVersion().getProject(), Project.class);

            log.debugf("### Finding already existing projects with ecosystem: %s and key: %s ",
                    mappedProject.getProjectEcosystem().getName(), mappedProject.getKey());

            Project project = dbStore.getProjectByEcosystemKey(mappedProject.getProjectEcosystem().getName(),
                    mappedProject.getKey());

            if (project == null) {

                log.debugf("### Finding already existing project ecosystems with name: %s ",
                        mappedProject.getProjectEcosystem().getName());
                ProjectEcosystem projectEcosystem = dbStore
                        .getProjectEcosystemByName(mappedProject.getProjectEcosystem().getName());

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
            ProjectVersion projectVersion = dbStore.getProjectVersionByVersionProjectId(mappedProjectVersion.getVersion(),
                    mappedProjectVersion.getProject().getId());

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
                    mappedLicenseDeterminationType.getId());

            List<ProjectVersionLicenseCheck> projectVersionLicenseCheckList = dbStore
                    .getAllProjectVersionLicenseCheckByProjVersIdLicDeteTypeId(projectVersion.getId(),
                            licenseDeterminationType.getId());

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

                projectVersionLicenseList = dbStore.getAllProjectVersionLicenseByScopeLicIdProjVersLicCheckId(
                        projectVersionLicenseHintRest.getProjectVersionLicense().getScope(), license.getId(),
                        projectVersionLicenseCheck.getId());
            } else {

                projectVersionLicenseList = dbStore.getAllProjectVersionLicenseByLicIdProjVersLicCheckId(license.getId(),
                        projectVersionLicenseCheck.getId());
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
            LicenseHintType licenseHintType = dbStore.getLicenseHintTypeByName(mappedLicenseHintType.getName());
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

            List<ProjectVersionLicenseHint> projectVersionLicenseHintList = dbStore
                    .getAllProjectVersionLicenseHintByValueProjVersLicIdLicHintType(projectVersionLicenseHintRest.getValue(),
                            projectVersionLicense.getId(), licenseHintType.getId());
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
