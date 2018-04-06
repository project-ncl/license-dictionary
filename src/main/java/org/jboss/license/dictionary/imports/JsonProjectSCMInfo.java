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

import api.ProjectVersionRest;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
public class JsonProjectSCMInfo {

    private String name;
    private String type;
    private String url;
    private String revision;

    public JsonProjectSCMInfo() {

    }

    public ProjectVersionRest toProjectVersionRest() {
        return ProjectVersionRest.Builder.newBuilder().scmRevision(revision).scmUrl(url).build();
    }

}
// "SCM info": [
// {
// "name": "internal",
// "type": "git",
// "url": "git://git.app.eng.bos.redhat.com/antlr2.git",
// "revision": "bd0ab97"
// }
// ],
