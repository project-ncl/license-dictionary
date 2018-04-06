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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.license.dictionary.model.LicenseAlias;

import lombok.Getter;
import lombok.Setter;

public class LicenseRest {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String fedoraAbbreviation;

    @Getter
    @Setter
    private String fedoraName;

    @Getter
    @Setter
    private String spdxAbbreviation;

    @Getter
    @Setter
    private String spdxName;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String textUrl;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private LicenseApprovalStatusRest licenseApprovalStatus;

    @Getter
    @Setter
    private Set<LicenseAliasRest> aliases;

    public LicenseRest() {
        this.aliases = new HashSet<LicenseAliasRest>();
    }

    public static void checkIntegrity(LicenseRest licenseRest) {
        if (licenseRest.getFedoraName() == null && licenseRest.getSpdxName() == null) {
            throw new IllegalStateException("License data is incomplete " + licenseRest.toString());
        }
    }

    public List<String> getAliasNames() {
        return this.aliases.stream().sorted(Comparator.comparing(LicenseAliasRest::getAliasName))
                .map(alias -> alias.getAliasName()).collect(Collectors.toList());
    }

    public void addAliases(Set<LicenseAlias> licenseAliases) {
        licenseAliases.stream().forEach(licenseAlias -> {
            aliases.add(LicenseAliasRest.Builder.newBuilder().id(licenseAlias.getId()).aliasName(licenseAlias.getAliasName())
                    .licenseId(licenseAlias.getLicense().getId()).build());
        });
    }

    public void addAlias(Integer id, String licenseAlias, Integer licenseId) {
        aliases.add(LicenseAliasRest.Builder.newBuilder().id(id).aliasName(licenseAlias).licenseId(licenseId).build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LicenseRest licenseRest = (LicenseRest) o;

        if (id != null ? !id.equals(licenseRest.id) : licenseRest.id != null)
            return false;
        if (code != null ? !code.equals(licenseRest.code) : licenseRest.code != null)
            return false;
        if (fedoraAbbreviation != null ? !fedoraAbbreviation.equals(licenseRest.fedoraAbbreviation)
                : licenseRest.fedoraAbbreviation != null)
            return false;
        if (fedoraName != null ? !fedoraName.equals(licenseRest.fedoraName) : licenseRest.fedoraName != null)
            return false;
        if (spdxAbbreviation != null ? !spdxAbbreviation.equals(licenseRest.spdxAbbreviation)
                : licenseRest.spdxAbbreviation != null)
            return false;
        if (spdxName != null ? !spdxName.equals(licenseRest.spdxName) : licenseRest.spdxName != null)
            return false;
        if (url != null ? !url.equals(licenseRest.url) : licenseRest.url != null)
            return false;
        if (textUrl != null ? !textUrl.equals(licenseRest.textUrl) : licenseRest.textUrl != null)
            return false;
        if (licenseApprovalStatus != null ? !licenseApprovalStatus.equals(licenseRest.licenseApprovalStatus)
                : licenseRest.licenseApprovalStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (fedoraAbbreviation != null ? fedoraAbbreviation.hashCode() : 0);
        result = 31 * result + (fedoraName != null ? fedoraName.hashCode() : 0);
        result = 31 * result + (spdxAbbreviation != null ? spdxAbbreviation.hashCode() : 0);
        result = 31 * result + (spdxName != null ? spdxName.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (textUrl != null ? textUrl.hashCode() : 0);
        result = 31 * result + (licenseApprovalStatus != null ? licenseApprovalStatus.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "LicenseRest{" + "id=" + id + ", code='" + code + '\'' + ", fedoraAbbreviation='" + fedoraAbbreviation + '\''
                + ", fedoraName='" + fedoraName + '\'' + ", spdxAbbreviation='" + spdxAbbreviation + '\'' + ", spdxName='"
                + spdxName + '\'' + ", url='" + url + '\'' + ", textUrl='" + textUrl + '\'' + ", licenseApprovalStatus='"
                + licenseApprovalStatus + '\'' + '}';
    }

    public static class Builder {

        private Integer id;
        private String fedoraAbbreviation;
        private String fedoraName;
        private String spdxAbbreviation;
        private String spdxName;
        private String url;
        private String textUrl;
        private String code;
        private LicenseApprovalStatusRest licenseApprovalStatus;
        private Set<LicenseAliasRest> aliases;

        private Builder() {
            this.aliases = new HashSet<LicenseAliasRest>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder fedoraAbbreviation(String fedoraAbbreviation) {
            this.fedoraAbbreviation = fedoraAbbreviation;
            return this;
        }

        public Builder fedoraName(String fedoraName) {
            this.fedoraName = fedoraName;
            return this;
        }

        public Builder spdxAbbreviation(String spdxAbbreviation) {
            this.spdxAbbreviation = spdxAbbreviation;
            return this;
        }

        public Builder spdxName(String spdxName) {
            this.spdxName = spdxName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder textUrl(String textUrl) {
            this.textUrl = textUrl;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder licenseApprovalStatus(LicenseApprovalStatusRest licenseApprovalStatus) {
            this.licenseApprovalStatus = licenseApprovalStatus;
            return this;
        }

        public Builder aliases(Set<LicenseAliasRest> aliases) {
            this.aliases = aliases;
            return this;
        }

        public LicenseRest build() {
            LicenseRest licenseRest = new LicenseRest();
            licenseRest.setId(id);
            licenseRest.setFedoraName(fedoraName);
            licenseRest.setFedoraAbbreviation(fedoraAbbreviation);
            licenseRest.setSpdxName(spdxName);
            licenseRest.setSpdxAbbreviation(spdxAbbreviation);
            licenseRest.setUrl(url);
            licenseRest.setTextUrl(textUrl);
            licenseRest.setCode(code);
            licenseRest.setLicenseApprovalStatus(licenseApprovalStatus);
            licenseRest.setAliases(aliases);

            aliases.stream().forEach(alias -> {
                alias.setLicenseId(id);
            });

            return licenseRest;
        }
    }

}
