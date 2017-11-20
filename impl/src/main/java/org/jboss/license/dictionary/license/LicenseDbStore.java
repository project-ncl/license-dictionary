package org.jboss.license.dictionary.license;

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
