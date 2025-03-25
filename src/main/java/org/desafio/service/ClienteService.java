package org.desafio.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.desafio.dto.ClienteInsertDTO;
import org.desafio.dto.ClienteUpdateDTO;
import org.desafio.entity.ClienteEntity;
import org.desafio.repository.ClienteRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository clienteRepository;

    public List<ClienteInsertDTO> listarClientes() {
        return clienteRepository.listAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ClienteInsertDTO cadastrarCliente(ClienteInsertDTO clienteDTO) {
        if (clienteRepository.find("cpfCnpj", clienteDTO.cpfCnpj).firstResult() != null) {
            throw new IllegalArgumentException("CPF/CNPJ já cadastrado.");
        }
        ClienteEntity cliente = toEntity(clienteDTO);
        clienteRepository.persist(cliente);
        return toDTO(cliente);
    }

    @Transactional
    public ClienteInsertDTO alterarCliente(String cpfCnpj, ClienteUpdateDTO clienteDTO) {
        ClienteEntity clienteExistente = clienteRepository.findByCpfCnpj(cpfCnpj);
        if (clienteExistente != null) {
            clienteExistente.setNome(clienteDTO.nome);
            clienteExistente.setCpfCnpj(cpfCnpj);
            clienteExistente.setEndereco(clienteDTO.endereco);
            clienteRepository.persist(clienteExistente);
            return toDTO(clienteExistente);
        } else {
            return null;
        }
    }

    private ClienteInsertDTO toDTO(ClienteEntity cliente) {
        ClienteInsertDTO dto = new ClienteInsertDTO();
        dto.nome = cliente.getNome();
        dto.cpfCnpj = cliente.getCpfCnpj();
        dto.endereco = cliente.getEndereco();
        return dto;
    }

    private ClienteEntity toEntity(ClienteInsertDTO dto) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome(dto.nome);
        cliente.setCpfCnpj(dto.cpfCnpj);
        cliente.setEndereco(dto.endereco);
        return cliente;
    }
}