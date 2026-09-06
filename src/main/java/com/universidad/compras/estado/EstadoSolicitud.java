package com.universidad.compras.estado;

/**
 * Necesidad 4 - Patron State.
 *
 * Cada implementacion representa un estado posible de una Solicitud y decide,
 * por si misma, cuales de las cuatro operaciones (aprobar, rechazar, ejecutar,
 * cancelar) son validas desde ese estado y a que estado se transiciona si lo son.
 * Agregar un estado nuevo (por ejemplo EN_ESPERA_PROVEEDOR) significa agregar una
 * clase nueva, no tocar if/else existentes en ningun otro punto del sistema.
 *
 * Los metodos por defecto representan "operacion invalida en este estado":
 * no cambian nada y devuelven false, para que el ContextoSolicitud sepa que
 * la transicion fue rechazada sin necesidad de excepciones para el caso normal.
 */
public interface EstadoSolicitud {

    default boolean aprobar(ContextoSolicitud contexto, String nivelResolutor) {
        return false;
    }

    default boolean rechazar(ContextoSolicitud contexto, String nivelResolutor) {
        return false;
    }

    default boolean ejecutar(ContextoSolicitud contexto) {
        return false;
    }

    default boolean cancelar(ContextoSolicitud contexto) {
        return false;
    }

    String nombre();
}
