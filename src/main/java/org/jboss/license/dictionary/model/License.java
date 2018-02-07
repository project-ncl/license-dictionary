package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "license", indexes = { @Index(name = "idx_license_code", columnList = "code"),
        @Index(name = "idx_license_fedora_abbrev", columnList = "fedora_abbrev"),
        @Index(name = "idx_license_spdx_abbrev", columnList = "spdx_abbrev"), }, uniqueConstraints = {
                @UniqueConstraint(name = "uc_license_code", columnNames = { "code" }) })
@ToString
@EqualsAndHashCode
public class License {

    public static final String SEQUENCE_NAME = "license_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "100") })
    @Getter
    private Integer id;

    @Size(max = 20)
    @Column(name = "fedora_abbrev", length = 20)
    @Getter
    @Setter
    private String fedoraAbbreviation;

    @Column(name = "fedora_name")
    @Getter
    @Setter
    private String fedoraName;

    @Size(max = 20)
    @Column(name = "spdx_abbrev", length = 20)
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
    @Size(max = 20)
    @Column(name = "code", length = 20)
    @Getter
    @Setter
    private String code;

    @ManyToOne
    @JoinColumn(name = "license_approval_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_license_license_approval_status"))
    @Getter
    @Setter
    private LicenseApprovalStatus licenseApprovalStatus;

    @OneToMany(mappedBy = "license")
    @Getter
    @Setter
    private Set<LicenseAlias> aliases;

    @OneToMany(mappedBy = "license")
    @Getter
    @Setter
    private Set<ProjectVersionLicense> projectVersionLicenses;

    public License() {
        this.aliases = new HashSet<LicenseAlias>();
        this.projectVersionLicenses = new HashSet<ProjectVersionLicense>();
    }

    public void addAlias(LicenseAlias licenseAlias) {
        this.aliases.add(licenseAlias);
    }

    public void addProjectVersionLicense(ProjectVersionLicense projectVersionLicense) {
        this.projectVersionLicenses.add(projectVersionLicense);
    }

}
