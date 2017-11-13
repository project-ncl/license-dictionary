package org.jboss.license.dictionary.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/3/17
 */
@Data
public class ErrorDto {
    private List<String> errorMessages = new ArrayList<>();

    public void addError(String message, Object... parameters) {
        errorMessages.add(String.format(message, parameters));
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

}
