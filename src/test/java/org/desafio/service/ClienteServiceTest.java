package org.desafio.service;


import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.desafio.dto.ClienteInsertDTO;
import org.desafio.dto.ClienteUpdateDTO;
import org.desafio.entity.ClienteEntity;
import org.desafio.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteInsertDTO clienteInsertDTO;
    private ClienteUpdateDTO clienteUpdateDTO;
    private ClienteEntity clienteEntity;

    @BeforeEach
    public void setUp() {
        clienteInsertDTO = new ClienteInsertDTO();
        clienteInsertDTO.nome = "Nome Teste";
        clienteInsertDTO.cpfCnpj = "12345678901";
        clienteInsertDTO.endereco = "Endereço Teste";

        clienteUpdateDTO = new ClienteUpdateDTO();
        clienteUpdateDTO.nome = "Nome Atualizado";
        clienteUpdateDTO.endereco = "Endereço Atualizado";

        clienteEntity = new ClienteEntity();
        clienteEntity.setNome("Nome Teste");
        clienteEntity.setCpfCnpj("12345678901");
        clienteEntity.setEndereco("Endereço Teste");
    }

    @Test
    public void testCadastrarCliente_CpfCnpjJaCadastrado() {
        PanacheQuery<ClienteEntity> query = mock(PanacheQuery.class);
        when(clienteRepository.find("cpfCnpj", clienteInsertDTO.cpfCnpj)).thenReturn(query);
        when(query.firstResult()).thenReturn(clienteEntity);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrarCliente(clienteInsertDTO);
        });

        assertEquals("CPF/CNPJ ja cadastrado.", exception.getMessage());
        verify(clienteRepository, times(1)).find("cpfCnpj", clienteInsertDTO.cpfCnpj);
        verify(clienteRepository, never()).persist(any(ClienteEntity.class));
    }

    @Test
    public void testCadastrarCliente_Success() {
        PanacheQuery<ClienteEntity> query = mock(PanacheQuery.class);
        when(clienteRepository.find("cpfCnpj", clienteInsertDTO.cpfCnpj)).thenReturn(query);
        when(query.firstResult()).thenReturn(null);

        ClienteInsertDTO result = clienteService.cadastrarCliente(clienteInsertDTO);

        assertNotNull(result);
        assertEquals(clienteInsertDTO.nome, result.nome);
        assertEquals(clienteInsertDTO.cpfCnpj, result.cpfCnpj);
        assertEquals(clienteInsertDTO.endereco, result.endereco);
        verify(clienteRepository, times(1)).find("cpfCnpj", clienteInsertDTO.cpfCnpj);
        verify(clienteRepository, times(1)).persist(any(ClienteEntity.class));
    }

    @Test
    public void testAlterarCliente_ClienteNaoEncontrado() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(null);

        ClienteInsertDTO result = clienteService.alterarCliente("12345678901", clienteUpdateDTO);

        assertNull(result);
        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(clienteRepository, never()).persist(any(ClienteEntity.class));
    }

    @Test
    public void testAlterarCliente_Success() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(clienteEntity);

        ClienteInsertDTO result = clienteService.alterarCliente("12345678901", clienteUpdateDTO);

        assertNotNull(result);
        assertEquals(clienteUpdateDTO.nome, result.nome);
        assertEquals("12345678901", result.cpfCnpj);
        assertEquals(clienteUpdateDTO.endereco, result.endereco);
        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(clienteRepository, times(1)).persist(any(ClienteEntity.class));
    }
}