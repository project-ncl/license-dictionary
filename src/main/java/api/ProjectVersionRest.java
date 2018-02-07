package api;

import org.jboss.license.dictionary.model.ProjectVersion;

import lombok.Getter;
import lombok.Setter;

public class ProjectVersionRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String scmUrl;

    @Getter
    @Setter
    private String scmRevision;

    @Getter
    @Setter
    private ProjectRest project;

    public ProjectVersionRest() {
    }

    public ProjectVersionRest(ProjectVersion projectVersion) {
        this.id = projectVersion.getId();
        this.scmUrl = projectVersion.getScmUrl();
        this.scmRevision = projectVersion.getScmRevision();
        this.project = new ProjectRest(projectVersion.getProject());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectVersionRest projectVersionRest = (ProjectVersionRest) o;

        if (id != null ? !id.equals(projectVersionRest.id) : projectVersionRest.id != null)
            return false;
        if (scmUrl != null ? !scmUrl.equals(projectVersionRest.scmUrl) : projectVersionRest.scmUrl != null)
            return false;
        if (scmRevision != null ? !scmRevision.equals(projectVersionRest.scmRevision) : projectVersionRest.scmRevision != null)
            return false;
        if (project != null ? !project.equals(projectVersionRest.project) : projectVersionRest.project != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (scmUrl != null ? scmUrl.hashCode() : 0);
        result = 31 * result + (scmRevision != null ? scmRevision.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "ProjectVersionRest{" + "id=" + id + ", scmUrl='" + scmUrl + '\'' + ", scmRevision='" + scmRevision + '\''
                + ", project='" + project + '\'' + '}';
    }

}
