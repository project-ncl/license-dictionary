/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package api;

import lombok.Getter;
import lombok.Setter;

public class ProjectEcosystemRest {

    public static final ProjectEcosystemRest MAVEN = ProjectEcosystemRest.Builder.newBuilder().id(1).name("mvn").build();
    public static final ProjectEcosystemRest NPM = ProjectEcosystemRest.Builder.newBuilder().id(2).name("npm").build();
    public static final ProjectEcosystemRest NUGET = ProjectEcosystemRest.Builder.newBuilder().id(3).name("nuget").build();
    public static final ProjectEcosystemRest PYPI = ProjectEcosystemRest.Builder.newBuilder().id(4).name("pypi").build();
    public static final ProjectEcosystemRest GEM = ProjectEcosystemRest.Builder.newBuilder().id(5).name("gem").build();
    public static final ProjectEcosystemRest GITHUB = ProjectEcosystemRest.Builder.newBuilder().id(6).name("github").build();

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    public ProjectEcosystemRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectEcosystemRest projectEcosystemRest = (ProjectEcosystemRest) o;

        if (id != null ? !id.equals(projectEcosystemRest.id) : projectEcosystemRest.id != null)
            return false;
        if (name != null ? !name.equals(projectEcosystemRest.name) : projectEcosystemRest.name != null)
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
        return "ProjectEcosystemRest{" + "id=" + id + ", name='" + name + '\'' + '}';
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

        public ProjectEcosystemRest build() {
            ProjectEcosystemRest projectEcosystemRest = new ProjectEcosystemRest();
            projectEcosystemRest.setId(id);
            projectEcosystemRest.setName(name);
            return projectEcosystemRest;
        }
    }

}
