package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

/**
 * Necesidad 3 - Patron Observer.
 *
 * Cada implementacion representa una reaccion independiente ante un cambio de estado
 * de una Solicitud. Ninguna conoce a las demas; el GestorNotificacionEstado las invoca
 * a todas por igual. Agregar una cuarta reaccion (por ejemplo, un canal interno del area)
 * significa escribir una clase nueva que implemente esta interfaz y suscribirla, sin tocar
 * el codigo que dispara el cambio de estado ni las demas reacciones ya registradas.
 */
public interface ObservadorCambioEstado {
    void onCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo);
}
