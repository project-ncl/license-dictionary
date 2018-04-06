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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
// @JsonIgnoreProperties(ignoreUnknown = true)
public class JsonProjectLicense {

    private static final String GAV_SEPARATOR = ":";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String project;

    @JsonProperty(value = "root pom GAV")
    private String rootPomGAV;

    @JsonProperty(value = "scope")
    private String scope; // PROJECT, PROJECT_WITH_EXCEPTION, GAV

    @JsonProperty(value = "GAV", defaultValue = "")
    private String GAV; // non-empty if scope == GAV

    @JsonProperty(value = "SCM info")
    private JsonProjectSCMInfo[] scmInfo;

    @JsonProperty(value = "license")
    private String[] licenseList;

    @JsonProperty(value = "license determination type")
    private JsonProjectLicenseDeterminationType licenseDeterminationType;

    @JsonProperty(value = "license determination hints")
    private JsonProjectLicenseDeterminationHint[] licenseDeterminationHints;

    @JsonProperty(value = "determined by")
    private String determinedBy;

    @JsonProperty(value = "determined date")
    private String determinationDate;

    @JsonProperty(value = "determined automatically based on", defaultValue = "")
    private String determinedBasedOn;

    @JsonProperty(value = "license determination comment", defaultValue = "")
    private String licenseDeterminationComment;

    public JsonProjectLicense() {

    }

    public String retrieveGAFromRootPom() {
        return calculateGAFromGAV(rootPomGAV);
    }

    public String retrieveVFromProject() {
        return calculateVFromProject(project);
    }

    public String retrieveGAFromScopeGAV() {
        return calculateGAFromGAV(GAV);
    }

    public boolean isScopeGAV() {
        return scope.equalsIgnoreCase("GAV");
    }

    private String calculateGAFromGAV(String gav) {

        if (gav == null || gav.isEmpty()) {
            System.out.println("calculateGAFromGAV: " + gav + " :gav == null || gav.isEmpty()");
            return "";
        }

        String[] gavTokens = gav.split(GAV_SEPARATOR);
        try {
            return gavTokens[0] + GAV_SEPARATOR + gavTokens[1];
        } catch (IndexOutOfBoundsException e) {
            System.out.println(gav + " :IndexOutOfBoundsException");
            return "";
        }
    }

    private String calculateVFromProject(String gav) {

        if (gav == null || gav.isEmpty()) {
            System.out.println("calculateVFromProject: " + gav + " :gav == null || gav.isEmpty()");
            return "";
        }

        String[] projectTokens = gav.split(GAV_SEPARATOR);
        try {
            return projectTokens[1];
        } catch (IndexOutOfBoundsException e) {
            System.out.println(gav + " :IndexOutOfBoundsException");
            return "";
        }
    }

    public LocalDateTime retrieveDeterminationDate() {
        try {
            LocalDate ld = LocalDate.parse(determinationDate, DATE_TIME_FORMAT);
            return ld.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

}
