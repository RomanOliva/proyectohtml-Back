package com.example.ms_warframe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ms_warframe.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Integer> {

    @EntityGraph(attributePaths = { "usuario", "detalles", "detalles.item" })
    List<Boleta> findByUsuarioCorreoOrderByFechaDesc(String correo);

    @EntityGraph(attributePaths = { "usuario", "detalles", "detalles.item" })
    List<Boleta> findAllByOrderByFechaDesc();
}
