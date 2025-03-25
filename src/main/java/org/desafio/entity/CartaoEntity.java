package org.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.desafio.enums.TipoCartao;

import java.util.Objects;

@Entity
@Data
@Table(name = "cartao")
public class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Número do cartão é obrigatório")
    @Size(min = 16, max = 20, message = "Número do cartão deve entre 16 e 20 dígitos")
    @Column(length = 20, unique = true, nullable = false)
    public String numero;

    @NotNull(message = "Validade é obrigatória")
    public String validade;

    @NotNull(message = "Tipo de cartão é obrigatório")
    @Enumerated(EnumType.STRING)
    public TipoCartao tipo;

    @NotNull(message = "Validação é obrigatória")
    @Column(nullable = false)
    public Boolean validado = false;

    @NotNull(message = "Ativo é obrigatória")
    @Column(nullable = false)
    public Boolean ativo = true;

    @NotNull(message = "Conta é obrigatória")
    @ManyToOne
    @JoinColumn(name = "conta_id", nullable = false)
    public ContaEntity conta = new ContaEntity();

    @NotBlank(message = "CVV é obrigatório")
    @Size(min = 3, max = 3, message = "CVV deve ter 3 dígitos")
    @Column(length = 3, nullable = false)
    private String cvv;

    @ManyToOne
    @JoinColumn(name = "cartao_fisico_id")
    private CartaoEntity cartaoFisico;

    @NotNull(message = "Processadora é obrigatória")
    @Column(name = "id_processadora", nullable = false)
    private int processadora;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaoEntity that = (CartaoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}