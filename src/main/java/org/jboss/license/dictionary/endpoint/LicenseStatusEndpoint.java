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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.license.dictionary.LicenseStatusStore;
import org.jboss.license.dictionary.RestApplication;
import org.jboss.license.dictionary.utils.NotFoundException;

import api.LicenseApprovalStatusRest;

/**
 * @author Andrea Vibelli, andrea.vibelli@gmail.com <br>
 *         Date: 16/02/18
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(RestApplication.REST_VERS_1 + RestApplication.LICENSE_STATUS_ENDPOINT)
public class LicenseStatusEndpoint {

    @Inject
    private LicenseStatusStore licenseStatusStore;

    @GET
    public Response getAllLicenseApprovalStatusRest() {
        List<LicenseApprovalStatusRest> results = licenseStatusStore.getAll();

        return paginated(results, results.size(), 0);
    }

    @GET
    @Path("/{id}")
    public LicenseApprovalStatusRest getLicenseApprovalStatusRest(@PathParam("id") Integer licenseStatusId) {
        return licenseStatusStore.getById(licenseStatusId)
                .orElseThrow(() -> new NotFoundException("No license status found for id " + licenseStatusId));
    }

    private static <T> Response paginated(T content, int totalCount, int offset) {
        return Response.ok().header("totalCount", totalCount).header("offset", offset).entity(content).build();
    }

    @POST
    @Transactional
    public LicenseApprovalStatusRest addLicenseApprovalStatusRest(LicenseApprovalStatusRest licenseApprovalStatusRest) {
        return licenseStatusStore.save(licenseApprovalStatusRest);
    }

}
