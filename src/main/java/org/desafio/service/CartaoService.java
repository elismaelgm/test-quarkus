package org.desafio.service;

import jakarta.transaction.Transactional;
import org.desafio.dto.AlterarCvvRequestDTO;
import org.desafio.dto.CartaoDTO;
import org.desafio.entity.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.desafio.enums.MotivoReemissao;
import org.desafio.enums.Processadora;
import org.desafio.enums.TipoCartao;
import org.desafio.repository.CartaoRepository;
import org.desafio.repository.ClienteRepository;
import org.desafio.repository.ContaRepository;
import org.desafio.util.CartaoGenerator;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartaoService {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ContaRepository contaRepository;

    @Inject
    CartaoRepository cartaoRepository;

    public List<CartaoDTO> listarCartoesPorCpfCnpj(String cpfCnpj) {
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        return cliente.getContas().stream()
                .flatMap(conta -> conta.getCartoes().stream())
                .filter(CartaoEntity::getAtivo)
                .map(this::toCartaoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean validarCartao(String numeroCartao, String cpfCnpj, String dataValidade) {
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente == null) {
            return false;
        }

        CartaoEntity cartao = cliente.getContas().stream()
                .flatMap(conta -> conta.getCartoes().stream())
                .filter(c -> c.getNumero().equals(numeroCartao) && c.getValidade().equals(dataValidade) && c.getAtivo())
                .findFirst()
                .orElse(null);

        if (cartao != null) {
            cartao.setValidado(true);
            cartaoRepository.persist(cartao);
            return true;
        }

        return false;
    }

    @Transactional
    public CartaoDTO solicitarCartaoVirtual(String numeroCartaoFisico, String cpfCnpj) {
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        CartaoEntity cartaoFisico = cartaoRepository.findByNumeroAndAtivo(numeroCartaoFisico);
        if (cartaoFisico == null || !cartaoFisico.getValidado()) {
            throw new IllegalArgumentException("Cartão físico não encontrado ou não validado.");
        }

        CartaoEntity cartaoVirtual = new CartaoEntity();
        cartaoVirtual.setNumero(CartaoGenerator.gerarNumeroCartao());
        cartaoVirtual.setValidade(CartaoGenerator.gerarDataValidade());
        cartaoVirtual.setCvv(CartaoGenerator.gerarCVV());
        cartaoVirtual.setValidado(true);
        cartaoVirtual.setTipo(TipoCartao.VIRTUAL);
        cartaoVirtual.setConta(cartaoFisico.getConta());
        cartaoVirtual.setCartaoFisico(cartaoFisico);

        cartaoRepository.persist(cartaoVirtual);
        return toCartaoDTO(cartaoVirtual);
    }

    @Transactional
    public CartaoDTO solicitarCartaoFisico(String numeroConta) {

        ContaEntity conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta nao encontrada.");
        }

        CartaoEntity cartaoFisico = new CartaoEntity();
        cartaoFisico.setNumero(CartaoGenerator.gerarNumeroCartao());
        cartaoFisico.setValidade(CartaoGenerator.gerarDataValidade());
        cartaoFisico.setCvv(CartaoGenerator.gerarCVV());
        cartaoFisico.setTipo(TipoCartao.FISICO);
        cartaoFisico.setConta(conta);
        cartaoFisico.setProcessadora(Processadora.VISA.getCodigo()); // Alterar para deixar dinamico
        cartaoRepository.persist(cartaoFisico);
        return toCartaoDTO(cartaoFisico);
    }

    @Transactional
    public void alterarCvvCartaoDigital(String numeroCartao) {
        CartaoEntity cartao = cartaoRepository.findByNumeroAndAtivo(numeroCartao);
        if (cartao == null) {
            throw new IllegalArgumentException("Cartão não encontrado.");
        }
        if (cartao.getTipo() == TipoCartao.FISICO) {
            throw new IllegalArgumentException("Não é permitido alterar o CVV de um cartão físico.");
        }
        cartao.setCvv(CartaoGenerator.gerarCVV());
        cartaoRepository.persist(cartao);
    }

    @Transactional
    public CartaoDTO reemitirCartaoFisico(String numeroCartaoFisico, MotivoReemissao motivo) {

        if (motivo == null) {
            throw new IllegalArgumentException("Motivo inválido para reemissão do cartão.");
        }

        CartaoEntity cartaoFisico = cartaoRepository.findByNumeroAndAtivo(numeroCartaoFisico);
        if (cartaoFisico == null || cartaoFisico.getTipo() != TipoCartao.FISICO) {
            throw new IllegalArgumentException("Cartão físico não encontrado.");
        }

        CartaoEntity novoCartaoFisico = new CartaoEntity();
        novoCartaoFisico.setNumero(CartaoGenerator.gerarNumeroCartao());
        novoCartaoFisico.setValidade(CartaoGenerator.gerarDataValidade());
        novoCartaoFisico.setCvv(CartaoGenerator.gerarCVV());
        novoCartaoFisico.setTipo(TipoCartao.FISICO);
        novoCartaoFisico.setConta(cartaoFisico.getConta());
        novoCartaoFisico.setProcessadora(cartaoFisico.getProcessadora());
        cartaoRepository.persist(novoCartaoFisico);

        cancelarCartaoFisico(numeroCartaoFisico);

        return toCartaoDTO(novoCartaoFisico);
    }

    @Transactional
    public void cancelarCartaoFisico(String numeroCartaoFisico) {
        CartaoEntity cartaoFisico = cartaoRepository.findByNumeroAndAtivo(numeroCartaoFisico);
        if (cartaoFisico == null || cartaoFisico.getTipo() != TipoCartao.FISICO) {
            throw new IllegalArgumentException("Cartão físico não encontrado.");
        }

        // Cancelar todos os cartões virtuais associados
        List<CartaoEntity> cartoesVirtuais = cartaoRepository.findByCartaoFisico(cartaoFisico);
        for (CartaoEntity cartaoVirtual : cartoesVirtuais) {
            cartaoVirtual.setAtivo(false);
            cartaoRepository.persist(cartaoVirtual);
        }

        // Cancelar o cartão físico
        cartaoFisico.setAtivo(false);
        cartaoRepository.persist(cartaoFisico);
    }

    @Transactional
    public void alterarCvv(AlterarCvvRequestDTO request) {
        CartaoEntity cartao = cartaoRepository.findByNumeroAndAtivo(request.getCardId());
        if (cartao == null) {
            throw new IllegalArgumentException("Cartão não encontrado.");
        }
        cartao.setCvv(request.getNextCvv());
        cartaoRepository.persist(cartao);
    }

    private CartaoDTO toCartaoDTO(CartaoEntity cartao) {
        CartaoDTO dto = new CartaoDTO();
        dto.setId(cartao.getId());
        dto.setNumero(cartao.getNumero());
        dto.setValidade(cartao.getValidade());
        dto.setValidado(cartao.getValidado());
        dto.setTipo(cartao.getTipo().name());
        dto.setCvv(cartao.getCvv());
        return dto;
    }
}