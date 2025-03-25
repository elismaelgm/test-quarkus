package org.desafio.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.desafio.entity.ClienteEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<ClienteEntity> {

    public ClienteEntity findByCpfCnpj(String cpfCnpj) {
        return find("cpfCnpj", cpfCnpj).firstResult();
    }
}