package api;

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

    public static class Builder {

        private Integer id;
        private String name;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public LicenseHintTypeRest build() {
            LicenseHintTypeRest licenseHintTypeRest = new LicenseHintTypeRest();
            licenseHintTypeRest.setId(id);
            licenseHintTypeRest.setName(name);
            return licenseHintTypeRest;
        }
    }

}
