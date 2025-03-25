package org.desafio.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CartaoDTO {

    private Long id;
    private String numero;
    private String validade;
    private String tipo;
    private String cvv;
    private boolean validado;
}
