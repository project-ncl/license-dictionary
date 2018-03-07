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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseAlias;
import org.jboss.license.dictionary.model.LicenseApprovalStatus;
import org.jboss.logging.Logger;

import api.LicenseAliasRest;
import api.LicenseRest;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 8/31/17
 */
@ApplicationScoped
public class LicenseStore {
    private static final Logger log = Logger.getLogger(LicenseStore.class);

    @Inject
    private LicenseDbStore dbStore;

    private Map<Integer, LicenseRest> licensesById;

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

    @Transactional
    public synchronized void forceReload() {
        licensesById = null;
        init();
    }

    public Optional<LicenseRest> getById(Integer licenseId) {
        return Optional.ofNullable(licensesById.get(licenseId));
    }

    public Optional<LicenseRest> getForFedoraName(String name) {
        return findSingle(l -> searchEquals(name, l.getFedoraName()));
    }

    public Optional<LicenseRest> getForSpdxName(String name) {
        return findSingle(l -> searchEquals(name, l.getSpdxName()));
    }

    // public Optional<LicenseRest> getForUrl(String url) {
    // return findSingle(l -> searchEquals(url, l.getUrl()));
    // }
    //
    // public Optional<LicenseRest> getForTextUrl(String url) {
    // return findSingle(l -> searchEquals(url, l.getTextUrl()));
    // }

    public Optional<LicenseRest> getForNameAlias(String alias) {
        return findSingle(l -> isOneOf(l.getAliasNames(), alias));
    }

    public Optional<LicenseRest> getForCode(String code) {
        return findSingle(l -> searchEquals(code, l.getCode()));
    }

    public LicenseRest save(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.save(entity);

        licenseRest = fullMapper.map(entity, LicenseRest.class);
        licensesById.put(entity.getId(), licenseRest);
        return licenseRest;
    }

    public LicenseRest update(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.update(entity);

        licenseRest = fullMapper.map(entity, LicenseRest.class);
        licensesById.put(entity.getId(), licenseRest);
        return licenseRest;
    }

    public List<LicenseRest> getAll() {
        ArrayList<LicenseRest> result = new ArrayList<>(licensesById.values());
        result.sort(Comparator.comparing(LicenseRest::getCode));
        return result;
    }

    public Set<LicenseRest> findBySearchTerm(String denormalizedSearchTerm) {
        final String searchTerm = denormalizedSearchTerm.toLowerCase();
        Set<LicenseRest> resultSet = new TreeSet<>(Comparator.comparing(LicenseRest::getCode));

        resultSet.addAll(join(getForFedoraName(searchTerm), getForSpdxName(searchTerm), getForNameAlias(searchTerm),
                getForCode(searchTerm)));

        licensesById.values().stream().filter(l -> l.toString().toLowerCase().contains(searchTerm)).forEach(resultSet::add);

        return resultSet;
    }

    <T> List<T> join(Optional<T>... optionals) {
        return Stream.of(optionals).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Transactional
    public boolean delete(Integer licenseId) {
        boolean result = dbStore.deleteLicense(licenseId);
        licensesById.remove(licenseId);
        return result;
    }

    @Transactional
    public void replaceAllLicensesWith(Map<String, Collection<LicenseRest>> licensesByName) {
        log.infof("started replacing licenses with import data. Licenses to import: %d", licensesByName.values().size());

        Map<String, License> licenseEntityByAlias = new HashMap<String, License>();
        licensesByName.keySet().stream().forEach(alias -> {
            License lic = fullMapper.map(licensesByName.get(alias).iterator().next(), License.class);
            LicenseApprovalStatus las = dbStore.getLicenseApprovalStatus(lic.getLicenseApprovalStatus().getId());
            lic.setLicenseApprovalStatus(las);
            las.addLicense(lic);

            licenseEntityByAlias.put(alias, lic);
        });

        log.infof("will replace existing entities with %d new entities", licenseEntityByAlias.values().size());
        dbStore.replaceAllLicensesWith(licenseEntityByAlias);
        log.info("started loading imported entities");
        load();
        log.info("finished loading imported entities");
    }

    @Transactional
    public void replaceAllLicenseAliasesWith(Map<String, Collection<LicenseAliasRest>> licensesAliasByName) {
        log.info("started replacing licens aliases with import data.");

        licensesAliasByName.keySet().stream().forEach(key -> {
            Collection<LicenseAliasRest> licensesAliases = licensesAliasByName.get(key);
            Integer licenseId = licensesAliases.iterator().next().getLicenseId();
            License license = dbStore.getLicense(licenseId);
            if (license != null) {
                license.getAliases().clear();

                licensesAliases.stream().forEach(alias -> {
                    LicenseAlias licenseAlias = LicenseAlias.Builder.newBuilder().aliasName(alias.getAliasName())
                            .license(license).build();
                    license.addAlias(licenseAlias);
                    dbStore.saveLicenseAlias(licenseAlias);
                });
                // dbStore.save(license);
            } else {
                log.infof("No license found for id: %d", licenseId);
            }
        });

        log.info("started loading imported entities");
        load();
        log.info("finished loading imported entities");
    }

    @Transactional
    public synchronized void load() {
        licensesById = dbStore.getAllLicense().stream().map(entity -> fullMapper.map(entity, LicenseRest.class))
                .peek(LicenseRest::checkIntegrity).collect(Collectors.toMap(l -> l.getId(), l -> l));
    }

    private Optional<LicenseRest> findSingle(Predicate<LicenseRest> predicate) {
        return licensesById.values().stream().filter(predicate).findAny();
    }

    private boolean isOneOf(Collection<String> values, String value) {
        if (value == null) {
            return false;
        }
        String normalizedValue = value.toLowerCase();
        return values.stream().anyMatch(v -> v.toLowerCase().equals(normalizedValue));
    }

    private boolean searchEquals(String value1, String value2) {
        return value1 != null && value2 != null && value1.toLowerCase().equals(value2.toLowerCase());
    }

}
