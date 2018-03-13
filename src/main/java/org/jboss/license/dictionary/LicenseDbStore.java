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

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/17/17
 */
@ApplicationScoped
public class LicenseDbStore {

    private static final Logger log = Logger.getLogger(LicenseDbStore.class);

    @PersistenceContext
    private EntityManager entityManager;

    public LicenseEntity save(LicenseEntity license) {
        entityManager.persist(license);
        return license;
    }

    public List<LicenseEntity> getAll() {
        return entityManager.createQuery("SELECT e FROM LicenseEntity e", LicenseEntity.class)
                .getResultList();
    }

    public boolean delete(Integer licenseId) {
        LicenseEntity entity = entityManager.find(LicenseEntity.class, licenseId);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    public void replaceAllLicensesWith(Collection<LicenseEntity> entities) {
        getAll().forEach(entityManager::remove);
        entities.forEach(entityManager::persist);
    }
}
