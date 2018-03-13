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

import api.FullLicenseData;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.license.dictionary.utils.Mappers.fullMapper;
import static org.jboss.license.dictionary.utils.Mappers.licenseEntityListType;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
@ApplicationScoped
public class LicenseStore {
    private static final Logger log = Logger.getLogger(LicenseStore.class);

    @Inject
    private LicenseDbStore dbStore;

    private Map<Integer, FullLicenseData> licensesById;

    @Transactional
    public synchronized void init() {
        if (licensesById != null) {
            log.warn("License Store was already initialized! skipping");
            return;
        } else {
            log.warn("Started License Store initialization");
            load();
            log.info("Finished License Store initialization");
        }
    }

    public Optional<FullLicenseData> getById(Integer licenseId) {
        return Optional.ofNullable(licensesById.get(licenseId));
    }

    public Optional<FullLicenseData> getForName(String name) {
        return findSingle(l -> searchEquals(name, l.getName()));
    }

    public Optional<FullLicenseData> getForUrl(String url) {
        return findSingle(l -> searchEquals(url, l.getUrl()));
    }

    public Optional<FullLicenseData> getForNameAlias(String name) {
        return findSingle(l -> isOneOf(l.getNameAliases(), name));
    }

    public Optional<FullLicenseData> getForUrlAlias(String url) {
        return findSingle(l -> isOneOf(l.getUrlAliases(), url));
    }

    public FullLicenseData save(FullLicenseData license) {
        LicenseEntity entity = fullMapper.map(license, LicenseEntity.class);
        entity = dbStore.save(entity);

        license = fullMapper.map(entity, FullLicenseData.class);
        licensesById.put(entity.getId(), license);
        return license;
    }

    public List<FullLicenseData> getAll() {
        ArrayList<FullLicenseData> result = new ArrayList<>(licensesById.values());
        result.sort(
                Comparator.comparing(FullLicenseData::getName)
        );
        return result;
    }

    public Set<FullLicenseData> findBySearchTerm(String denormalizedSearchTerm) {
        final String searchTerm = denormalizedSearchTerm.toLowerCase();
        Set<FullLicenseData> resultSet = new TreeSet<>(Comparator.comparing(FullLicenseData::getName));

        resultSet.addAll(
                join(getForName(searchTerm),
                        getForUrl(searchTerm),
                        getForNameAlias(searchTerm),
                        getForUrlAlias(searchTerm))
        );

        licensesById.values()
                .stream()
                .filter(l -> l.toFullString().toLowerCase().contains(searchTerm))
                .forEach(resultSet::add);

        return resultSet;
    }

    <T> List<T> join(Optional<T>... optionals) {
        return Stream.of(optionals)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean delete(Integer licenseId) {
        boolean result = dbStore.delete(licenseId);
        licensesById.remove(licenseId);
        return result;
    }

    @Transactional
    public void replaceAllLicensesWith(Collection<FullLicenseData> licenses) {
        log.infof("started replacing licenses with import data. Licenses to import: %d", licenses.size());
        List<LicenseEntity> entityList = fullMapper.map(licenses, licenseEntityListType);
        log.infof("will replace existing entities with %d new entities", entityList.size());
        dbStore.replaceAllLicensesWith(entityList);
        log.info("started loading imported entities");
        load();
        log.info("finished loading imported entities");
    }

    @Transactional
    public synchronized void load() {
        licensesById = dbStore.getAll().stream()
                .map(entity -> fullMapper.map(entity, FullLicenseData.class))
                .peek(FullLicenseData::checkIntegrity)
                .collect(Collectors.toMap(l -> l.getId(), l -> l));
    }

    private Optional<FullLicenseData> findSingle(Predicate<FullLicenseData> predicate) {
        return licensesById.values().stream()
                .filter(predicate)
                .findAny();
    }

    private boolean isOneOf(Collection<String> values, String value) {
        if (value == null) {
            return false;
        }
        String normalizedValue = value.toLowerCase();
        return values.stream()
                .anyMatch(v -> v.toLowerCase().equals(normalizedValue));
    }

    private boolean searchEquals(String value1, String value2) {
        return value1 != null
                && value2 != null
                && value1.toLowerCase().equals(value2.toLowerCase());
    }
}
