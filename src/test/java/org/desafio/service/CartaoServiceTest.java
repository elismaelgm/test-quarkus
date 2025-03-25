package org.desafio.service;

import org.desafio.dto.AlterarCvvRequestDTO;
import org.desafio.entity.CartaoEntity;
import org.desafio.enums.TipoCartao;
import org.desafio.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private CartaoService cartaoService;
    private CartaoEntity cartaoFisico;
    private CartaoEntity cartaoVirtual;
    private AlterarCvvRequestDTO request;
    private CartaoEntity cartao;

    @BeforeEach
    public void setUp() {
        request = new AlterarCvvRequestDTO();
        request.setCardId("1234567890123456");
        request.setNextCvv("123");

        cartao = new CartaoEntity();
        cartao.setNumero("1234567890123456");
        cartao.setCvv("000");

        cartaoFisico = new CartaoEntity();
        cartaoFisico.setNumero("1234567890123456");
        cartaoFisico.setTipo(TipoCartao.FISICO);
        cartaoFisico.setAtivo(true);

        cartaoVirtual = new CartaoEntity();
        cartaoVirtual.setNumero("6543210987654321");
        cartaoVirtual.setTipo(TipoCartao.VIRTUAL);
        cartaoVirtual.setAtivo(true);
        cartaoVirtual.setCartaoFisico(cartaoFisico);

    }

    @Test
    public void testAlterarCvv_CartaoNotFound() {
        when(cartaoRepository.findByNumeroAndAtivo(request.getCardId())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            cartaoService.alterarCvv(request);
        });

        verify(cartaoRepository, times(1)).findByNumeroAndAtivo(request.getCardId());
        verify(cartaoRepository, never()).persist(any(CartaoEntity.class));
    }

    @Test
    public void testAlterarCvv_Success() {
        when(cartaoRepository.findByNumeroAndAtivo(request.getCardId())).thenReturn(cartao);

        cartaoService.alterarCvv(request);

        verify(cartaoRepository, times(1)).findByNumeroAndAtivo(request.getCardId());
        verify(cartaoRepository, times(1)).persist(cartao);
        assert(cartao.getCvv().equals(request.getNextCvv()));
    }

    @Test
    public void testCancelarCartaoFisico_CartaoNotFound() {
        when(cartaoRepository.findByNumeroAndAtivo("1234567890123456")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            cartaoService.cancelarCartaoFisico("1234567890123456");
        });

        verify(cartaoRepository, times(1)).findByNumeroAndAtivo("1234567890123456");
        verify(cartaoRepository, never()).persist(any(CartaoEntity.class));
    }

    @Test
    public void testCancelarCartaoFisico_Success() {
        when(cartaoRepository.findByNumeroAndAtivo("1234567890123456")).thenReturn(cartaoFisico);
        when(cartaoRepository.findByCartaoFisico(cartaoFisico)).thenReturn(Collections.singletonList(cartaoVirtual));

        cartaoService.cancelarCartaoFisico("1234567890123456");

        verify(cartaoRepository, times(1)).findByNumeroAndAtivo("1234567890123456");
        verify(cartaoRepository, times(1)).findByCartaoFisico(cartaoFisico);
        verify(cartaoRepository, times(2)).persist(any(CartaoEntity.class));
        assert(!cartaoFisico.getAtivo());
        assert(!cartaoVirtual.getAtivo());
    }
}