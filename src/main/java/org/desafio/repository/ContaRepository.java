package org.desafio.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.desafio.entity.ContaEntity;

import java.util.List;

@ApplicationScoped
public class ContaRepository implements PanacheRepository<ContaEntity> {


    public List<ContaEntity> findByClienteCpfCnpj(String cpfCnpj) {
        return find("SELECT c FROM ContaEntity c JOIN c.clientes cl WHERE cl.cpfCnpj = ?1 and ativo = true", cpfCnpj).list();
    }

    public ContaEntity findByNumero(String numeroConta) {
        return find("SELECT c FROM ContaEntity c WHERE c.numero = ?1 and ativo = true", numeroConta).firstResult();
    }
}