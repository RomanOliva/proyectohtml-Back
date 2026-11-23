package com.example.ms_warframe.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_warframe.DTO.AjusteMasivoDTO;
import com.example.ms_warframe.DTO.AjusteMasivoRequestDTO;
import com.example.ms_warframe.DTO.ItemRequestDTO;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.exception.ReglaNegocioException;
import com.example.ms_warframe.model.Categoria;
import com.example.ms_warframe.model.Item;
import com.example.ms_warframe.repository.CategoriaRepository;
import com.example.ms_warframe.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    public static final int PRECIO_MINIMO = 1;
    public static final int PRECIO_MAXIMO = 3000;

    private final ItemRepository itemRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Item> listar(Integer idCategoria, Integer min, Integer max, String texto) {
        if (min != null && max != null && min > max) {
            throw new ReglaNegocioException("El precio mínimo no puede ser mayor que el máximo");
        }
        return itemRepository.filtrar(idCategoria, min, max, texto);
    }

    @Transactional(readOnly = true)
    public Item obtener(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el ítem con id " + id));
    }

    @Transactional
    public Item crear(ItemRequestDTO request) {
        String nombre = request.getNombre().trim();
        if (itemRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un ítem llamado '" + nombre + "'");
        }
        Item item = new Item();
        aplicar(item, request, nombre);
        log.info("Creando ítem '{}'", nombre);
        return itemRepository.save(item);
    }

    @Transactional
    public Item actualizar(Integer id, ItemRequestDTO request) {
        Item item = obtener(id);
        String nombre = request.getNombre().trim();
        if (itemRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otro ítem llamado '" + nombre + "'");
        }
        aplicar(item, request, nombre);
        return itemRepository.save(item);
    }

    @Transactional
    public Item actualizarPrecio(Integer id, Integer nuevoPrecio) {
        validarPrecio(nuevoPrecio);
        Item item = obtener(id);
        item.setPrecioCreditos(nuevoPrecio);
        return itemRepository.save(item);
    }

    @Transactional
    public AjusteMasivoDTO ajustarPorUmbral(AjusteMasivoRequestDTO request) {
        Integer min = request.getPrecioMin();
        Integer max = request.getPrecioMax();
        if (min > max) {
            throw new ReglaNegocioException("El precio mínimo no puede ser mayor que el máximo");
        }

        BigDecimal factor = BigDecimal.ONE.add(request.getPorcentaje().movePointLeft(2));
        List<Item> afectados = itemRepository.filtrar(null, min, max, null);

        int[] nuevosPrecios = new int[afectados.size()];
        for (int i = 0; i < afectados.size(); i++) {
            BigDecimal nuevo = BigDecimal.valueOf(afectados.get(i).getPrecioCreditos())
                    .multiply(factor)
                    .setScale(0, RoundingMode.HALF_UP);
            if (nuevo.compareTo(BigDecimal.valueOf(PRECIO_MINIMO)) < 0
                    || nuevo.compareTo(BigDecimal.valueOf(PRECIO_MAXIMO)) > 0) {
                throw new ReglaNegocioException("El precio debe estar entre " + PRECIO_MINIMO
                        + " y " + PRECIO_MAXIMO + " créditos (resultado: " + nuevo.toPlainString() + ")");
            }
            nuevosPrecios[i] = nuevo.intValue();
        }

        for (int i = 0; i < afectados.size(); i++) {
            afectados.get(i).setPrecioCreditos(nuevosPrecios[i]);
        }

        log.info("Ajuste de {}% aplicado a {} ítems entre {} y {} créditos",
                request.getPorcentaje(), afectados.size(), min, max);
        return new AjusteMasivoDTO(min, max, request.getPorcentaje(), afectados.size());
    }

    @Transactional
    public void eliminar(Integer id) {
        Item item = obtener(id);
        itemRepository.delete(item);

        itemRepository.flush();
        log.info("Ítem {} eliminado", id);
    }

    private void aplicar(Item item, ItemRequestDTO request, String nombre) {
        validarPrecio(request.getPrecioCreditos());
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la categoría con id " + request.getIdCategoria()));
        item.setNombre(nombre);
        item.setCategoria(categoria);
        item.setMotivoPocoUso(request.getMotivoPocoUso().trim());
        item.setPrecioCreditos(request.getPrecioCreditos());
        item.setImagenUrl(request.getImagenUrl());

        if (request.getStock() != null) {
            item.setStock(request.getStock());
        }
    }

    private void validarPrecio(Integer precio) {
        if (precio == null || precio < PRECIO_MINIMO || precio > PRECIO_MAXIMO) {
            throw new ReglaNegocioException("El precio debe estar entre " + PRECIO_MINIMO
                    + " y " + PRECIO_MAXIMO + " créditos (resultado: " + precio + ")");
        }
    }
}
