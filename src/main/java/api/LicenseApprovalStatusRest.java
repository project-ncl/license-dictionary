package api;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

public class LicenseApprovalStatusRest {

    public static final LicenseApprovalStatusRest APPROVED = LicenseApprovalStatusRest.Builder.newBuilder().id(1)
            .name("APPROVED").build();
    public static final LicenseApprovalStatusRest NOT_APPROVED = LicenseApprovalStatusRest.Builder.newBuilder().id(2)
            .name("NOT_APPROVED").build();
    public static final LicenseApprovalStatusRest UNKNOWN = LicenseApprovalStatusRest.Builder.newBuilder().id(3).name("UNKNOWN")
            .build();

    public static final Map<String, LicenseApprovalStatusRest> approvalMap = Collections.unmodifiableMap(
            Stream.of(new AbstractMap.SimpleEntry<>("yes", APPROVED), new AbstractMap.SimpleEntry<>("no", NOT_APPROVED))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    public LicenseApprovalStatusRest() {
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

    public static LicenseApprovalStatusRest fromJsonString(String approved) {
        if (approved == null) {
            return UNKNOWN;
        }

        LicenseApprovalStatusRest licenseApprovalStatusRest = approvalMap.get(approved);
        System.out.println("Got result of " + licenseApprovalStatusRest + " for the status " + approved);
        return (licenseApprovalStatusRest == null ? UNKNOWN : licenseApprovalStatusRest);
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

        public LicenseApprovalStatusRest build() {
            LicenseApprovalStatusRest licenseApprovalStatusRest = new LicenseApprovalStatusRest();
            licenseApprovalStatusRest.setId(id);
            licenseApprovalStatusRest.setName(name);
            return licenseApprovalStatusRest;
        }
    }

}
