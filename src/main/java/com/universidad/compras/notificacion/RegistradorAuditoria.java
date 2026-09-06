package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

/** Reaccion concreta: deja constancia del cambio en el log de auditoria. */
public class RegistradorAuditoria implements ObservadorCambioEstado {

    @Override
    public void onCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
        String detalle = "transicion " + estadoAnterior + " -> " + estadoNuevo;
        ClientesNotificacion.registrarAuditoria(solicitud.getId(), estadoNuevo, detalle);
    }
}
