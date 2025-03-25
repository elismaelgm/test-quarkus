package org.desafio.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.desafio.dto.ClienteDTO;
import org.desafio.dto.ContaDTO;
import org.desafio.service.ContaService;
import org.desafio.util.TokenRequired;

import java.util.List;

@Path("/conta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContaResource {

    @Inject
    ContaService contaService;

    @POST
    @Path("gerarConta/{cpfCnpj}")
    @TokenRequired
    public Response gerarConta(@HeaderParam("Authorization") String authHeader, @PathParam("cpfCnpj") String cpfCnpj) {
        ContaDTO novaConta = contaService.gerarConta(cpfCnpj);
        return Response.status(Response.Status.CREATED).entity(novaConta).build();
    }

    @POST
    @Path("associarConta/{cpfCnpj}/{numeroConta}")
    @TokenRequired
    public Response associarConta(@HeaderParam("Authorization") String authHeader, @PathParam("cpfCnpj") String cpfCnpj, @PathParam("numeroConta") String numeroConta) {
        contaService.associarConta(cpfCnpj, numeroConta);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/{cpfCnpj}")
    @TokenRequired
    public List<ContaDTO> listarContasPorCpfCnpj(@HeaderParam("Authorization") String authHeader, @PathParam("cpfCnpj") String cpfCnpj) {
        return contaService.listarContasPorCpfCnpj(cpfCnpj);
    }

    @GET
    @Path("clientes/{numeroConta}")
    @TokenRequired
    public Response listarClientesPorNumeroConta(@HeaderParam("Authorization") String authHeader, @PathParam("numeroConta") String numeroConta) {
        List<ClienteDTO> clientes = contaService.listarClientesPorNumeroConta(numeroConta);
        return Response.status(Response.Status.OK).entity(clientes).build();
    }

    @POST
    @Path("desabilitarConta/{numeroConta}")
    @TokenRequired
    public Response desabilitarConta(@HeaderParam("Authorization") String authHeader, @PathParam("numeroConta") String numeroConta) {
        contaService.desabilitarConta(numeroConta);
        return Response.status(Response.Status.OK).build();
    }
}