package org.jboss.license.dictionary.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/21/17
 */
@Path("/config")
@Consumes("application/json")
@Produces("application/json")
public class ConfigEndpoint {
    @Inject
    @ConfigurationValue("keycloak.url")
    private String keycloakUrl;
    @Inject
    @ConfigurationValue("keycloak.realm")
    private String keycloakRealm;
    @Inject
    @ConfigurationValue("keycloak.uiClientId")
    private String keycloakClientId;

    @GET
    @Path("keycloak-config")
    public KeycloakConfig getKeycloakConfig() {
        KeycloakConfig config = new KeycloakConfig();
        config.setClientId(keycloakClientId);
        config.setRealm(keycloakRealm);
        config.setUrl(keycloakUrl);
        return config;
    }
}
