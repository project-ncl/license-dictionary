package org.jboss.license.dictionary.utils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/13/17
 */
public class NotFoundException extends ClientErrorException {
    public NotFoundException(String message) {
        super(createResponse(message));
    }

    private static Response createResponse(String message) {
        return Response
                .status(SC_NOT_FOUND)
                .entity(message)
                .build();
    }
}
