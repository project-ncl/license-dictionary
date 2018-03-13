/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
