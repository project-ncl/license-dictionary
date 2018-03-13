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

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/13/17
 */
public class BadRequestException extends ClientErrorException {
    public BadRequestException(String message) {
        super(createResponse(message));
    }

    private static Response createResponse(String message) {
        return Response.status(SC_BAD_REQUEST)
                .entity(message)
                .build();
    }
}
