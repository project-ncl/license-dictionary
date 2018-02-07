package api;

import org.jboss.license.dictionary.ProjectVersionLicenseHintDBEntity;

import lombok.Getter;
import lombok.Setter;

public class ProjectVersionLicenseHintRest {

    @Setter
    @Getter
    private Integer id;

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private ProjectVersionLicenseRest projectVersionLicense;

    @Getter
    @Setter
    private LicenseHintTypeRest licenseHintType;

    public ProjectVersionLicenseHintRest() {
    }

    public ProjectVersionLicenseHintRest(ProjectVersionLicenseHintDBEntity projectVersionLicenseHint) {
        this.id = projectVersionLicenseHint.getId();
        this.value = projectVersionLicenseHint.getValue();
        this.projectVersionLicense = new ProjectVersionLicenseRest(projectVersionLicenseHint.getProjectVersionLicense());
        this.licenseHintType = new LicenseHintTypeRest(projectVersionLicenseHint.getLicenseHintType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseHintRest projectVersionLicenseHintRest = (ProjectVersionLicenseHintRest) o;

        if (id != null ? !id.equals(projectVersionLicenseHintRest.id) : projectVersionLicenseHintRest.id != null)
            return false;
        if (value != null ? !value.equals(projectVersionLicenseHintRest.value) : projectVersionLicenseHintRest.value != null)
            return false;
        if (projectVersionLicense != null ? !projectVersionLicense.equals(projectVersionLicenseHintRest.projectVersionLicense)
                : projectVersionLicenseHintRest.projectVersionLicense != null)
            return false;
        if (licenseHintType != null ? !licenseHintType.equals(projectVersionLicenseHintRest.licenseHintType)
                : projectVersionLicenseHintRest.licenseHintType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (projectVersionLicense != null ? projectVersionLicense.hashCode() : 0);
        result = 31 * result + (licenseHintType != null ? licenseHintType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseHintRest{" + "id=" + id + ", value='" + value + '\'' + ", projectVersionLicense='"
                + projectVersionLicense + '\'' + ", licenseHintType='" + licenseHintType + '\'' + '}';
    }

}
