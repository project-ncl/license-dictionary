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
