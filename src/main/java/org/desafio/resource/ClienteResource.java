package org.desafio.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.desafio.dto.ClienteInsertDTO;
import org.desafio.dto.ClienteUpdateDTO;
import org.desafio.service.ClienteService;
import org.desafio.util.TokenRequired;

import java.util.List;

@Path("/cliente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {


    @Inject
    ClienteService clienteService;

    @GET
    @TokenRequired
    public Response listarClientes(@HeaderParam("Authorization") String authHeader) {
        List<ClienteInsertDTO> clientes = clienteService.listarClientes();
        return Response.ok(clientes).build();
    }

    @POST
    @TokenRequired
    public Response cadastrarCliente(@HeaderParam("Authorization") String authHeader, @Valid ClienteInsertDTO cliente) {
        ClienteInsertDTO novoCliente = clienteService.cadastrarCliente(cliente);
        return Response.status(Response.Status.CREATED).entity(novoCliente).build();
    }

    @PUT
    @TokenRequired
    @Path("/{cpfCnpj}")
    public Response alterarCliente(@HeaderParam("Authorization") String authHeader, @PathParam("cpfCnpj") String cpfCnpj, @Valid ClienteUpdateDTO cliente) {
        ClienteUpdateDTO clienteAtualizado = clienteService.alterarCliente(cpfCnpj, cliente);
        if (clienteAtualizado != null) {
            return Response.ok(clienteAtualizado).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}