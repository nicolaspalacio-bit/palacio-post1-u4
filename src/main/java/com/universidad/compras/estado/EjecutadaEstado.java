package com.universidad.compras.estado;

/** Estado terminal: una solicitud ya ejecutada no puede volver a ejecutarse ni cambiar de estado. */
public class EjecutadaEstado implements EstadoSolicitud {
    @Override
    public String nombre() {
        return "EJECUTADA";
    }
}
