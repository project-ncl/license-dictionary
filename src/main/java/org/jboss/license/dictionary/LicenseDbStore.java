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

    public License save(License license) {
        log.debug("Saving license: " + license);
        entityManager.persist(license);
        return license;
    }

    public License update(License license) {
        log.debug("Updating license: " + license);
        license = entityManager.merge(license);
        return license;
    }

    public License getLicense(Integer id) {
        log.debugf("Get license by id:  %d", id);
        return entityManager.find(License.class, id);
    }

    public List<License> getAllLicense() {
        log.debug("Get all license ...");
        return entityManager.createQuery("SELECT e FROM License e", License.class).getResultList();
    }

    public boolean deleteLicense(Integer id) {
        log.debug("Deleting license: " + id);
        License entity = entityManager.find(License.class, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    public LicenseApprovalStatus getLicenseApprovalStatus(Integer id) {
        log.debugf("Get licenseApprovalStatus by id:  %d", id);
        return entityManager.find(LicenseApprovalStatus.class, id);
    }

    public List<LicenseApprovalStatus> getAllLicenseApprovalStatus() {
        log.debugf("Get get all license approval status ...");
        return entityManager.createQuery("SELECT e FROM LicenseApprovalStatus e", LicenseApprovalStatus.class).getResultList();
    }

    public LicenseAlias saveLicenseAlias(LicenseAlias licenseAlias) {
        log.debug("Saving licenseAlias: " + licenseAlias);
        entityManager.persist(licenseAlias);
        return licenseAlias;
    }

    public void replaceAllLicensesWith(Map<String, License> licenseEntityByAlias) {
        getAllLicense().forEach(entityManager::remove);
        licenseEntityByAlias.keySet().stream().forEach(aliasName -> {

            License license = licenseEntityByAlias.get(aliasName);
            try {
                license = save(license);
                license.addAlias(LicenseAlias.Builder.newBuilder().aliasName(aliasName).license(license).build());
                license = update(license);
            } catch (Exception exc) {
                log.error(exc);
            }
        });
    }

    public LicenseApprovalStatus saveLicenseApprovalStatus(LicenseApprovalStatus licenseApprovalStatus) {
        log.debug("Saving licenseApprovalStatus: " + licenseApprovalStatus);
        entityManager.persist(licenseApprovalStatus);
        return licenseApprovalStatus;
    }
}
