package org.desafio.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.desafio.dto.ClienteDTO;
import org.desafio.dto.ContaDTO;
import org.desafio.entity.CartaoEntity;
import org.desafio.entity.ClienteEntity;
import org.desafio.entity.ContaEntity;
import org.desafio.repository.ClienteRepository;
import org.desafio.repository.ContaRepository;
import org.desafio.util.CartaoGenerator;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ContaService {

    @Inject
    ContaRepository contaRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    CartaoService cartaoService;

    @Transactional
    public ContaDTO gerarConta(String cpfCnpj) {
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }
        ContaEntity conta = new ContaEntity();
        conta.setDataAbertura(new Date());
        conta.setNumero(CartaoGenerator.gerarNumeroConta());
        contaRepository.persist(conta);

        cliente.getContas().add(conta);
        clienteRepository.persist(cliente);

        return toDTO(conta);
    }



    public List<ContaDTO> listarContasPorCpfCnpj(String cpfCnpj) {
        List<ContaEntity> contas = contaRepository.findByClienteCpfCnpj(cpfCnpj);
        if (contas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma conta encontrada para o cliente com CPF/CNPJ: " + cpfCnpj);
        }
        return contas.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void associarConta(String cpfCnpj, String numeroConta) {
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }

        ContaEntity conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta nao encontrada.");
        }

        cliente.getContas().add(conta);
        clienteRepository.persist(cliente);
    }

    @Transactional
    public List<ClienteDTO> listarClientesPorNumeroConta(String numeroConta) {
        ContaEntity conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta nao encontrada.");
        }
        return conta.getClientes().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void desabilitarConta(String numeroConta) {
        ContaEntity conta = contaRepository.findByNumero(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta nao encontrada.");
        }

        conta.setAtivo(false);
        conta.getCartoes().forEach(cartao -> cartao.setAtivo(false));
        contaRepository.persist(conta);
    }

    private ClienteDTO toDTO(ClienteEntity cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.nome = cliente.getNome();
        dto.cpfCnpj = cliente.getCpfCnpj();
        return dto;
    }

    private ContaDTO toDTO(ContaEntity conta) {
        ContaDTO dto = new ContaDTO();
        dto.dataAbertura = conta.getDataAbertura();
        dto.numeroConta = conta.getNumero();
        return dto;
    }

    private ContaEntity toEntity(ContaDTO dto) {
        ContaEntity conta = new ContaEntity();
        conta.setDataAbertura(dto.dataAbertura);
        conta.setNumero(dto.numeroConta);
        return conta;
    }
}