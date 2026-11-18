package com.example.ms_warframe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer id;

    @Column(name = "nombre_item", nullable = false, length = 60)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "motivo_poco_uso", nullable = false, length = 150)
    private String motivoPocoUso;

    @Column(name = "precio_creditos", nullable = false)
    private Integer precioCreditos;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;
}
