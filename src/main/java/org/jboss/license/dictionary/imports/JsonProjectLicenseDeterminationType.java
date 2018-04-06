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
package org.jboss.license.dictionary.imports;

import api.LicenseDeterminationTypeRest;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
public class JsonProjectLicenseDeterminationType {

    private String id;
    private String name;

    public JsonProjectLicenseDeterminationType() {

    }

    public LicenseDeterminationTypeRest toLicenseDeterminationTypeRest() {
        return LicenseDeterminationTypeRest.Builder.newBuilder().id(Integer.parseInt(id)).name(name).build();
    }

}

// "license determination type": {
// "id": "14",
// "name": "License file, readme file, missing in pom file"
// },
