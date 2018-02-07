package api;

import org.jboss.license.dictionary.model.LicenseHintType;

import lombok.Getter;
import lombok.Setter;

public class LicenseHintTypeRest {

    @Setter
    @Getter
    private Integer id;

    @Getter
    @Setter
    private String name;

    public LicenseHintTypeRest() {
    }

    public LicenseHintTypeRest(LicenseHintType licenseHintType) {
        this.id = licenseHintType.getId();
        this.name = licenseHintType.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseHintTypeRest licenseHintTypeRest = (LicenseHintTypeRest) o;

        if (id != null ? !id.equals(licenseHintTypeRest.id) : licenseHintTypeRest.id != null)
            return false;
        if (name != null ? !name.equals(licenseHintTypeRest.name) : licenseHintTypeRest.name != null)
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
        return "LicenseHintTypeRest{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

}
