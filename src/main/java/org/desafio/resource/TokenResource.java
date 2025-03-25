package org.desafio.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.desafio.util.JWTUtil;

@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {

    @POST
    @Path("/gerarToken")
    public Response gerarToken(@QueryParam("username") String username, @QueryParam("password") String password) {
        if ("user".equals(username) && "password".equals(password)) {
            String token = JWTUtil.generateToken(username);
            return Response.ok(token).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}
