package api;

import lombok.Getter;
import lombok.Setter;

public class ProjectRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String ecosystem;

    @Getter
    @Setter
    private String key;

    public ProjectRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectRest projectRest = (ProjectRest) o;

        if (id != null ? !id.equals(projectRest.id) : projectRest.id != null)
            return false;
        if (ecosystem != null ? !ecosystem.equals(projectRest.ecosystem) : projectRest.ecosystem != null)
            return false;
        if (key != null ? !key.equals(projectRest.key) : projectRest.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ecosystem != null ? ecosystem.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LicenseHintTypeRest{" + "id=" + id + ", ecosystem='" + ecosystem + '\'' + ", key='" + key + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String ecosystem;
        private String key;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder ecosystem(String ecosystem) {
            this.ecosystem = ecosystem;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public ProjectRest build() {
            ProjectRest projectRest = new ProjectRest();
            projectRest.setId(id);
            projectRest.setEcosystem(ecosystem);
            projectRest.setKey(key);
            return projectRest;
        }
    }

}
