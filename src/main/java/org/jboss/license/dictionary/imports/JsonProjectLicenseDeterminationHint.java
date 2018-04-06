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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonProjectLicenseDeterminationHint {

    private String name;
    private String[] values;
    private String value;

    public JsonProjectLicenseDeterminationHint() {

    }

}

// "license determination hints": [
// {
// "name": "license file",
// "values": [
// "LICENSE.txt"
// ]
// },
// {
// "name": "pom file",
// "value": "pom.xml"
// },
// {
// "name": "readme file",
// "values": [
// "README.txt:2:33"
// ]
// }
// ],