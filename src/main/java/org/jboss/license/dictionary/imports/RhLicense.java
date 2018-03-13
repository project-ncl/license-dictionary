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

import api.FullLicenseData;
import api.LicenseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/13/17
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RhLicense {
    private String id;

    private String url;
    private String license_text_url;
    private String license_text;

    private String fedora_abbrev;
    private String fedora_name;

    private String spdx_name;
    private String spdx_abbrev;
    private String spdx_license_url;

    private String approved;

    public FullLicenseData toFullLicenseData(String alias) {
        FullLicenseData entity = new FullLicenseData();

        entity.setContent(license_text);
        entity.setTextUrl(license_text_url);

        entity.setFedoraName(fedora_name);
        entity.setFedoraAbbrevation(fedora_abbrev);

        entity.setSpdxName(spdx_name);
        entity.setSpdxAbbreviation(spdx_abbrev);
        entity.setSpdxUrl(spdx_license_url);

        entity.setStatus(LicenseStatus.fromJsonString(approved));

        entity.setName(getName(alias));
        entity.setUrl(firstNotBlank(url, spdx_license_url));
        entity.setAbbreviation(firstNotBlank(fedora_abbrev, spdx_abbrev));

        entity.getNameAliases().add(alias);

        return entity;
    }

    public String getName(String alias) {
        String name = firstNotBlank(fedora_name, spdx_name);
        return name != null && !name.trim().isEmpty() ?
                name
                : alias;
    }

    private static String firstNotBlank(String... values) {
        return Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
    }

    /*"3dfx Glide License": {
    "approved": "yes",
    "fedora_abbrev": "Glide",
    "fedora_name": "3dfx Glide License",
    "id": "1",
    "license_text_url": "http://rcm-guest.app.eng.bos.redhat.com/rcm-guest/staging/avibelli/licenses/Glide.txt",
    "spdx_abbrev": "Glide",
    "spdx_license_url": "https://spdx.org/licenses/Glide.html#licenseText",
    "spdx_name": "3dfx Glide License",
    "url": "http://www.users.on.net/~triforce/glidexp/COPYING.txt"
  }*/
}
