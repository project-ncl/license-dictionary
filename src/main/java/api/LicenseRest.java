package api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.license.dictionary.model.License;
import org.jboss.license.dictionary.model.LicenseAlias;

import lombok.Getter;
import lombok.Setter;

public class LicenseRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String fedoraAbbreviation;

    @Getter
    @Setter
    private String fedoraName;

    @Getter
    @Setter
    private String spdxAbbreviation;

    @Getter
    @Setter
    private String spdxName;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String textUrl;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private LicenseApprovalStatusRest licenseApprovalStatus;

    @Getter
    @Setter
    private Set<LicenseAliasRest> aliases;

    public LicenseRest() {
    }

    public LicenseRest(License license) {
        this.id = license.getId();
        this.fedoraAbbreviation = license.getFedoraAbbreviation();
        this.fedoraName = license.getFedoraName();
        this.spdxAbbreviation = license.getSpdxAbbreviation();
        this.spdxName = license.getSpdxName();
        this.url = license.getUrl();
        this.textUrl = license.getTextUrl();
        this.code = license.getCode();
        this.licenseApprovalStatus = new LicenseApprovalStatusRest(license.getLicenseApprovalStatus());
        this.aliases = new HashSet<LicenseAliasRest>();

        addAliases(license.getAliases());
    }

    public static void checkIntegrity(LicenseRest licenseRest) {
        if (licenseRest.getFedoraName() == null && licenseRest.getSpdxName() == null) {
            throw new IllegalStateException("License data is incomplete " + licenseRest.toString());
        }
    }

    public List<String> getAliasNames() {
        return this.aliases.stream().map(alias -> alias.getAliasName()).collect(Collectors.toList());
    }

    private void addAliases(Set<LicenseAlias> licenseAliases) {
        licenseAliases.stream().forEach(licenseAlias -> {
            aliases.add(new LicenseAliasRest(licenseAlias));
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseRest licenseRest = (LicenseRest) o;

        if (id != null ? !id.equals(licenseRest.id) : licenseRest.id != null)
            return false;
        if (code != null ? !code.equals(licenseRest.code) : licenseRest.code != null)
            return false;
        if (fedoraAbbreviation != null ? !fedoraAbbreviation.equals(licenseRest.fedoraAbbreviation)
                : licenseRest.fedoraAbbreviation != null)
            return false;
        if (fedoraName != null ? !fedoraName.equals(licenseRest.fedoraName) : licenseRest.fedoraName != null)
            return false;
        if (spdxAbbreviation != null ? !spdxAbbreviation.equals(licenseRest.spdxAbbreviation)
                : licenseRest.spdxAbbreviation != null)
            return false;
        if (spdxName != null ? !spdxName.equals(licenseRest.spdxName) : licenseRest.spdxName != null)
            return false;
        if (url != null ? !url.equals(licenseRest.url) : licenseRest.url != null)
            return false;
        if (textUrl != null ? !textUrl.equals(licenseRest.textUrl) : licenseRest.textUrl != null)
            return false;
        if (licenseApprovalStatus != null ? !licenseApprovalStatus.equals(licenseRest.licenseApprovalStatus)
                : licenseRest.licenseApprovalStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (fedoraAbbreviation != null ? fedoraAbbreviation.hashCode() : 0);
        result = 31 * result + (fedoraName != null ? fedoraName.hashCode() : 0);
        result = 31 * result + (spdxAbbreviation != null ? spdxAbbreviation.hashCode() : 0);
        result = 31 * result + (spdxName != null ? spdxName.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (textUrl != null ? textUrl.hashCode() : 0);
        result = 31 * result + (licenseApprovalStatus != null ? licenseApprovalStatus.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "License{" + "id=" + id + ", code='" + code + '\'' + ", fedoraAbbreviation='" + fedoraAbbreviation + '\''
                + ", fedoraName='" + fedoraName + '\'' + ", spdxAbbreviation='" + spdxAbbreviation + '\'' + ", spdxName='"
                + spdxName + '\'' + ", url='" + url + '\'' + ", textUrl='" + textUrl + '\'' + ", licenseApprovalStatus='"
                + licenseApprovalStatus + '\'' + '}';
    }

}
