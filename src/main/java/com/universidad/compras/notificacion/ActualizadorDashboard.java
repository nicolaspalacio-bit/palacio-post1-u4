package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

/** Reaccion concreta: refleja el nuevo estado en el tablero de contabilidad. */
public class ActualizadorDashboard implements ObservadorCambioEstado {

    @Override
    public void onCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
        ClientesNotificacion.actualizarDashboardContabilidad(solicitud.getId(), estadoNuevo, solicitud.getMonto());
    }
}
