package org.jboss.license.dictionary.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/25/17
 */
@Data
public class KeycloakConfig {
    private String realm;
    @JsonProperty("auth-server-url")
    private String url;
    @JsonProperty("resource")
    private String clientId;
}
