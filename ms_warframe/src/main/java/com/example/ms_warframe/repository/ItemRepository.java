package com.example.ms_warframe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ms_warframe.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, ItemRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = "categoria")
    Optional<Item> findById(Integer id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
