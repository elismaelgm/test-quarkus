package org.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "conta")
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "Data de abertura é obrigatória")
    @Temporal(TemporalType.DATE)
    @Column(name = "data_abertura", nullable = false)
    public Date dataAbertura;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(name = "numero", nullable = false, length = 20)
    public String numero;

    @ManyToMany(mappedBy = "contas")
    public Set<ClienteEntity> clientes = new HashSet<>();

    @NotNull(message = "Ativo é obrigatória")
    @Column(nullable = false)
    public Boolean ativo = true;

    @OneToMany(mappedBy = "conta")
    public Set<CartaoEntity> cartoes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaEntity that = (ContaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}