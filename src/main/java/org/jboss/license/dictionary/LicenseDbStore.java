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
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

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
import org.jboss.license.dictionary.utils.rsql.CustomizedJpaPredicateVisitor;
import org.jboss.license.dictionary.utils.rsql.CustomizedPredicateBuilder;
import org.jboss.license.dictionary.utils.rsql.CustomizedPredicateBuilderStrategy;
import org.jboss.logging.Logger;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

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

    protected <T> CriteriaQuery<T> createCriteriaQuery(Class<T> type, String rsql) {
        /**
         * DUE TO A BUG IN RSQL LIBRARY (https://github.com/tennaito/rsql-jpa/pull/16), THAT GIVES
         * 
         * java.lang.ClassCastException: org.hibernate.jpa.criteria.path.SingularAttributePath cannot be cast to
         * javax.persistence.criteria.From
         * 
         * WE NEED TO USE CUSTOMIZED FIXED VERSIONS OF JPA PREDICATE VISITOR, BUILDER and STRATEGY
         * 
         */

        /*
         * example without customizers:
         * 
         * CriteriaBuilder builder = entityManager.getCriteriaBuilder();
         * 
         * CriteriaQuery<LicenseDeterminationType> query = builder.createQuery(LicenseDeterminationType.class);
         * 
         * From root = query.from(LicenseDeterminationType.class);
         * 
         * RSQLVisitor<Predicate, EntityManager> visitor = new
         * CustomizedJpaPredicateVisitor<LicenseDeterminationType>().withRoot(root).withPredicateBuilder(new
         * CustomizedPredicateBuilder()).withPredicateBuilderStrategy(new CustomizedPredicateBuilderStrategy());
         * 
         * Node rootNode = new RSQLParser().parse(rsql);
         * 
         * Predicate predicate = rootNode.accept(visitor, entityManager); query.where(predicate);
         * 
         * return entityManager.createQuery(query).getResultList();
         */

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> query = builder.createQuery(type);
        From root = query.from(type);

        RSQLVisitor<Predicate, EntityManager> visitor = new CustomizedJpaPredicateVisitor<T>().withRoot(root)
                .withPredicateBuilder(new CustomizedPredicateBuilder())
                .withPredicateBuilderStrategy(new CustomizedPredicateBuilderStrategy());

        log.debugf("### About to parse rsql %s ...", rsql);
        Node rootNode = new RSQLParser().parse(rsql);

        Predicate predicate = rootNode.accept(visitor, entityManager);
        query.where(predicate);

        return query;
    }

    protected boolean isNonEmptyRsqlString(Optional<String> rsql) {
        if (!rsql.isPresent() || "".equalsIgnoreCase(rsql.get().trim())) {
            return false;
        }
        return true;
    }

    /* LICENSE */

    public List<License> getAllLicense(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all license by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<License> query = createCriteriaQuery(License.class, rsqlSearch.get());
            query.distinct(true);
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all license ...");
        return entityManager.createNamedQuery(License.QUERY_FIND_ALL_UNORDERED, License.class).getResultList();
    }

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
        getAllLicense(Optional.ofNullable(null)).forEach(entityManager::remove);
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

    public List<LicenseApprovalStatus> getAllLicenseApprovalStatus(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all license approval status by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<LicenseApprovalStatus> query = createCriteriaQuery(LicenseApprovalStatus.class, rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all license approval status ...");
        return entityManager.createNamedQuery(LicenseApprovalStatus.QUERY_FIND_ALL_UNORDERED, LicenseApprovalStatus.class)
                .getResultList();
    }

    public LicenseApprovalStatus getLicenseApprovalStatusById(Integer id) {
        log.debugf("Get licenseApprovalStatus by id:  %d", id);
        return entityManager.find(LicenseApprovalStatus.class, id);
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

    public Project getProjectById(Integer id) {
        log.debugf("Get project by id:  %d", id);
        return entityManager.find(Project.class, id);
    }

    public List<Project> getAllProject(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all license approval status by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<Project> query = createCriteriaQuery(Project.class, rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project ...");
        return entityManager.createNamedQuery(Project.QUERY_FIND_ALL_UNORDERED, Project.class).getResultList();
    }

    /* PROJECT VERSION */

    public List<ProjectVersion> getAllProjectVersion(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all project versions by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<ProjectVersion> query = createCriteriaQuery(ProjectVersion.class, rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project versions ...");
        return entityManager.createNamedQuery(ProjectVersion.QUERY_FIND_ALL_UNORDERED, ProjectVersion.class).getResultList();
    }

    public ProjectVersion getProjectVersionById(Integer id) {
        log.debugf("Get project version by id:  %d", id);
        return entityManager.find(ProjectVersion.class, id);
    }

    public ProjectVersion saveProjectVersion(ProjectVersion projectVersion) {
        log.debugf("Saving projectVersion: %s", projectVersion);
        entityManager.persist(projectVersion);
        return projectVersion;
    }

    /* LICENSE DETERMINATION TYPE */

    public List<LicenseDeterminationType> getAllLicenseDeterminationType(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all determination type by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<LicenseDeterminationType> query = createCriteriaQuery(LicenseDeterminationType.class,
                    rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all license determination types ...");
        return entityManager.createNamedQuery(LicenseDeterminationType.QUERY_FIND_ALL_UNORDERED, LicenseDeterminationType.class)
                .getResultList();
    }

    public LicenseDeterminationType getLicenseDeterminationTypeById(Integer id) {
        log.debugf("Get license determination type by id %d ...", id);
        return entityManager.find(LicenseDeterminationType.class, id);
    }

    public LicenseDeterminationType saveLicenseDeterminationType(LicenseDeterminationType licenseDeterminationType) {
        log.debugf("Saving licenseDeterminationType: %s", licenseDeterminationType);
        entityManager.persist(licenseDeterminationType);
        return licenseDeterminationType;
    }

    /* LICENSE HINT TYPE */

    public List<LicenseHintType> getAllLicenseHintType(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all license hint type by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<LicenseHintType> query = createCriteriaQuery(LicenseHintType.class, rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

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

    public List<ProjectEcosystem> getAllProjectEcosystem(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all project ecosystems by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<ProjectEcosystem> query = createCriteriaQuery(ProjectEcosystem.class, rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project ecosystems ...");
        return entityManager.createNamedQuery(ProjectEcosystem.QUERY_FIND_ALL_UNORDERED, ProjectEcosystem.class)
                .getResultList();
    }

    public ProjectEcosystem getProjectEcosystemById(Integer id) {
        log.debugf("Get project ecosystem by id %d ...", id);
        return entityManager.find(ProjectEcosystem.class, id);
    }

    public ProjectEcosystem saveProjectEcosystem(ProjectEcosystem projectEcosystem) {
        log.debugf("Saving project ecosystem: %s", projectEcosystem);
        entityManager.persist(projectEcosystem);
        return projectEcosystem;
    }

    /* PROJECT VERSION LICENSE CHECK */

    public List<ProjectVersionLicenseCheck> getAllProjectVersionLicenseCheck(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all project version license checks by rsql %s ...", rsqlSearch.get());

            CriteriaQuery<ProjectVersionLicenseCheck> query = createCriteriaQuery(ProjectVersionLicenseCheck.class,
                    rsqlSearch.get());
            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project versions ...");
        return entityManager
                .createNamedQuery(ProjectVersionLicenseCheck.QUERY_FIND_ALL_UNORDERED, ProjectVersionLicenseCheck.class)
                .getResultList();
    }

    public ProjectVersionLicenseCheck getProjectVersionLicenseCheckById(Integer id) {
        log.debugf("Get project version license check by id:  %d", id);
        return entityManager.find(ProjectVersionLicenseCheck.class, id);
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

    public List<ProjectVersionLicense> getAllProjectVersionLicense(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all project versions license by rsql %s ...", rsqlSearch.get());

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProjectVersionLicense> query = builder.createQuery(ProjectVersionLicense.class);
            From root = query.from(ProjectVersionLicense.class);

            // To avoid a lazy init on aliases
            Fetch<ProjectVersionLicense, License> wl = root.fetch("license");
            wl.fetch("aliases");

            RSQLVisitor<Predicate, EntityManager> visitor = new CustomizedJpaPredicateVisitor<ProjectVersionLicense>()
                    .withRoot(root).withPredicateBuilder(new CustomizedPredicateBuilder())
                    .withPredicateBuilderStrategy(new CustomizedPredicateBuilderStrategy());

            Node rootNode = new RSQLParser() // create RSQLParser with default and custom operators
                    .parse(rsqlSearch.get()); // pass RQSL expression here (for example "field1 == value1 and field2 == value2")

            Predicate predicate = rootNode.accept(visitor, entityManager);
            query.where(predicate);
            query.distinct(true);

            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project version license ...");
        return entityManager.createNamedQuery(ProjectVersionLicense.QUERY_FIND_ALL_UNORDERED, ProjectVersionLicense.class)
                .getResultList();
    }

    public ProjectVersionLicense saveProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        log.debugf("Saving projectVersionLicense: %s", projectVersionLicense);
        entityManager.persist(projectVersionLicense);
        return projectVersionLicense;
    }

    /* PROJECT VERSION LICENSE HINT */

    public List<ProjectVersionLicenseHint> getAllProjectVersionLicenseHint(Optional<String> rsqlSearch) {

        if (isNonEmptyRsqlString(rsqlSearch)) {
            log.debugf("Get all project versions license by rsql %s ...", rsqlSearch.get());

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProjectVersionLicenseHint> query = builder.createQuery(ProjectVersionLicenseHint.class);
            From root = query.from(ProjectVersionLicenseHint.class);

            // // To avoid a lazy init on aliases
            // Fetch<ProjectVersionLicense, License> wl = root.fetch("license");
            // wl.fetch("aliases");

            RSQLVisitor<Predicate, EntityManager> visitor = new CustomizedJpaPredicateVisitor<ProjectVersionLicenseHint>()
                    .withRoot(root).withPredicateBuilder(new CustomizedPredicateBuilder())
                    .withPredicateBuilderStrategy(new CustomizedPredicateBuilderStrategy());

            Node rootNode = new RSQLParser() // create RSQLParser with default and custom operators
                    .parse(rsqlSearch.get()); // pass RQSL expression here (for example "field1 == value1 and field2 == value2")

            Predicate predicate = rootNode.accept(visitor, entityManager);
            query.where(predicate);
            query.distinct(true);

            return entityManager.createQuery(query).getResultList();
        }

        log.debug("Get all project versions ...");
        return entityManager
                .createNamedQuery(ProjectVersionLicenseHint.QUERY_FIND_ALL_UNORDERED, ProjectVersionLicenseHint.class)
                .getResultList();
    }

    public ProjectVersionLicenseHint getProjectVersionLicenseHintById(Integer id) {
        log.debugf("Get project version license hint by id:  %d", id);
        return entityManager.find(ProjectVersionLicenseHint.class, id);
    }

    public ProjectVersionLicenseHint saveProjectVersionLicenseHint(ProjectVersionLicenseHint projectVersionLicenseHint) {
        log.debugf("Saving projectVersionLicenseHint: %s", projectVersionLicenseHint);
        entityManager.persist(projectVersionLicenseHint);
        return projectVersionLicenseHint;
    }

}
