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
package org.jboss.license.dictionary.api;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class LicenseApprovalStatusRest {

    public static final LicenseApprovalStatusRest APPROVED = LicenseApprovalStatusRest.Builder.newBuilder().id(1)
            .name("APPROVED").build();
    public static final LicenseApprovalStatusRest NOT_APPROVED = LicenseApprovalStatusRest.Builder.newBuilder().id(2)
            .name("NOT_APPROVED").build();
    public static final LicenseApprovalStatusRest UNKNOWN = LicenseApprovalStatusRest.Builder.newBuilder().id(3).name("UNKNOWN")
            .build();

    public static final Map<String, LicenseApprovalStatusRest> approvalMapFromJson = Collections.unmodifiableMap(Stream
            .of(new AbstractMap.SimpleEntry<>("yes", APPROVED), new AbstractMap.SimpleEntry<>("no", NOT_APPROVED),
                    new AbstractMap.SimpleEntry<>("unknown", UNKNOWN))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

    public static final Map<LicenseApprovalStatusRest, String> approvalMapFromEntity = Collections.unmodifiableMap(Stream
            .of(new AbstractMap.SimpleEntry<>(APPROVED, "yes"), new AbstractMap.SimpleEntry<>(NOT_APPROVED, "no"),
                    new AbstractMap.SimpleEntry<>(UNKNOWN, "unknown"))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    public LicenseApprovalStatusRest() {
    }

    public static LicenseApprovalStatusRest restEntityFromJsonString(String approved) {
        if (approved == null) {
            return UNKNOWN;
        }

        LicenseApprovalStatusRest licenseApprovalStatusRest = approvalMapFromJson.get(approved);
        return (licenseApprovalStatusRest == null ? UNKNOWN : licenseApprovalStatusRest);
    }

    public static String jsonFromRestEntity(LicenseApprovalStatusRest licenseApprovalStatusRest) {
        if (licenseApprovalStatusRest == null) {
            return approvalMapFromEntity.get(UNKNOWN);
        }

        String approval = approvalMapFromEntity.get(licenseApprovalStatusRest);
        return (approval == null ? approvalMapFromEntity.get(UNKNOWN) : approval);
    }

    public static class Builder {

        private Integer id;
        private String name;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public LicenseApprovalStatusRest build() {
            LicenseApprovalStatusRest licenseApprovalStatusRest = new LicenseApprovalStatusRest();
            licenseApprovalStatusRest.setId(id);
            licenseApprovalStatusRest.setName(name);
            return licenseApprovalStatusRest;
        }
    }

}
