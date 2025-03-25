package org.desafio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteInsertDTO extends ClienteUpdateDTO {

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(min = 11, max = 14, message = "CPF/CNPJ deve ter entre 11 e 14 caracteres")
    public String cpfCnpj;

}