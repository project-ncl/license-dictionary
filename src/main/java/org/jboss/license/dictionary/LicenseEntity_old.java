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
package org.jboss.license.dictionary;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import api.LicenseStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 8/31/17
 */
@Entity
@Getter
@ToString(exclude = { "content", "nameAliases", "urlAliases" })
@EqualsAndHashCode
public class LicenseEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Setter
    private String name;

    @Setter
    private String abbreviation;

    @Setter
    private String url;

    @Setter
    @Enumerated(EnumType.STRING)
    private LicenseStatus status;

    @ElementCollection
    @Setter
    private Set<String> nameAliases;

    @ElementCollection
    @Setter
    private Set<String> urlAliases;

    @Setter
    private String textUrl;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Setter
    private String content;

    @Setter
    private String fedoraAbbrevation;

    @Setter
    private String fedoraName;

    @Setter
    private String spdxName;

    @Setter
    private String spdxAbbreviation;

    @Setter
    private String spdxUrl;
}
