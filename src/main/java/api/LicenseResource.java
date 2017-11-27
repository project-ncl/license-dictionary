package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
@Consumes("application/json")
@Produces("application/json")
@Path(LicenseResource.LICENSES)
// todo merge with implementation
public interface LicenseResource {

    String LICENSES = "/licenses";

    @GET
    Response getLicenses(
            @QueryParam("name") String name,
            @QueryParam("url") String url,
            @QueryParam("nameAlias") String nameAlias,
            @QueryParam("urlAlias") String urlAlias,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("count") Integer resultCount,
            @QueryParam("offset") Integer offset);

    @PUT
    @Path("/{id}")
    License updateLicense(@PathParam("id") Integer licenseId,
                          FullLicenseData license);

    @DELETE
    @Path("/{id}")
    void deleteLicense(@PathParam("id") Integer licenseId);

    @POST
    License addLicense(FullLicenseData license);

    @GET
    @Path("/{id}")
    License getLicense(@PathParam("id") Integer licenseId);
}
