package com.example.ms_warframe.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_warframe.DTO.BoletaDTO;
import com.example.ms_warframe.DTO.BoletaRequestDTO;
import com.example.ms_warframe.DTO.DetalleRequestDTO;
import com.example.ms_warframe.assembler.BoletaAssembler;
import com.example.ms_warframe.exception.ConflictoException;
import com.example.ms_warframe.exception.CredencialesInvalidasException;
import com.example.ms_warframe.exception.RecursoNoEncontradoException;
import com.example.ms_warframe.exception.ReglaNegocioException;
import com.example.ms_warframe.model.Boleta;
import com.example.ms_warframe.model.BoletaDetalle;
import com.example.ms_warframe.model.Item;
import com.example.ms_warframe.model.Usuario;
import com.example.ms_warframe.repository.BoletaRepository;
import com.example.ms_warframe.repository.ItemRepository;
import com.example.ms_warframe.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoletaAssembler assembler;

    @Transactional
    public BoletaDTO crear(String correo, BoletaRequestDTO request) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("Tu sesión no es válida."));

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (DetalleRequestDTO d : request.getDetalles()) {
            cantidades.merge(d.getProductoId(), d.getCantidad(), Integer::sum);
        }

        List<Item> items = itemRepository.bloquearPorIds(cantidades.keySet());
        if (items.size() != cantidades.size()) {
            throw new RecursoNoEncontradoException("Un producto de tu carrito ya no existe.");
        }

        Boleta boleta = new Boleta();
        boleta.setUsuario(usuario);
        boleta.setFecha(Instant.now());

        long total = 0;
        for (Item item : items) {
            int cantidad = cantidades.get(item.getId());
            if (cantidad > item.getStock()) {
                String quedan = item.getStock() == 1 ? "queda 1 unidad" : "quedan " + item.getStock() + " unidades";
                throw new ConflictoException("Stock insuficiente de «" + item.getNombre() + "»: " + quedan + ".");
            }
            item.setStock(item.getStock() - cantidad);

            BoletaDetalle detalle = new BoletaDetalle();
            detalle.setItem(item);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(item.getPrecioCreditos());
            boleta.agregarDetalle(detalle);

            total += (long) item.getPrecioCreditos() * cantidad;
        }
        if (total > Integer.MAX_VALUE) {
            throw new ReglaNegocioException("El total de la compra es demasiado alto");
        }
        boleta.setTotal((int) total);

        Boleta guardada = boletaRepository.save(boleta);
        log.info("Boleta {} emitida a {} por {} créditos", guardada.getId(), correo, total);
        return assembler.toDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<BoletaDTO> listarDe(String correo) {
        return boletaRepository.findByUsuarioCorreoOrderByFechaDesc(correo).stream()
                .map(assembler::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<BoletaDTO> listarTodas() {
        return boletaRepository.findAllByOrderByFechaDesc().stream()
                .map(assembler::toDTO).toList();
    }
}
