package api;

import org.jboss.license.dictionary.model.LicenseAlias;

import lombok.Getter;
import lombok.Setter;

public class LicenseAliasRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String aliasName;

    @Getter
    @Setter
    private Integer licenseId;

    public LicenseAliasRest() {
    }

    public LicenseAliasRest(LicenseAlias licenseAlias) {
        this.id = licenseAlias.getId();
        this.aliasName = licenseAlias.getAliasName();
        this.licenseId = licenseAlias.getLicense().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseAliasRest licenseAliasRest = (LicenseAliasRest) o;

        if (id != null ? !id.equals(licenseAliasRest.id) : licenseAliasRest.id != null)
            return false;
        if (aliasName != null ? !aliasName.equals(licenseAliasRest.aliasName) : licenseAliasRest.aliasName != null)
            return false;
        if (licenseId != null ? !licenseId.equals(licenseAliasRest.licenseId) : licenseAliasRest.licenseId != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (aliasName != null ? aliasName.hashCode() : 0);
        result = 31 * result + (licenseId != null ? licenseId.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "LicenseAliasRest{" + "id=" + id + ", aliasName='" + aliasName + '\'' + ", licenseId='" + licenseId + '\'' + '}';
    }

}
