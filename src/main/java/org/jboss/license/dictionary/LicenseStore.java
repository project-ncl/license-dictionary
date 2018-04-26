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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.license.dictionary.api.LicenseAliasRest;
import org.jboss.license.dictionary.api.LicenseApprovalStatusRest;
import org.jboss.license.dictionary.api.LicenseDeterminationTypeRest;
import org.jboss.license.dictionary.api.LicenseHintTypeRest;
import org.jboss.license.dictionary.api.LicenseRest;
import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseAlias;
import org.jboss.license.dictionary.model.LicenseApprovalStatus;
import org.jboss.license.dictionary.model.LicenseDeterminationType;
import org.jboss.license.dictionary.model.LicenseHintType;
import org.jboss.license.dictionary.utils.QueryUtils;
import org.jboss.logging.Logger;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 8/31/17
 */
@ApplicationScoped
public class LicenseStore {
    private static final Logger log = Logger.getLogger(LicenseStore.class);

    @Inject
    private LicenseDbStore dbStore;

    /* LICENSE */

    public List<LicenseRest> getAllLicense(Optional<String> rsqlSearch) {

        return dbStore.getAllLicense(rsqlSearch).stream().map(entity -> fullMapper.map(entity, LicenseRest.class))
                .collect(Collectors.toList());
    }

    public Optional<LicenseRest> getLicenseById(Integer licenseId) {
        log.infof("Get license by id %d", licenseId);

        License license = dbStore.getLicenseById(licenseId);
        if (license == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(fullMapper.map(license, LicenseRest.class));
    }

    public Optional<LicenseRest> getLicenseForFedoraName(String name) {

        String rsql = "fedoraName=='" + QueryUtils.escapeReservedChars(name) + "'";
        List<LicenseRest> results = getAllLicense(Optional.of(rsql));
        if (results == null || results.isEmpty()) {
            return Optional.ofNullable(null);
        } else {
            return Optional.of(results.get(0));
        }
    }

    public Optional<LicenseRest> getLicenseForSpdxName(String name) {

        String rsql = "spdxName=='" + QueryUtils.escapeReservedChars(name) + "'";
        List<LicenseRest> results = getAllLicense(Optional.of(rsql));
        if (results == null || results.isEmpty()) {
            return Optional.ofNullable(null);
        } else {
            return Optional.of(results.get(0));
        }
    }

    public Optional<LicenseRest> getLicenseForNameAlias(String alias) {

        String rsql = "aliases.aliasName=='" + QueryUtils.escapeReservedChars(alias) + "'";
        List<LicenseRest> results = getAllLicense(Optional.of(rsql));
        if (results == null || results.isEmpty()) {
            return Optional.ofNullable(null);
        } else {
            return Optional.of(results.get(0));
        }
    }

    public Optional<LicenseRest> getLicenseForCode(String code) {

        String rsql = "code=='" + QueryUtils.escapeReservedChars(code) + "'";
        List<LicenseRest> results = getAllLicense(Optional.of(rsql));
        if (results == null || results.isEmpty()) {
            return Optional.ofNullable(null);
        } else {
            return Optional.of(results.get(0));
        }
    }

    public LicenseRest saveLicense(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.saveLicense(entity);

        return fullMapper.map(entity, LicenseRest.class);
    }

    public LicenseRest updateLicense(LicenseRest licenseRest) {
        License entity = fullMapper.map(licenseRest, License.class);
        entity = dbStore.updateLicense(entity);

        return fullMapper.map(entity, LicenseRest.class);
    }

    <T> List<T> join(Optional<T>... optionals) {
        return Stream.of(optionals).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteLicense(Integer licenseId) {
        return dbStore.deleteLicense(licenseId);
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
    }

    @Transactional
    public void replaceAllLicenseAliasesWith(Map<String, Collection<LicenseAliasRest>> licensesAliasByName) {
        log.info("started replacing licens aliases with import data.");

        licensesAliasByName.keySet().stream().forEach(key -> {

            Collection<LicenseAliasRest> licensesAliases = licensesAliasByName.get(key);
            log.infof("analyzing licensesAliases %s", licensesAliases);

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
    }

    /* LICENSE APPROVAL STATUS */

    public List<LicenseApprovalStatusRest> getAllLicenseApprovalStatus(Optional<String> rsqlSearch) {

        return dbStore.getAllLicenseApprovalStatus(rsqlSearch).stream()
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

        return fullMapper.map(entity, LicenseApprovalStatusRest.class);
    }

    /* LICENSE DETERMINATION TYPE */

    public List<LicenseDeterminationTypeRest> getAllLicenseDeterminationType(Optional<String> rsqlSearch) {

        return dbStore.getAllLicenseDeterminationType(rsqlSearch).stream()
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

        return fullMapper.map(entity, LicenseDeterminationTypeRest.class);
    }

    /* LICENSE HINT TYPE */

    public List<LicenseHintTypeRest> getAllLicenseHintType(Optional<String> rsqlSearch) {

        return dbStore.getAllLicenseHintType(rsqlSearch).stream()
                .map(entity -> fullMapper.map(entity, LicenseHintTypeRest.class)).collect(Collectors.toList());
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

        return fullMapper.map(entity, LicenseHintTypeRest.class);
    }

}
