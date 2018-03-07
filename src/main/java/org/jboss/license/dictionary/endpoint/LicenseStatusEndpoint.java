package org.jboss.license.dictionary.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.license.dictionary.LicenseStatusStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.utils.NotFoundException;
import org.jboss.logging.Logger;

import api.LicenseApprovalStatusRest;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_STATUS_ENDPOINT)
public class LicenseStatusEndpoint extends AbstractEndpoint {

    private static final Logger log = Logger.getLogger(LicenseStatusEndpoint.class);

    @Inject
    private LicenseStatusStore licenseStatusStore;

    @GET
    public Response getAll() {
        List<LicenseApprovalStatusRest> results = licenseStatusStore.getAll();

        return paginated(results, results.size(), 0);
    }

    @GET
    @Path("/{id}")
    public Response getSpecific(@PathParam("id") Integer id) {

        LicenseApprovalStatusRest entity = licenseStatusStore.getById(id)
                .orElseThrow(() -> new NotFoundException("No license status found for id " + id));

        return Response.ok().entity(entity).build();
    }

    @POST
    @Transactional
    public Response createNew(LicenseApprovalStatusRest licenseApprovalStatusRest, @Context UriInfo uriInfo) {

        log.info("creating license approval status: " + licenseApprovalStatusRest);
        licenseApprovalStatusRest = licenseStatusStore.save(licenseApprovalStatusRest);

        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri()).path("{id}");
        return Response.created(uriBuilder.build(licenseApprovalStatusRest.getId())).entity(licenseApprovalStatusRest).build();
    }

}
