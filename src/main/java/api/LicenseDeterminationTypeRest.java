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

public class LicenseDeterminationTypeRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    public LicenseDeterminationTypeRest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseDeterminationTypeRest licenseDeterminationTypeRest = (LicenseDeterminationTypeRest) o;

        if (id != null ? !id.equals(licenseDeterminationTypeRest.id) : licenseDeterminationTypeRest.id != null)
            return false;
        if (name != null ? !name.equals(licenseDeterminationTypeRest.name) : licenseDeterminationTypeRest.name != null)
            return false;
        if (description != null ? !description.equals(licenseDeterminationTypeRest.description)
                : licenseDeterminationTypeRest.description != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LicenseDeterminationTypeRest{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\''
                + '}';
    }

    public static class Builder {

        private Integer id;
        private String name;
        private String description;

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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public LicenseDeterminationTypeRest build() {
            LicenseDeterminationTypeRest licenseDeterminationTypeRest = new LicenseDeterminationTypeRest();
            licenseDeterminationTypeRest.setId(id);
            licenseDeterminationTypeRest.setName(name);
            licenseDeterminationTypeRest.setDescription(description);
            return licenseDeterminationTypeRest;
        }
    }

}
