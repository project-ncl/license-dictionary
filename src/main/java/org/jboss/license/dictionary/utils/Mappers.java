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
package org.jboss.license.dictionary.utils;

import java.lang.reflect.Type;
import java.util.List;

import org.jboss.license.dictionary.model.License;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import api.LicenseRest;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/17/17
 */
public class Mappers {

    public static final Type licenseRestListType = new TypeToken<List<LicenseRest>>() {
    }.getType();

    public static final Type licenseListType = new TypeToken<List<License>>() {
    }.getType();

    public static final ModelMapper limitedMapper;
    public static final ModelMapper fullMapper;

    static {
        limitedMapper = new ModelMapper();
        fullMapper = new ModelMapper();
    }
}
