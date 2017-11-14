package org.jboss.license.dictionary.license.imports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.jboss.license.dictionary.api.LicenseStatus;
import org.jboss.license.dictionary.license.LicenseEntity;

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

    public LicenseEntity toLicenseEntity(String name) {
        LicenseEntity entity = new LicenseEntity();
        entity.setName(name);

        entity.setUrl(url);
        entity.setContent(license_text);
        entity.setTextUrl(license_text_url);

        entity.setFedoraName(fedora_name);
        entity.setFedoraAbbrevation(fedora_abbrev);

        entity.setSpdxName(spdx_name);
        entity.setSpdxAbbreviation(spdx_abbrev);
        entity.setSpdxUrl(spdx_license_url);

        entity.setStatus(getLicenseStatus());
        return entity;
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
