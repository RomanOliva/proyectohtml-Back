package com.example.ms_warframe.repository;

import java.util.List;

import com.example.ms_warframe.model.Item;

public interface ItemRepositoryCustom {

    List<Item> filtrar(Integer idCategoria, Integer min, Integer max, String texto);
}
