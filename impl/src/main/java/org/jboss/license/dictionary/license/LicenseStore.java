package org.jboss.license.dictionary.license;

import org.jboss.license.dictionary.utils.QueryUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
@ApplicationScoped
public class LicenseStore {
    private static final Logger log = Logger.getLogger(LicenseStore.class);

    @PersistenceContext
    private EntityManager entityManager;

    // mstodo simplify
    @Transactional
    public Optional<LicenseEntity> getById(Integer licenseId) {
        TypedQuery<LicenseEntity> query = entityManager.createQuery(
                "SELECT e FROM LicenseEntity e WHERE e.id = :id",
                LicenseEntity.class
        );
        query.setParameter("id", licenseId);
        Optional<LicenseEntity> result = QueryUtils.getSingleResult(query);
        result.ifPresent(license -> {
            license.getNameAliases().size();
            license.getUrlAliases().size();
        });
        return result;
    }

    public Optional<LicenseEntity> getForName(String name) {
        String query = "SELECT e FROM LicenseEntity e WHERE :name = e.name";

        return QueryUtils.getSingleResult(
                entityManager.createQuery(query, LicenseEntity.class)
                        .setParameter("name", name)
        );
    }

    public Optional<LicenseEntity> getForUrl(String url) {
        String query = "SELECT e FROM LicenseEntity e WHERE :url = e.url";

        return QueryUtils.getSingleResult(
                entityManager.createQuery(query, LicenseEntity.class)
                        .setParameter("url", url)
        );
    }

    public Optional<LicenseEntity> getForNameAlias(String name) {
        String query = "SELECT e FROM LicenseEntity e WHERE :name MEMBER OF e.nameAliases";

        return QueryUtils.getSingleResult(
                entityManager.createQuery(query, LicenseEntity.class)
                        .setParameter("name", name)
        );
    }

    public Optional<LicenseEntity> getForUrlAlias(String url) {
        String query = "SELECT e FROM LicenseEntity e WHERE :alias MEMBER OF e.urlAliases";

        return QueryUtils.getSingleResult(
                entityManager.createQuery(query, LicenseEntity.class)
                        .setParameter("alias", url)
        );
    }

    public LicenseEntity save(LicenseEntity license) {
        entityManager.persist(license);
        return license;
    }

    public List<LicenseEntity> getAll() {
        return entityManager.createQuery("SELECT e FROM LicenseEntity e", LicenseEntity.class)
                .getResultList();
    }

    // mstodo pagination won't work with it!
    // mstodo for now only name or url substring or exact match of alias are supported
    public Set<LicenseEntity> findBySearchTerm(String searchTerm) {
        Set<LicenseEntity> resultSet = new TreeSet<>(Comparator.comparing(LicenseEntity::getName));
        
        resultSet.addAll(
                join(getForName(searchTerm),
                        getForUrl(searchTerm),
                        getForNameAlias(searchTerm),
                        getForUrlAlias(searchTerm))
        );


        resultSet.addAll(entityManager.createQuery(
                "SELECT distinct e FROM LicenseEntity e " +
                        "WHERE e.name LIKE :searchTerm or " +
                        "e.url LIKE :searchTerm", LicenseEntity.class)
                .setParameter("searchTerm", "%" + searchTerm + "%").getResultList());

        return resultSet;
    }

    <T> List<T> join(Optional<T>... optionals) {
        return Stream.of(optionals)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean delete(Integer licenseId) {
        Query query = entityManager.createQuery("DELETE from LicenseEntity where id = :licenseId");
        query.setParameter("licenseId", licenseId);
        int deletedRows = query.executeUpdate();
        return deletedRows > 0;
    }

    @Transactional
    public void replaceAllLicensesWith(List<LicenseEntity> entities) {
        entityManager.createQuery("DELETE FROM LicenseEntity").executeUpdate();
        entities.forEach(entityManager::persist);
    }
}
