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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.license.dictionary.model.LicenseApprovalStatus;
import org.jboss.logging.Logger;

import api.LicenseApprovalStatusRest;

@ApplicationScoped
public class LicenseStatusStore {

    private static final Logger log = Logger.getLogger(LicenseStatusStore.class);

    @Inject
    private LicenseDbStore dbStore;

    public List<LicenseApprovalStatusRest> getAll() {
        log.info("Get all license approval status ...");
        return dbStore.getAllLicenseApprovalStatus().stream()
                .map(entity -> fullMapper.map(entity, LicenseApprovalStatusRest.class)).collect(Collectors.toList());
    }

    public Optional<LicenseApprovalStatusRest> getById(Integer licenseStatusId) {
        log.infof("Get license approval status by id %d", licenseStatusId);
        return Optional
                .ofNullable(fullMapper.map(dbStore.getLicenseApprovalStatus(licenseStatusId), LicenseApprovalStatusRest.class));
    }

    public LicenseApprovalStatusRest save(LicenseApprovalStatusRest licenseApprovalStatusRest) {
        LicenseApprovalStatus entity = fullMapper.map(licenseApprovalStatusRest, LicenseApprovalStatus.class);
        entity = dbStore.saveLicenseApprovalStatus(entity);

        licenseApprovalStatusRest = fullMapper.map(entity, LicenseApprovalStatusRest.class);
        return licenseApprovalStatusRest;
    }
}
