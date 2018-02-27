package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "License")
@Table(name = "license", indexes = { @Index(name = License.IDX_NAME_LICENSE_CODE, columnList = "code"),
        @Index(name = License.IDX_NAME_LICENSE_FEDORA_ABBREVIATION, columnList = "fedora_abbrev"),
        @Index(name = License.IDX_NAME_LICENSE_SPDX_ABBREVIATION, columnList = "spdx_abbrev"),
        @Index(name = License.IDX_NAME_LICENSE_LICENSE_APPROVAL_STATUS, columnList = "license_approval_status_id"), }, uniqueConstraints = {
                @UniqueConstraint(name = License.UC_NAME_LICENSE_CODE, columnNames = { "code" }) })

@ToString(exclude = { "aliases", "projectVersionLicenses" })
@EqualsAndHashCode(exclude = { "aliases", "projectVersionLicenses" })
public class License {

    public static final String SEQUENCE_NAME = "license_id_seq";
    public static final String IDX_NAME_LICENSE_CODE = "idx_license_code";
    public static final String IDX_NAME_LICENSE_FEDORA_ABBREVIATION = "idx_license_fedoraabbr";
    public static final String IDX_NAME_LICENSE_SPDX_ABBREVIATION = "idx_license_spdxabbr";
    public static final String IDX_NAME_LICENSE_LICENSE_APPROVAL_STATUS = "idx_license_licenseapprstatus";
    public static final String FK_NAME_LICENSE_LICENSE_APPROVAL_STATUS = "fk_license_licenseapprstatus";
    public static final String UC_NAME_LICENSE_CODE = "uc_license_code";

    public static final int ABBREV_MAX_LENGHT = 50;

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    @Setter
    private Integer id;

    @Size(max = ABBREV_MAX_LENGHT)
    @Column(name = "fedora_abbrev", length = ABBREV_MAX_LENGHT)
    @Getter
    @Setter
    private String fedoraAbbreviation;

    @Column(name = "fedora_name")
    @Getter
    @Setter
    private String fedoraName;

    @Size(max = ABBREV_MAX_LENGHT)
    @Column(name = "spdx_abbrev", length = ABBREV_MAX_LENGHT)
    @Getter
    @Setter
    private String spdxAbbreviation;

    @Column(name = "spdx_name")
    @Getter
    @Setter
    private String spdxName;

    @Column(name = "url")
    @Getter
    @Setter
    private String url;

    @Column(name = "text_url")
    @Getter
    @Setter
    private String textUrl;

    @NotNull
    @Size(max = ABBREV_MAX_LENGHT)
    @Column(name = "code", length = ABBREV_MAX_LENGHT)
    @Getter
    @Setter
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "license_approval_status_id", nullable = false, foreignKey = @ForeignKey(name = FK_NAME_LICENSE_LICENSE_APPROVAL_STATUS))
    @Getter
    @Setter
    private LicenseApprovalStatus licenseApprovalStatus;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<LicenseAlias> aliases;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<ProjectVersionLicense> projectVersionLicenses;

    public License() {
        this.aliases = new HashSet<LicenseAlias>();
        this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
    }

    public void addAlias(LicenseAlias licenseAlias) {
        this.aliases.add(licenseAlias);
        licenseAlias.setLicense(this);
    }

    public void addProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        this.projectVersionLicenses.add(projectVersionLicense);
        projectVersionLicense.setLicense(this);
    }

    public static class Builder {

        private Integer id;
        private String fedoraAbbreviation;
        private String fedoraName;
        private String spdxAbbreviation;
        private String spdxName;
        private String url;
        private String textUrl;
        private String code;
        private LicenseApprovalStatus licenseApprovalStatus;
        private Set<LicenseAlias> aliases;
        private Set<ProjectVersionLicense> projectVersionLicenses;

        private Builder() {
            this.aliases = new HashSet<LicenseAlias>();
            this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder fedoraAbbreviation(String fedoraAbbreviation) {
            this.fedoraAbbreviation = fedoraAbbreviation;
            return this;
        }

        public Builder fedoraName(String fedoraName) {
            this.fedoraName = fedoraName;
            return this;
        }

        public Builder spdxAbbreviation(String spdxAbbreviation) {
            this.spdxAbbreviation = spdxAbbreviation;
            return this;
        }

        public Builder spdxName(String spdxName) {
            this.spdxName = spdxName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder textUrl(String textUrl) {
            this.textUrl = textUrl;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder licenseApprovalStatus(LicenseApprovalStatus licenseApprovalStatus) {
            this.licenseApprovalStatus = licenseApprovalStatus;
            return this;
        }

        public Builder aliases(Set<LicenseAlias> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder projectVersionLicenses(Set<ProjectVersionLicense> projectVersionLicenses) {
            this.projectVersionLicenses = projectVersionLicenses;
            return this;
        }

        public License build() {
            License license = new License();
            license.id = this.id;
            license.setFedoraName(fedoraName);
            license.setFedoraAbbreviation(fedoraAbbreviation);
            license.setSpdxName(spdxName);
            license.setSpdxAbbreviation(spdxAbbreviation);
            license.setUrl(url);
            license.setTextUrl(textUrl);
            license.setCode(code);
            license.setLicenseApprovalStatus(licenseApprovalStatus);
            license.setAliases(aliases);
            license.setProjectVersionLicenses(projectVersionLicenses);

            // Set bi-directional mappings
            licenseApprovalStatus.addLicense(license);
            aliases.stream().forEach(alias -> {
                alias.setLicense(license);
            });
            projectVersionLicenses.stream().forEach(projectVersionLicense -> {
                projectVersionLicense.setLicense(license);
            });

            return license;
        }
    }

}
