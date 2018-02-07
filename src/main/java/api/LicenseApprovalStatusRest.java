package api;

import org.jboss.license.dictionary.model.LicenseApprovalStatus;

import lombok.Getter;
import lombok.Setter;

public class LicenseApprovalStatusRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    public LicenseApprovalStatusRest() {
    }

    public LicenseApprovalStatusRest(LicenseApprovalStatus licenseApprovalStatus) {
        this.id = licenseApprovalStatus.getId();
        this.name = licenseApprovalStatus.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseApprovalStatusRest licenseApprovalStatusRest = (LicenseApprovalStatusRest) o;

        if (id != null ? !id.equals(licenseApprovalStatusRest.id) : licenseApprovalStatusRest.id != null)
            return false;
        if (name != null ? !name.equals(licenseApprovalStatusRest.name) : licenseApprovalStatusRest.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LicenseApprovalStatusRest{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
