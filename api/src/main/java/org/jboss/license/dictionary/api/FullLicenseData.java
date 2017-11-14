package org.jboss.license.dictionary.api;

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
    private String spdxFedoraUrl;

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

    public String getSpdxFedoraUrl() {
        return spdxFedoraUrl;
    }

    public void setSpdxFedoraUrl(String spdxFedoraUrl) {
        this.spdxFedoraUrl = spdxFedoraUrl;
    }
}
