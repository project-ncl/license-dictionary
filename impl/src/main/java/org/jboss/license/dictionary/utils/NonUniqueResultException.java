package org.jboss.license.dictionary.utils;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 9/11/17
 */
public class NonUniqueResultException extends RuntimeException {

    public NonUniqueResultException(String message) {
        super(message);    
    }

    public NonUniqueResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
