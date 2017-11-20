package org.jboss.license.dictionary.license.imports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jboss.license.dictionary.api.FullLicenseData;
import org.jboss.license.dictionary.api.LicenseStatus;

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

        entity.setStatus(getLicenseStatus());

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

    private LicenseStatus getLicenseStatus() {
        switch (approved) {
            case "yes":
                return LicenseStatus.APPROVED;
            case "no":
                return LicenseStatus.NOT_APPROVED;
            default:
                return LicenseStatus.AWAITING_APPROVAL;
        }
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
