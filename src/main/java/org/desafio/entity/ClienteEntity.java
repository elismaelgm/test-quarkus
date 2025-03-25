package org.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "cliente")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(length = 255)
    public String nome;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(min = 11, max = 14, message = "CPF/CNPJ deve ter entre 11 e 14 caracteres")
    @Column(length = 14, unique = true, nullable = false)
    public String cpfCnpj;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    @Column(length = 255)
    public String endereco;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cliente_conta",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "conta_id")
    )
    public Set<ContaEntity> contas = new HashSet<>();
}