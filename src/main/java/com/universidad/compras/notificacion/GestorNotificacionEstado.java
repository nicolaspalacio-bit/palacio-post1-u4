package com.universidad.compras.notificacion;

import com.universidad.compras.estado.TransicionListener;
import com.universidad.compras.modelo.Solicitud;

import java.util.ArrayList;
import java.util.List;

/**
 * Necesidad 3 - Sujeto (Subject) del patron Observer.
 *
 * Mantiene la lista de reacciones suscritas y las dispara, una por una, cada vez que
 * recibe el aviso de que una solicitud cambio de estado. Implementa TransicionListener
 * para poder registrarse directamente en un ContextoSolicitud (Necesidad 4) sin que ese
 * paquete conozca nada de notificaciones: desde su perspectiva, el gestor es solo "algo
 * que quiere enterarse cuando ocurre una transicion".
 *
 * Por defecto se suscriben las tres reacciones exigidas (correo, dashboard, auditoria);
 * cualquier otra puede agregarse despues con suscribir(...) sin modificar esta clase.
 */
public class GestorNotificacionEstado implements TransicionListener {

    private final List<ObservadorCambioEstado> suscriptores = new ArrayList<>();

    public static GestorNotificacionEstado conReaccionesEstandar() {
        GestorNotificacionEstado gestor = new GestorNotificacionEstado();
        gestor.suscribir(new NotificadorCorreo());
        gestor.suscribir(new ActualizadorDashboard());
        gestor.suscribir(new RegistradorAuditoria());
        return gestor;
    }

    public void suscribir(ObservadorCambioEstado observador) {
        suscriptores.add(observador);
    }

    public void notificar(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
        for (ObservadorCambioEstado observador : suscriptores) {
            observador.onCambioEstado(solicitud, estadoAnterior, estadoNuevo);
        }
    }

    @Override
    public void onTransicionCompletada(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
        notificar(solicitud, estadoAnterior, estadoNuevo);
    }

    public int totalSuscriptores() {
        return suscriptores.size();
    }
}
