package org.desafio.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.desafio.entity.CartaoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

@ApplicationScoped
public class CartaoRepository implements PanacheRepository<CartaoEntity> {

    public CartaoEntity findByNumeroAndAtivo(String numeroCartaoFisico) {
        return find("numero = ?1 and ativo = true", numeroCartaoFisico).firstResult();
    }

    public List<CartaoEntity> findByCartaoFisico(CartaoEntity cartaoFisico) {
        return list("cartaoFisico = ?1 and ativo = true", cartaoFisico);
    }

}