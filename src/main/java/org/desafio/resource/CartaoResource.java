package org.desafio.resource;

import jakarta.ws.rs.core.Response;
import org.desafio.dto.CartaoDTO;
import org.desafio.entity.MotivoReemissao;
import org.desafio.service.CartaoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.desafio.util.JWTUtil;
import org.desafio.util.TokenRequired;

import java.util.List;

@Path("/cartao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartaoResource {

    @Inject
    CartaoService cartaoService;

    @GET
    @Path("/cliente/{cpfCnpj}")
    @TokenRequired
    public List<CartaoDTO> listarCartoesPorCpfCnpj(@PathParam("cpfCnpj") String cpfCnpj) {
        return cartaoService.listarCartoesPorCpfCnpj(cpfCnpj);
    }

    @PUT
    @Path("/validar")
    @TokenRequired
    public Response validarCartao(
            @HeaderParam("Authorization") String authHeader,
            @QueryParam("numeroCartao") String numeroCartao,
            @QueryParam("cpfCnpj") String cpfCnpj,
            @QueryParam("dataValidade") String dataValidade) {
        boolean isValid = cartaoService.validarCartao(numeroCartao, cpfCnpj, dataValidade);
        if (isValid) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/solicitarVirtual")
    @TokenRequired
    public Response solicitarCartaoVirtual(
            @HeaderParam("Authorization") String authHeader,
            @QueryParam("numeroCartaoFisico") String numeroCartaoFisico,
            @QueryParam("cpfCnpj") String cpfCnpj) {
        CartaoDTO cartaoVirtual = cartaoService.solicitarCartaoVirtual(numeroCartaoFisico, cpfCnpj);
        return Response.status(Response.Status.CREATED).entity(cartaoVirtual).build();
    }

    @PUT
    @Path("/alterarCvvCartaoDigital")
    @TokenRequired
    public Response alterarCvvCartaoDigital(
            @HeaderParam("Authorization") String authHeader,
            @QueryParam("numeroCartao") String numeroCartao) {
        cartaoService.alterarCvvCartaoDigital(numeroCartao);
        return Response.ok().build();
    }

    @POST
    @Path("/reemitirCartaoFisico")
    @TokenRequired
    public Response reemitirCartaoFisico(
            @HeaderParam("Authorization") String authHeader,
            @QueryParam("numeroCartaoFisico") String numeroCartaoFisico,
            @QueryParam("motivo") MotivoReemissao motivo) {
        CartaoDTO novoCartaoFisico = cartaoService.reemitirCartaoFisico(numeroCartaoFisico, motivo);
        return Response.status(Response.Status.CREATED).entity(novoCartaoFisico).build();
    }
}