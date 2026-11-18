package com.example.ms_warframe.model;

public enum ClasificacionPrecio {
    BAJO, INTERMEDIO, ALTO;

    public static ClasificacionPrecio de(int precio) {
        if (precio < 800) {
            return BAJO;
        }
        if (precio <= 1500) {
            return INTERMEDIO;
        }
        return ALTO;
    }
}
