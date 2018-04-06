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
import org.jboss.license.dictionary.model.LicenseDeterminationType;
import org.jboss.license.dictionary.model.LicenseHintType;
import org.jboss.logging.Logger;

import api.LicenseAliasRest;
import api.LicenseApprovalStatusRest;
import api.LicenseDeterminationTypeRest;
import api.LicenseHintTypeRest;
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

    @Transactional
    public synchronized void load() {
        licensesById = dbStore.getAllLicense().stream().map(entity -> fullMapper.map(entity, LicenseRest.class))
                .peek(LicenseRest::checkIntegrity).collect(Collectors.toMap(l -> l.getId(), l -> l));
    }

    public Optional<LicenseRest> getLicenseById(Integer licenseId) {
        return Optional.ofNullable(licensesById.get(licenseId));
    }

    public Optional<LicenseRest> getLicenseForFedoraName(String name) {
        return findSingle(l -> searchEquals(name, l.getFedoraName()));
    }

    public Optional<LicenseRest> getLicenseForSpdxName(String name) {
        return findSingle(l -> searchEquals(name, l.getSpdxName()));
    }

    // public Optional<LicenseRest> getForUrl(String url) {
    // return findSingle(l -> searchEquals(url, l.getUrl()));
    // }
    //
    // public Optional<LicenseRest> getForTextUrl(String url) {
    // return findSingle(l -> searchEquals(url, l.getTextUrl()));
    // }

    public Optional<LicenseRest> getLicenseForNameAlias(String alias) {
        return findSingle(l -> isOneOf(l.getAliasNames(), alias));
    }

    public Optional<LicenseRest> getLicenseForCode(String code) {
        return findSingle(l -> searchEquals(code, l.getCode()));
    }

    public LicenseRest saveLicense(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.saveLicense(entity);

        licenseRest = fullMapper.map(entity, LicenseRest.class);
        licensesById.put(entity.getId(), licenseRest);
        return licenseRest;
    }

    public LicenseRest updateLicense(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.updateLicense(entity);

        licenseRest = fullMapper.map(entity, LicenseRest.class);
        licensesById.put(entity.getId(), licenseRest);
        return licenseRest;
    }

    public List<LicenseRest> getAllLicense() {
        ArrayList<LicenseRest> result = new ArrayList<>(licensesById.values());
        result.sort(Comparator.comparing(LicenseRest::getCode));
        return result;
    }

    public Set<LicenseRest> findBySearchTerm(String denormalizedSearchTerm) {
        final String searchTerm = denormalizedSearchTerm.toLowerCase();
        Set<LicenseRest> resultSet = new TreeSet<>(Comparator.comparing(LicenseRest::getCode));

        resultSet.addAll(join(getLicenseForFedoraName(searchTerm), getLicenseForSpdxName(searchTerm),
                getLicenseForNameAlias(searchTerm), getLicenseForCode(searchTerm)));

        licensesById.values().stream().filter(l -> l.toString().toLowerCase().contains(searchTerm)).forEach(resultSet::add);

        return resultSet;
    }

    public Set<LicenseRest> findByExactSearchTerm(String denormalizedSearchTerm) {
        final String searchTerm = denormalizedSearchTerm.toLowerCase();
        Set<LicenseRest> resultSet = new TreeSet<>(Comparator.comparing(LicenseRest::getCode));

        resultSet.addAll(join(getLicenseForFedoraName(searchTerm), getLicenseForSpdxName(searchTerm),
                getLicenseForNameAlias(searchTerm), getLicenseForCode(searchTerm)));

        licensesById.values().stream().filter(l -> l.toString().toLowerCase().equalsIgnoreCase(searchTerm))
                .forEach(resultSet::add);

        return resultSet;
    }

    <T> List<T> join(Optional<T>... optionals) {
        return Stream.of(optionals).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteLicense(Integer licenseId) {
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
            LicenseApprovalStatus las = dbStore.getLicenseApprovalStatusById(lic.getLicenseApprovalStatus().getId());
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
            License license = dbStore.getLicenseById(licenseId);
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

    public List<LicenseApprovalStatusRest> getAllLicenseApprovalStatus() {
        log.info("Get all license approval status ...");
        return dbStore.getAllLicenseApprovalStatus().stream()
                .map(entity -> fullMapper.map(entity, LicenseApprovalStatusRest.class)).collect(Collectors.toList());
    }

    public Optional<LicenseApprovalStatusRest> getLicenseApprovalStatusById(Integer licenseStatusId) {
        log.infof("Get license approval status by id %d", licenseStatusId);

        LicenseApprovalStatus licenseApprovalStatus = dbStore.getLicenseApprovalStatusById(licenseStatusId);
        if (licenseApprovalStatus == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(licenseApprovalStatus, LicenseApprovalStatusRest.class));
    }

    public LicenseApprovalStatusRest saveLicenseApprovalStatus(LicenseApprovalStatusRest licenseApprovalStatusRest) {
        LicenseApprovalStatus entity = fullMapper.map(licenseApprovalStatusRest, LicenseApprovalStatus.class);
        entity = dbStore.saveLicenseApprovalStatus(entity);

        licenseApprovalStatusRest = fullMapper.map(entity, LicenseApprovalStatusRest.class);
        return licenseApprovalStatusRest;
    }

    public List<LicenseDeterminationTypeRest> getAllLicenseDeterminationType() {
        log.info("Get all license determination type ...");
        return dbStore.getAllLicenseDeterminationType().stream()
                .map(entity -> fullMapper.map(entity, LicenseDeterminationTypeRest.class)).collect(Collectors.toList());
    }

    public Optional<LicenseDeterminationTypeRest> getLicenseDeterminationTypeById(Integer licenseDetTypeId) {
        log.infof("Get license determination type by id %d", licenseDetTypeId);

        LicenseDeterminationType licenseDeterminationType = dbStore.getLicenseDeterminationTypeById(licenseDetTypeId);
        if (licenseDeterminationType == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(licenseDeterminationType, LicenseDeterminationTypeRest.class));
    }

    public LicenseDeterminationTypeRest saveLicenseDeterminationType(
            LicenseDeterminationTypeRest licenseDeterminationTypeRest) {
        LicenseDeterminationType entity = fullMapper.map(licenseDeterminationTypeRest, LicenseDeterminationType.class);
        entity = dbStore.saveLicenseDeterminationType(entity);

        licenseDeterminationTypeRest = fullMapper.map(entity, LicenseDeterminationTypeRest.class);
        return licenseDeterminationTypeRest;
    }

    public List<LicenseHintTypeRest> getAllLicenseHintType() {
        log.info("Get all license hint type ...");
        return dbStore.getAllLicenseHintType().stream().map(entity -> fullMapper.map(entity, LicenseHintTypeRest.class))
                .collect(Collectors.toList());
    }

    public Optional<LicenseHintTypeRest> getLicenseHintTypeById(Integer licenseHintTypeId) {
        log.infof("Get license hint type by id %d", licenseHintTypeId);

        LicenseHintType licenseHintType = dbStore.getLicenseHintTypeById(licenseHintTypeId);
        if (licenseHintType == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(licenseHintType, LicenseHintTypeRest.class));
    }

    public LicenseHintTypeRest saveLicenseHintType(LicenseHintTypeRest licenseHintTypeRest) {
        LicenseHintType entity = fullMapper.map(licenseHintTypeRest, LicenseHintType.class);
        entity = dbStore.saveLicenseHintType(entity);

        licenseHintTypeRest = fullMapper.map(entity, LicenseHintTypeRest.class);
        return licenseHintTypeRest;
    }

    public Optional<LicenseHintTypeRest> getLicenseHintTypeByName(String name) {
        log.infof("Get license hint type by name %s", name);

        LicenseHintType licenseHintType = dbStore.getLicenseHintTypeByName(name);
        if (licenseHintType == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(licenseHintType, LicenseHintTypeRest.class));
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
