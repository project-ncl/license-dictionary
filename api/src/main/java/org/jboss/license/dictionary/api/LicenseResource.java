package org.jboss.license.dictionary.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
@Consumes("application/json")
@Produces("application/json")
@Path(LicenseResource.LICENSES)
public interface LicenseResource {

    String LICENSES = "/licenses";

    @GET
    List<License> getLicenses(
            @QueryParam("name") String name,
            @QueryParam("url") String url,
            @QueryParam("nameAlias") String nameAlias,
            @QueryParam("urlAlias") String urlAlias,
            @QueryParam("searchTerm") String searchTerm);

    @POST
    License addLicense(License license);
}
