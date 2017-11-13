package org.jboss.license.dictionary.utils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/3/17
 */
public class ResponseUtils {

    public static <T> List<T> listOrNotFound(List<T> list, String message, Object... parameters) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            throw new ClientErrorException(Response.status(SC_NOT_FOUND)
                    .entity(String.format(message, parameters))
                    .build());
        }
        return list;
    }

    public static <T> T valueOrNotFound(Optional<T> maybeElement, String message, Object... parameters) {
        T element = maybeElement.orElseThrow(
                () -> new ClientErrorException(Response.status(SC_NOT_FOUND)
                        .entity(String.format(message, parameters))
                        .build())
        );
        return element;
    }

    public static void conflict(String message, Object... parameters) {
        throw new ClientErrorException(Response.status(SC_CONFLICT)
                .entity(String.format(message, parameters))
                .build());
    }
}
