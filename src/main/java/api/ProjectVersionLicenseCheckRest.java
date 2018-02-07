package api;

import java.util.Date;

import org.jboss.license.dictionary.ProjectVersionLicenseCheckDBEntity;

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

    public ProjectVersionLicenseCheckRest(ProjectVersionLicenseCheckDBEntity projectVersionLicenseCheck) {
        this.id = projectVersionLicenseCheck.getId();
        this.determinedByUser = projectVersionLicenseCheck.getDeterminedByUser();
        this.determinationDate = projectVersionLicenseCheck.getDeterminationDate();
        this.notes = projectVersionLicenseCheck.getNotes();
        this.projectVersion = new ProjectVersionRest(projectVersionLicenseCheck.getProjectVersion());
        this.licenseDeterminationType = new LicenseDeterminationTypeRest(
                projectVersionLicenseCheck.getLicenseDeterminationType());
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

}
