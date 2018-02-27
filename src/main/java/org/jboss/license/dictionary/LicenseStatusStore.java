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
