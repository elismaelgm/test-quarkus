package org.desafio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterarCvvRequestDTO {

    @NotNull(message = "Account ID é obrigatório")
    @JsonProperty("account_id")
    private String accountId;

    @NotNull(message = "Card ID é obrigatório")
    @JsonProperty("card_id")
    private String cardId;

    @NotNull(message = "Next CVV é obrigatório")
    @Size(min = 3, max = 3, message = "CVV deve ter 3 dígitos")
    @JsonProperty("next_cvv")
    private String nextCvv;

    @NotNull(message = "Expiration date é obrigatória")
    @JsonProperty("expiration_date")
    private String expirationDate;
}