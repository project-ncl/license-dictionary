package api;

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

    public static class Builder {

        private Integer id;
        private String scope;
        private LicenseRest license;
        private ProjectVersionLicenseCheckRest projectVersionLicenseCheck;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder license(LicenseRest license) {
            this.license = license;
            return this;
        }

        public Builder projectVersionLicenseCheck(ProjectVersionLicenseCheckRest projectVersionLicenseCheck) {
            this.projectVersionLicenseCheck = projectVersionLicenseCheck;
            return this;
        }

        public ProjectVersionLicenseRest build() {
            ProjectVersionLicenseRest projectVersionRest = new ProjectVersionLicenseRest();
            projectVersionRest.setId(id);
            projectVersionRest.setScope(scope);
            projectVersionRest.setLicense(license);
            projectVersionRest.setProjectVersionLicenseCheck(projectVersionLicenseCheck);
            return projectVersionRest;
        }
    }
}
