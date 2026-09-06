package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;

/**
 * Punto de enganche minimo entre el mecanismo de transicion de estados (Necesidad 4)
 * y cualquier mecanismo externo que deba reaccionar a un cambio de estado ya ocurrido
 * (Necesidad 3). El paquete "estado" no conoce ni depende de "notificacion": solo invoca
 * este hook cada vez que una transicion se completa con exito. Quien implemente esta
 * interfaz decide que hacer con el aviso.
 */
public interface TransicionListener {
    void onTransicionCompletada(Solicitud solicitud, String estadoAnterior, String estadoNuevo);
}
