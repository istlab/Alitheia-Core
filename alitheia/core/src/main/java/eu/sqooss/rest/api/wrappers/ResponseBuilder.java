package eu.sqooss.rest.api.wrappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResponseBuilder {
	
	protected static final int HTTP_NOT_AUTHORIZED = 401;
    protected static final int HTTP_NOT_FOUND = 404;
    protected static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    
    public static Response simpleResponse(String msg) {
        return Response.ok(msg, MediaType.TEXT_PLAIN).build(); 
    }
    
    public static Response simpleResponse(int status, String msg) {
        return Response.status(status).type(MediaType.TEXT_PLAIN).entity(msg).build();
    }
    
    public static Response internalServerErrorResponse(String msg) {
        return simpleResponse(HTTP_INTERNAL_SERVER_ERROR, msg);
    }

}
