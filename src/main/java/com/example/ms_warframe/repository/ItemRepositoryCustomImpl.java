package com.example.ms_warframe.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.ms_warframe.model.Item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Item> filtrar(Integer idCategoria, Integer min, Integer max, String texto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> consulta = cb.createQuery(Item.class);
        Root<Item> item = consulta.from(Item.class);

        item.fetch("categoria", JoinType.LEFT);

        List<Predicate> condiciones = new ArrayList<>();
        if (idCategoria != null) {
            condiciones.add(cb.equal(item.get("categoria").get("id"), idCategoria));
        }
        if (min != null) {
            condiciones.add(cb.greaterThanOrEqualTo(item.<Integer>get("precioCreditos"), min));
        }
        if (max != null) {
            condiciones.add(cb.lessThanOrEqualTo(item.<Integer>get("precioCreditos"), max));
        }
        if (texto != null && !texto.isBlank()) {
            String patron = "%" + escaparComodines(texto.trim().toLowerCase()) + "%";
            condiciones.add(cb.like(cb.lower(item.<String>get("nombre")), patron, '\\'));
        }

        consulta.select(item)
                .where(condiciones.toArray(new Predicate[0]))
                .orderBy(cb.asc(item.get("categoria").get("id")), cb.asc(item.get("precioCreditos")));

        return em.createQuery(consulta).getResultList();
    }

    private String escaparComodines(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
