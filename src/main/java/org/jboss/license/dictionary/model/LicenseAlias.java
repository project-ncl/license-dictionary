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
package org.jboss.license.dictionary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "LicenseAlias")
@Table(name = "license_alias", indexes = {
        @Index(name = LicenseAlias.IDX_NAME_LICENSE_ALIAS_LICENSE, columnList = "license_id") })

@ToString
@EqualsAndHashCode
public class LicenseAlias {

    public static final String SEQUENCE_NAME = "license_alias_id_seq";
    public static final String IDX_NAME_LICENSE_ALIAS_LICENSE = "idx_licensealias_license";
    public static final String FK_NAME_LICENSE_ALIAS_LICENSE = "fk_licensealias_license";
    public static final String UC_NAME_ALIAS_NAME = "uc_alias_aliasname";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1") })
    @Getter
    private Integer id;

    @NotNull
    @Column(name = "alias_name")
    @Getter
    @Setter
    private String aliasName;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = FK_NAME_LICENSE_ALIAS_LICENSE))
    @Getter
    @Setter
    private License license;

    public static class Builder {

        private Integer id;
        private String aliasName;
        private License license;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder aliasName(String aliasName) {
            this.aliasName = aliasName;
            return this;
        }

        public Builder license(License license) {
            this.license = license;
            return this;
        }

        public LicenseAlias build() {
            LicenseAlias licenseAlias = new LicenseAlias();
            licenseAlias.id = this.id;
            licenseAlias.setAliasName(aliasName);
            licenseAlias.setLicense(license);

            // Set bi-directional mappings
            license.addAlias(licenseAlias);

            return licenseAlias;
        }
    }

}
