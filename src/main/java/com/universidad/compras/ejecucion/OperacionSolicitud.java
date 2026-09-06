package com.universidad.compras.ejecucion;

/**
 * Necesidad 2 - Patron Command.
 *
 * Cada implementacion encapsula una operacion concreta sobre una solicitud aprobada
 * (reservar presupuesto, generar orden de compra) como un objeto con su propia logica
 * de ejecucion y de reversion. Esto permite que el equipo de Compras deshaga una
 * operacion puntual sin afectar a las demas ya ejecutadas sobre la misma solicitud,
 * y que el HistorialOperaciones conserve cada comando para poder inspeccionarlo despues.
 */
public interface OperacionSolicitud {

    void ejecutar();

    void deshacer();

    String getDescripcion();

    boolean isEjecutada();
}
