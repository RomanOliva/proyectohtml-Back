package com.example.ms_warframe.assembler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.ms_warframe.DTO.BoletaDTO;
import com.example.ms_warframe.DTO.BoletaDetalleDTO;
import com.example.ms_warframe.model.Boleta;

@Component
public class BoletaAssembler {

    public BoletaDTO toDTO(Boleta boleta) {
        List<BoletaDetalleDTO> lineas = boleta.getDetalles().stream()
                .map(d -> new BoletaDetalleDTO(
                        d.getItem().getId(),
                        d.getItem().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getPrecioUnitario() * d.getCantidad()))
                .toList();
        return new BoletaDTO(boleta.getId(), boleta.getUsuario().getCorreo(),
                boleta.getFecha(), boleta.getTotal(), lineas);
    }
}
