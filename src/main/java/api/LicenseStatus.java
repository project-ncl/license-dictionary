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

import java.util.Arrays;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/3/17
 */
public enum LicenseStatus {
    AWAITING_APPROVAL(null),
    NOT_APPROVED("no"),
    APPROVED("yes");

    private final String mapping;

    LicenseStatus(String mapping) {
        this.mapping = mapping;
    }

    public static LicenseStatus fromJsonString(String approved) {
        if (approved == null) {
            return AWAITING_APPROVAL;
        }
        return Arrays.stream(LicenseStatus.values())
                .filter(v -> approved.equals(v.mapping))
                .findAny().orElse(AWAITING_APPROVAL);
    }

    public String getJsonMapping() {
        return mapping;
    }
}
