package api;

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

    public static class Builder {

        private Integer id;
        private String aliasName;
        private Integer licenseId;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder aliasName(String aliasName) {
            this.aliasName = aliasName;
            return this;
        }

        public Builder licenseId(Integer licenseId) {
            this.licenseId = licenseId;
            return this;
        }

        public LicenseAliasRest build() {
            LicenseAliasRest licenseAliasRest = new LicenseAliasRest();
            licenseAliasRest.setId(id);
            licenseAliasRest.setAliasName(aliasName);
            licenseAliasRest.setLicenseId(licenseId);
            return licenseAliasRest;
        }
    }

}
