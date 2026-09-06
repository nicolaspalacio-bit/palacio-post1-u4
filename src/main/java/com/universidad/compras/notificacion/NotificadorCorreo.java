package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

/** Reaccion concreta: avisa por correo al solicitante que su solicitud cambio de estado. */
public class NotificadorCorreo implements ObservadorCambioEstado {

    @Override
    public void onCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
        String asunto = "Actualizacion de tu solicitud " + solicitud.getId();
        String cuerpo = "Tu solicitud paso de " + estadoAnterior + " a " + estadoNuevo + ".";
        ClientesNotificacion.enviarCorreo(solicitud.getSolicitanteEmail(), asunto, cuerpo);
    }
}
