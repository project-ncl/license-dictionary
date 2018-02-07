package api;

import org.jboss.license.dictionary.model.ProjectVersionLicense;

import lombok.Getter;
import lombok.Setter;

public class ProjectVersionLicenseRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String scope;

    @Getter
    @Setter
    private LicenseRest license;

    @Getter
    @Setter
    private ProjectVersionLicenseCheckRest projectVersionLicenseCheck;

    public ProjectVersionLicenseRest() {
    }

    public ProjectVersionLicenseRest(ProjectVersionLicense projectVersionLicense) {
        this.id = projectVersionLicense.getId();
        this.scope = projectVersionLicense.getScope();
        this.license = new LicenseRest(projectVersionLicense.getLicense());
        this.projectVersionLicenseCheck = new ProjectVersionLicenseCheckRest(
                projectVersionLicense.getProjectVersionLicenseCheck());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseRest projectVersionLicenseRest = (ProjectVersionLicenseRest) o;

        if (id != null ? !id.equals(projectVersionLicenseRest.id) : projectVersionLicenseRest.id != null)
            return false;
        if (scope != null ? !scope.equals(projectVersionLicenseRest.scope) : projectVersionLicenseRest.scope != null)
            return false;
        if (license != null ? !license.equals(projectVersionLicenseRest.license) : projectVersionLicenseRest.license != null)
            return false;
        if (projectVersionLicenseCheck != null
                ? !projectVersionLicenseCheck.equals(projectVersionLicenseRest.projectVersionLicenseCheck)
                : projectVersionLicenseRest.projectVersionLicenseCheck != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (license != null ? license.hashCode() : 0);
        result = 31 * result + (projectVersionLicenseCheck != null ? projectVersionLicenseCheck.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseRest{" + "id=" + id + ", scope='" + scope + '\'' + ", license='" + license + '\''
                + ", projectVersionLicenseCheck='" + projectVersionLicenseCheck + '\'' + '}';
    }
}
