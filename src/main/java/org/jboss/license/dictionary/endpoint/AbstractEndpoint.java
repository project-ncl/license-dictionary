package org.jboss.license.dictionary.endpoint;

import javax.ws.rs.core.Response;

public class AbstractEndpoint {

    public AbstractEndpoint() {
    }

    protected <T> Response paginated(T content, int totalCount, int offset) {
        return Response.ok().header("totalCount", totalCount).header("offset", offset).entity(content).build();
    }
    
}
