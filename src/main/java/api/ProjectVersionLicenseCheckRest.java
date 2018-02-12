package api;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ProjectVersionLicenseCheckRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String determinedByUser;

    @Getter
    @Setter
    private Date determinationDate;

    @Getter
    @Setter
    private String notes;

    @Getter
    @Setter
    private ProjectVersionRest projectVersion;

    @Getter
    @Setter
    private LicenseDeterminationTypeRest licenseDeterminationType;

    public ProjectVersionLicenseCheckRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest = (ProjectVersionLicenseCheckRest) o;

        if (id != null ? !id.equals(projectVersionLicenseCheckRest.id) : projectVersionLicenseCheckRest.id != null)
            return false;
        if (determinedByUser != null ? !determinedByUser.equals(projectVersionLicenseCheckRest.determinedByUser)
                : projectVersionLicenseCheckRest.determinedByUser != null)
            return false;
        if (determinationDate != null ? !determinationDate.equals(projectVersionLicenseCheckRest.determinationDate)
                : projectVersionLicenseCheckRest.determinationDate != null)
            return false;
        if (notes != null ? !notes.equals(projectVersionLicenseCheckRest.notes) : projectVersionLicenseCheckRest.notes != null)
            return false;
        if (projectVersion != null ? !projectVersion.equals(projectVersionLicenseCheckRest.projectVersion)
                : projectVersionLicenseCheckRest.projectVersion != null)
            return false;
        if (licenseDeterminationType != null
                ? !licenseDeterminationType.equals(projectVersionLicenseCheckRest.licenseDeterminationType)
                : projectVersionLicenseCheckRest.licenseDeterminationType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (determinedByUser != null ? determinedByUser.hashCode() : 0);
        result = 31 * result + (determinationDate != null ? determinationDate.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (projectVersion != null ? projectVersion.hashCode() : 0);
        result = 31 * result + (licenseDeterminationType != null ? licenseDeterminationType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionLicenseCheckRest{" + "id=" + id + ", determinedByUser='" + determinedByUser + '\''
                + ", determinationDate='" + determinationDate + '\'' + ", notes='" + notes + '\'' + ", projectVersion='"
                + projectVersion + '\'' + ", licenseDeterminationType='" + licenseDeterminationType + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String determinedByUser;
        private Date determinationDate;
        private String notes;
        private ProjectVersionRest projectVersion;
        private LicenseDeterminationTypeRest licenseDeterminationType;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder determinedByUser(String determinedByUser) {
            this.determinedByUser = determinedByUser;
            return this;
        }

        public Builder determinationDate(Date determinationDate) {
            this.determinationDate = determinationDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder projectVersion(ProjectVersionRest projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder licenseDeterminationType(LicenseDeterminationTypeRest licenseDeterminationType) {
            this.licenseDeterminationType = licenseDeterminationType;
            return this;
        }

        public ProjectVersionLicenseCheckRest build() {
            ProjectVersionLicenseCheckRest projectVersionLicenseCheckRest = new ProjectVersionLicenseCheckRest();
            projectVersionLicenseCheckRest.setId(id);
            projectVersionLicenseCheckRest.setDeterminedByUser(determinedByUser);
            projectVersionLicenseCheckRest.setDeterminationDate(determinationDate);
            projectVersionLicenseCheckRest.setNotes(notes);
            projectVersionLicenseCheckRest.setProjectVersion(projectVersion);
            projectVersionLicenseCheckRest.setLicenseDeterminationType(licenseDeterminationType);

            return projectVersionLicenseCheckRest;
        }
    }

}
