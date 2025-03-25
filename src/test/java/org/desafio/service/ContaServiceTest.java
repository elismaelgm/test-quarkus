package org.desafio.service;

import org.desafio.dto.ContaDTO;
import org.desafio.entity.ClienteEntity;
import org.desafio.entity.ContaEntity;
import org.desafio.repository.ClienteRepository;
import org.desafio.repository.ContaRepository;
import org.desafio.util.CartaoGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CartaoGenerator cartaoGenerator;

    @InjectMocks
    private ContaService contaService;

    private ClienteEntity cliente;
    private ContaEntity conta;

    @BeforeEach
    public void setUp() {
        cliente = new ClienteEntity();
        cliente.setCpfCnpj("12345678901");
        cliente.setNome("Nome Teste");

        conta = new ContaEntity();
        conta.setDataAbertura(new Date());
        conta.setNumero("123456");
    }

    @Test
    public void testGerarConta_ClienteNaoEncontrado() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contaService.gerarConta("12345678901");
        });

        assertEquals("Cliente nao encontrado.", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(contaRepository, never()).persist(any(ContaEntity.class));
    }

    @Test
    public void testAssociarConta_ClienteNaoEncontrado() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contaService.associarConta("12345678901", "123456");
        });

        assertEquals("Cliente nao encontrado.", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(contaRepository, never()).findByNumero(anyString());
        verify(clienteRepository, never()).persist(any(ClienteEntity.class));
    }

    @Test
    public void testAssociarConta_ContaNaoEncontrada() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(cliente);
        when(contaRepository.findByNumero("123456")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contaService.associarConta("12345678901", "123456");
        });

        assertEquals("Conta nao encontrada.", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(contaRepository, times(1)).findByNumero("123456");
        verify(clienteRepository, never()).persist(any(ClienteEntity.class));
    }

    @Test
    public void testAssociarConta_Success() {
        when(clienteRepository.findByCpfCnpj("12345678901")).thenReturn(cliente);
        when(contaRepository.findByNumero("123456")).thenReturn(conta);

        contaService.associarConta("12345678901", "123456");

        verify(clienteRepository, times(1)).findByCpfCnpj("12345678901");
        verify(contaRepository, times(1)).findByNumero("123456");
        verify(clienteRepository, times(1)).persist(any(ClienteEntity.class));
    }
}
