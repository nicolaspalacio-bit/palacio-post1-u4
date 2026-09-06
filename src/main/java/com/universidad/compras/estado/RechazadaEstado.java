package com.universidad.compras.estado;

/** Estado terminal: ninguna operacion posterior es valida. */
public class RechazadaEstado implements EstadoSolicitud {
    @Override
    public String nombre() {
        return "RECHAZADA";
    }
}
