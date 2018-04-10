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

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseAlias;
import org.jboss.license.dictionary.model.LicenseApprovalStatus;
import org.jboss.license.dictionary.model.LicenseDeterminationType;
import org.jboss.license.dictionary.model.LicenseHintType;
import org.jboss.license.dictionary.model.Project;
import org.jboss.license.dictionary.model.ProjectEcosystem;
import org.jboss.license.dictionary.model.ProjectVersion;
import org.jboss.license.dictionary.model.ProjectVersionLicense;
import org.jboss.license.dictionary.model.ProjectVersionLicenseCheck;
import org.jboss.license.dictionary.model.ProjectVersionLicenseHint;
import org.jboss.logging.Logger;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/17/17
 */
@ApplicationScoped
public class LicenseDbStore {

    private static final Logger log = Logger.getLogger(LicenseDbStore.class);

    @PersistenceContext
    private EntityManager entityManager;

    /* LICENSE */

    public License saveLicense(License license) {
        log.debugf("Saving license: %s", license);
        entityManager.persist(license);
        return license;
    }

    public License updateLicense(License license) {
        log.debugf("Updating license: %s", license);
        license = entityManager.merge(license);
        return license;
    }

    public License getLicenseById(Integer id) {
        log.debugf("Get license by id:  %d", id);
        return entityManager.find(License.class, id);
    }

    public List<License> getAllLicense() {
        log.debug("Get all license ...");
        return entityManager.createNamedQuery(License.QUERY_FIND_ALL_UNORDERED, License.class).getResultList();
    }

    public boolean deleteLicense(Integer id) {
        log.debugf("Deleting license: %d", id);
        License entity = entityManager.find(License.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    public void replaceAllLicensesWith(Map<String, License> licenseEntityByAlias) {
        getAllLicense().forEach(entityManager::remove);
        licenseEntityByAlias.keySet().stream().forEach(aliasName -> {

            License license = licenseEntityByAlias.get(aliasName);
            try {
                license = saveLicense(license);
                license.addAlias(LicenseAlias.Builder.newBuilder().aliasName(aliasName).license(license).build());
                license = updateLicense(license);
            } catch (Exception exc) {
                log.error(exc);
            }
        });
    }

    /* LICENSE APPROVAL STATUS */

    public LicenseApprovalStatus getLicenseApprovalStatusById(Integer id) {
        log.debugf("Get licenseApprovalStatus by id:  %d", id);
        return entityManager.find(LicenseApprovalStatus.class, id);
    }

    public List<LicenseApprovalStatus> getAllLicenseApprovalStatus() {
        log.debug("Get all license approval status ...");
        return entityManager.createNamedQuery(LicenseApprovalStatus.QUERY_FIND_ALL_UNORDERED, LicenseApprovalStatus.class)
                .getResultList();
    }

    public LicenseApprovalStatus saveLicenseApprovalStatus(LicenseApprovalStatus licenseApprovalStatus) {
        log.debugf("Saving licenseApprovalStatus: %s", licenseApprovalStatus);
        entityManager.persist(licenseApprovalStatus);
        return licenseApprovalStatus;
    }

    /* LICENSE ALIAS */

    public LicenseAlias saveLicenseAlias(LicenseAlias licenseAlias) {
        log.debugf("Saving licenseAlias: %s", licenseAlias);
        entityManager.persist(licenseAlias);
        return licenseAlias;
    }

    /* PROJECT */

    public Project updateProject(Project project) {
        log.debugf("Updating project: %s", project);
        project = entityManager.merge(project);
        return project;
    }

    public Project saveProject(Project project) {
        log.debugf("Saving project: %s", project);
        entityManager.persist(project);
        return project;
    }

    public boolean deleteProject(Integer id) {
        log.debugf("Deleting project: %d", id);
        Project entity = entityManager.find(Project.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    public Project getProjectByEcosystemKey(String ecosystemName, String key) {
        log.debugf("Get project by ecosystem %s and key %s ...", ecosystemName, key);

        List<Project> projects = entityManager.createNamedQuery(Project.QUERY_FIND_BY_ECOSYSTEM_KEY_UNORDERED, Project.class)
                .setParameter("ecosystem", ecosystemName).setParameter("key", key).getResultList();
        if (!projects.isEmpty()) {
            return projects.get(0);
        }
        return null;
    }

    public Project getProjectById(Integer id) {
        log.debugf("Get project by id:  %d", id);
        return entityManager.find(Project.class, id);
    }

    public List<Project> getAllProject() {
        log.debug("Get all project ...");
        return entityManager.createNamedQuery(Project.QUERY_FIND_ALL_UNORDERED, Project.class).getResultList();
    }

    public List<Project> getAllProjectByEcosystem(String ecosystemName) {
        log.debugf("Get all project by ecosystem %s ...", ecosystemName);
        return entityManager.createNamedQuery(Project.QUERY_FIND_BY_ECOSYSTEM_UNORDERED, Project.class)
                .setParameter("ecosystem", ecosystemName).getResultList();
    }

    /* PROJECT VERSION */

    public ProjectVersion getProjectVersionByVersionProjectId(String version, Integer projectId) {
        log.debugf("Get project version by version %s and projectId %d ...", version, projectId);

        List<ProjectVersion> projectVersions = entityManager
                .createNamedQuery(ProjectVersion.QUERY_FIND_BY_VERSION_PROJECT_ID_UNORDERED, ProjectVersion.class)
                .setParameter("version", version).setParameter("projectId", projectId).getResultList();
        if (!projectVersions.isEmpty()) {
            return projectVersions.get(0);
        }
        return null;
    }

    public ProjectVersion getProjectVersionById(Integer id) {
        log.debugf("Get project version by id:  %d", id);
        return entityManager.find(ProjectVersion.class, id);
    }

    public List<ProjectVersion> getAllProjectVersionByProjectId(Integer projectId) {
        log.debugf("Get all project versions by project id:  %d", projectId);
        return entityManager.createNamedQuery(ProjectVersion.QUERY_FIND_ALL_BY_PROJECT_ID_UNORDERED, ProjectVersion.class)
                .setParameter("projectId", projectId).getResultList();
    }

    public ProjectVersion saveProjectVersion(ProjectVersion projectVersion) {
        log.debugf("Saving projectVersion: %s", projectVersion);
        entityManager.persist(projectVersion);
        return projectVersion;
    }

    /* LICENSE DETERMINATION TYPE */

    public LicenseDeterminationType getLicenseDeterminationTypeById(Integer id) {
        log.debugf("Get license determination type by id %d ...", id);
        return entityManager.find(LicenseDeterminationType.class, id);
    }

    public List<LicenseDeterminationType> getAllLicenseDeterminationType() {
        log.debug("Get all license determination types ...");
        return entityManager.createNamedQuery(LicenseDeterminationType.QUERY_FIND_ALL_UNORDERED, LicenseDeterminationType.class)
                .getResultList();
    }

    public LicenseDeterminationType saveLicenseDeterminationType(LicenseDeterminationType licenseDeterminationType) {
        log.debugf("Saving licenseDeterminationType: %s", licenseDeterminationType);
        entityManager.persist(licenseDeterminationType);
        return licenseDeterminationType;
    }

    /* LICENSE DETERMINATION HINT */

    public LicenseHintType getLicenseHintTypeByName(String name) {
        log.debugf("Get license hint type by name %s ...", name);

        List<LicenseHintType> licenseHintTypes = entityManager
                .createNamedQuery(LicenseHintType.QUERY_FIND_BY_NAME_UNORDERED, LicenseHintType.class)
                .setParameter("name", name).getResultList();
        if (!licenseHintTypes.isEmpty()) {
            return licenseHintTypes.get(0);
        }
        return null;
    }

    public List<LicenseHintType> getAllLicenseHintType() {
        log.debug("Get all license hint type ...");
        return entityManager.createNamedQuery(LicenseHintType.QUERY_FIND_ALL_UNORDERED, LicenseHintType.class).getResultList();
    }

    public LicenseHintType getLicenseHintTypeById(Integer id) {
        log.debugf("Get license hint type id %d ...", id);
        return entityManager.find(LicenseHintType.class, id);
    }

    public LicenseHintType saveLicenseHintType(LicenseHintType licenseHintType) {
        log.debugf("Saving licenseHintType: %s", licenseHintType);
        entityManager.persist(licenseHintType);
        return licenseHintType;
    }

    /* PROJECT ECOSYSTEM */

    public ProjectEcosystem getProjectEcosystemById(Integer id) {
        log.debugf("Get project ecosystem by id %d ...", id);
        return entityManager.find(ProjectEcosystem.class, id);
    }

    public List<ProjectEcosystem> getAllProjectEcosystem() {
        log.debug("Get all project ecosystems ...");
        return entityManager.createNamedQuery(ProjectEcosystem.QUERY_FIND_ALL_UNORDERED, ProjectEcosystem.class)
                .getResultList();
    }

    public ProjectEcosystem saveProjectEcosystem(ProjectEcosystem projectEcosystem) {
        log.debugf("Saving project ecosystem: %s", projectEcosystem);
        entityManager.persist(projectEcosystem);
        return projectEcosystem;
    }

    public ProjectEcosystem getProjectEcosystemByName(String name) {
        log.debugf("Get project ecosystem by name %s ...", name);

        List<ProjectEcosystem> projectEcosystems = entityManager
                .createNamedQuery(ProjectEcosystem.QUERY_FIND_BY_NAME_UNORDERED, ProjectEcosystem.class)
                .setParameter("name", name).getResultList();
        if (!projectEcosystems.isEmpty()) {
            return projectEcosystems.get(0);
        }
        return null;
    }

    /* PROJECT VERSION LICENSE CHECK */

    public List<ProjectVersionLicenseCheck> getAllProjectVersionLicenseCheckByProjVersId(Integer projectVersionId) {
        log.debugf("Get project version license check by projectVersionId %d ...", projectVersionId);

        List<ProjectVersionLicenseCheck> projectVersionLicenseChecks = entityManager
                .createNamedQuery(ProjectVersionLicenseCheck.QUERY_FIND_BY_PROJVERSID_UNORDERED,
                        ProjectVersionLicenseCheck.class)
                .setParameter("projVersId", projectVersionId).getResultList();
        return projectVersionLicenseChecks;
    }

    public List<ProjectVersionLicenseCheck> getAllProjectVersionLicenseCheckByProjVersIdLicDeteTypeId(Integer projectVersionId,
            Integer licenseDeterminationTypeId) {
        log.debugf("Get project version license check by projectVersionId %d and licenseDeterminationTypeId %d ...",
                projectVersionId, licenseDeterminationTypeId);

        List<ProjectVersionLicenseCheck> projectVersionLicenseChecks = entityManager
                .createNamedQuery(ProjectVersionLicenseCheck.QUERY_FIND_BY_PROJVERSID_LICDETTYPE_UNORDERED,
                        ProjectVersionLicenseCheck.class)
                .setParameter("projVersId", projectVersionId).setParameter("licDetTypeId", licenseDeterminationTypeId)
                .getResultList();
        return projectVersionLicenseChecks;
    }

    public ProjectVersionLicenseCheck saveProjectVersionLicenseCheck(ProjectVersionLicenseCheck projectVersionLicenseCheck) {
        log.debugf("Saving projectVersionLicenseCheck: %s", projectVersionLicenseCheck);
        entityManager.persist(projectVersionLicenseCheck);
        return projectVersionLicenseCheck;
    }

    /* PROJECT VERSION LICENSE */

    public ProjectVersionLicense getProjectVersionLicenseById(Integer id) {
        log.debugf("Get project version license by id %d ...", id);
        return entityManager.find(ProjectVersionLicense.class, id);
    }

    public List<ProjectVersionLicense> getAllProjectVersionLicenseByProjVersLicCheckId(Integer projVersLicCheckId) {
        log.debugf("Get all project version license with project version license check id %d ...", projVersLicCheckId);

        return entityManager
                .createNamedQuery(ProjectVersionLicense.QUERY_FIND_BY_PROJVERSLICCHECKID_UNORDERED, ProjectVersionLicense.class)
                .setParameter("projVersLicCheckId", projVersLicCheckId).getResultList();
    }

    public List<ProjectVersionLicense> getAllProjectVersionLicenseByLicIdProjVersLicCheckId(Integer licenseId,
            Integer projVersLicCheckId) {
        log.debugf("Get all project version license with license id %d and project version license check id %d ...", licenseId,
                projVersLicCheckId);
        return entityManager
                .createNamedQuery(ProjectVersionLicense.QUERY_FIND_BY_LICENSEID_PROJVERSLICCHECKID_UNORDERED,
                        ProjectVersionLicense.class)
                .setParameter("licenseId", licenseId).setParameter("projVersLicCheckId", projVersLicCheckId).getResultList();
    }

    public List<ProjectVersionLicense> getAllProjectVersionLicenseByScopeLicIdProjVersLicCheckId(String scope, Integer licenseId,
            Integer projVersLicCheckId) {
        log.debugf("Get project version license with scope %s , license id %d and project version license check id %d ...", scope,
                licenseId, projVersLicCheckId);

        return entityManager
                .createNamedQuery(ProjectVersionLicense.QUERY_FIND_BY_SCOPE_LICENSEID_PROJVERSLICCHECKID_UNORDERED,
                        ProjectVersionLicense.class)
                .setParameter("scope", scope).setParameter("licenseId", licenseId)
                .setParameter("projVersLicCheckId", projVersLicCheckId).getResultList();
    }

    public List<ProjectVersionLicense> getAllProjectVersionLicense() {
        log.debug("Get all project version license ...");
        return entityManager.createNamedQuery(ProjectVersionLicense.QUERY_FIND_ALL_UNORDERED, ProjectVersionLicense.class)
                .getResultList();
    }

    public List<ProjectVersionLicense> getProjectVersionLicenseByEcosystemProjKeyVersion(String ecosystemName,
            String projectKey, String version) {
        log.debugf("Get project version license by ecosystem %s , projectName %s and version %s ...", ecosystemName, projectKey,
                version);

        List<ProjectVersionLicense> projectVersionLicenses = entityManager
                .createNamedQuery(ProjectVersionLicense.QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_UNORDERED,
                        ProjectVersionLicense.class)
                .setParameter("ecosystem", ecosystemName).setParameter("key", projectKey).setParameter("vers", version)
                .getResultList();

        return projectVersionLicenses;
    }

    public List<ProjectVersionLicense> getProjectVersionLicenseByEcosystemProjKeyVersionScope(String ecosystemName,
            String projectKey, String version, String scope) {
        log.debugf("Get project version license by ecosystem %s , projectName %s , version %s and scope %s ...", ecosystemName,
                projectKey, version, scope);

        List<ProjectVersionLicense> projectVersionLicenses = entityManager
                .createNamedQuery(ProjectVersionLicense.QUERY_FIND_BY_ECOSYSTEM_PROJKEY_VERSION_SCOPE_UNORDERED,
                        ProjectVersionLicense.class)
                .setParameter("ecosystem", ecosystemName).setParameter("key", projectKey).setParameter("vers", version)
                .setParameter("scope", scope).getResultList();

        return projectVersionLicenses;
    }

    public ProjectVersionLicense saveProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        log.debugf("Saving projectVersionLicense: %s", projectVersionLicense);
        entityManager.persist(projectVersionLicense);
        return projectVersionLicense;
    }

    /* PROJECT VERSION LICENSE HINT */

    public List<ProjectVersionLicenseHint> getAllProjectVersionLicenseHintByProjVersLicId(Integer projectVersionLicenseId) {
        log.debugf("Get all project version license hint by projectVersionLicenseId %d", projectVersionLicenseId);

        return entityManager
                .createNamedQuery(ProjectVersionLicenseHint.QUERY_FIND_BY_PROJVERSLICID_UNORDERED,
                        ProjectVersionLicenseHint.class)
                .setParameter("projVersLicenseId", projectVersionLicenseId).getResultList();
    }

    public List<ProjectVersionLicenseHint> getAllProjectVersionLicenseHintByProjVersLicIdLicHintTypeId(
            Integer projectVersionLicenseId, Integer licenseHintTypeId) {
        log.debugf("Get all project version license hint by projectVersionLicenseId %d, licenseHintTypeId %d",
                projectVersionLicenseId);

        return entityManager
                .createNamedQuery(ProjectVersionLicenseHint.QUERY_FIND_BY_PROJVERSLICID_LICHINTTYPE_UNORDERED,
                        ProjectVersionLicenseHint.class)
                .setParameter("projVersLicenseId", projectVersionLicenseId).setParameter("licHintTypeId", licenseHintTypeId)
                .getResultList();
    }

    public List<ProjectVersionLicenseHint> getAllProjectVersionLicenseHintByValueProjVersLicIdLicHintType(String value,
            Integer projectVersionLicenseId, Integer licenseHintTypeId) {
        log.debugf("Get project version license hint by value %s, projectVersionLicenseId %d, licenseHintTypeId %d ...", value,
                projectVersionLicenseId, licenseHintTypeId);

        return entityManager
                .createNamedQuery(ProjectVersionLicenseHint.QUERY_FIND_BY_VALUE_PROJVERSLICID_LICHINTTYPE_UNORDERED,
                        ProjectVersionLicenseHint.class)
                .setParameter("value", value).setParameter("projVersLicenseId", projectVersionLicenseId)
                .setParameter("licHintTypeId", licenseHintTypeId).getResultList();
    }

    public ProjectVersionLicenseHint saveProjectVersionLicenseHint(ProjectVersionLicenseHint projectVersionLicenseHint) {
        log.debugf("Saving projectVersionLicenseHint: %s", projectVersionLicenseHint);
        entityManager.persist(projectVersionLicenseHint);
        return projectVersionLicenseHint;
    }

}
