package org.desafio.util;

import jakarta.ws.rs.core.Response;

public class AuthUtil {

    public static Response validateToken(String authHeader) {
        if (!JWTUtil.isTokenValid(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return null;
    }
}
