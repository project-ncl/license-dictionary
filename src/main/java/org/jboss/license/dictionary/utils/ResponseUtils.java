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

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 11/3/17
 */
public class ResponseUtils {

    public static <T> List<T> listOrNotFound(List<T> list, String message, Object... parameters) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            throw new ClientErrorException(Response.status(SC_NOT_FOUND).entity(String.format(message, parameters)).build());
        }
        return list;
    }

    public static <T> T valueOrNotFound(Optional<T> maybeElement, String message, Object... parameters) {
        T element = maybeElement.orElseThrow(() -> new ClientErrorException(
                Response.status(SC_NOT_FOUND).entity(String.format(message, parameters)).build()));
        return element;
    }

    public static void conflict(String message, Object... parameters) {
        throw new ClientErrorException(Response.status(SC_CONFLICT).entity(String.format(message, parameters)).build());
    }
}
