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

import org.jboss.license.dictionary.imports.RhLicense;

import java.util.stream.Collectors;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/14/17
 */
public class FullLicenseData extends License {
    private String fedoraAbbrevation;
    private String fedoraName;
    private String spdxName;
    private String spdxAbbreviation;
    private String spdxUrl;

    public String getFedoraAbbrevation() {
        return fedoraAbbrevation;
    }

    public void setFedoraAbbrevation(String fedoraAbbrevation) {
        this.fedoraAbbrevation = fedoraAbbrevation;
    }

    public String getFedoraName() {
        return fedoraName;
    }

    public void setFedoraName(String fedoraName) {
        this.fedoraName = fedoraName;
    }

    public String getSpdxName() {
        return spdxName;
    }

    public void setSpdxName(String spdxName) {
        this.spdxName = spdxName;
    }

    public String getSpdxAbbreviation() {
        return spdxAbbreviation;
    }

    public void setSpdxAbbreviation(String spdxAbbreviation) {
        this.spdxAbbreviation = spdxAbbreviation;
    }

    public String getSpdxUrl() {
        return spdxUrl;
    }

    public void setSpdxUrl(String spdxUrl) {
        this.spdxUrl = spdxUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FullLicenseData that = (FullLicenseData) o;

        if (fedoraAbbrevation != null ? !fedoraAbbrevation.equals(that.fedoraAbbrevation) : that.fedoraAbbrevation != null)
            return false;
        if (fedoraName != null ? !fedoraName.equals(that.fedoraName) : that.fedoraName != null) return false;
        if (spdxName != null ? !spdxName.equals(that.spdxName) : that.spdxName != null) return false;
        if (spdxAbbreviation != null ? !spdxAbbreviation.equals(that.spdxAbbreviation) : that.spdxAbbreviation != null)
            return false;
        if (spdxUrl != null ? !spdxUrl.equals(that.spdxUrl) : that.spdxUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fedoraAbbrevation != null ? fedoraAbbrevation.hashCode() : 0);
        result = 31 * result + (fedoraName != null ? fedoraName.hashCode() : 0);
        result = 31 * result + (spdxName != null ? spdxName.hashCode() : 0);
        result = 31 * result + (spdxAbbreviation != null ? spdxAbbreviation.hashCode() : 0);
        result = 31 * result + (spdxUrl != null ? spdxUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FullLicenseData{" +
                "super= " + super.toString() +
                ", fedoraAbbrevation='" + fedoraAbbrevation + '\'' +
                ", fedoraName='" + fedoraName + '\'' +
                ", spdxName='" + spdxName + '\'' +
                ", spdxAbbreviation='" + spdxAbbreviation + '\'' +
                ", spdxUrl='" + spdxUrl + '\'' +
                '}';
    }

    public String toFullString() {
        StringBuilder result = new StringBuilder(toString());
        result.append(", urlAliases: [")
                .append(getUrlAliases().stream().collect(Collectors.joining(",")))
                .append("], nameAliases: [")
                .append(getNameAliases().stream().collect(Collectors.joining(",")))
                .append("]");
        return result.toString();
    }

    public static void checkIntegrity(FullLicenseData fullLicenseData) {
        if (fullLicenseData.getName() == null
//                || fullLicenseData.getUrl() == null // mstodo uncomment
                ) {
            throw new IllegalStateException("License data is incomplete " + fullLicenseData.toFullString());
        }
    }

    public void mergeFrom(FullLicenseData other) {
        fedoraName = fedoraName == null ? other.fedoraName : fedoraName;
        fedoraAbbrevation = fedoraAbbrevation == null ? other.fedoraAbbrevation : fedoraAbbrevation;
        spdxName = spdxName == null ? other.spdxName : spdxName;
        spdxAbbreviation = spdxAbbreviation == null ? other.spdxAbbreviation : spdxAbbreviation;
        spdxUrl = spdxUrl == null ? other.spdxUrl : spdxUrl;

        setName(getName() == null ? other.getName() : getName());
        setAbbreviation(getAbbreviation() == null ? other.getAbbreviation() : getAbbreviation());
        setUrl(getUrl() == null ? other.getUrl() : getUrl());
        setTextUrl(getTextUrl() == null ? other.getTextUrl() : getTextUrl());
    }

    public RhLicense toRhLicense() {
        RhLicense result = new RhLicense();
        result.setApproved(getStatus().getJsonMapping());
        result.setFedora_abbrev(getFedoraAbbrevation());
        result.setFedora_name(getFedoraName());
        result.setLicense_text(getContent());
        result.setLicense_text_url(getTextUrl());
        result.setSpdx_abbrev(getSpdxAbbreviation());
        result.setSpdx_license_url(getSpdxUrl());
        result.setSpdx_name(getSpdxName());
        return result;
    }
}
