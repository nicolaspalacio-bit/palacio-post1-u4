package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;

import java.util.ArrayList;
import java.util.List;

/**
 * Necesidad 4 - Contexto del patron State.
 *
 * Es el unico punto del sistema que conoce la Solicitud y su EstadoSolicitud actual.
 * Toda operacion de negocio sobre el ciclo de vida de una solicitud (aprobar, rechazar,
 * ejecutar, cancelar) pasa por aqui, que delega la decision al objeto de estado en turno
 * en lugar de repetir if/else sobre getEstado() en cada metodo. Ademas, por ser el unico
 * punto donde una transicion se confirma, es tambien el punto natural donde se conecta el
 * mecanismo de notificacion de la Necesidad 3 (ver TransicionListener): ningun handler de
 * aprobacion (Necesidad 1) ni el ejecutor de ordenes (Necesidad 2) notifican directamente,
 * simplemente invocan una operacion de este contexto y el contexto avisa por ellos.
 */
public class ContextoSolicitud {

    private final Solicitud solicitud;
    private EstadoSolicitud estadoActual;
    private final List<TransicionListener> listeners = new ArrayList<>();

    public ContextoSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
        this.estadoActual = mapearEstadoInicial(solicitud.getEstado());
    }

    private EstadoSolicitud mapearEstadoInicial(String estado) {
        return switch (estado) {
            case "APROBADA" -> new AprobadaEstado();
            case "RECHAZADA" -> new RechazadaEstado();
            case "EJECUTADA" -> new EjecutadaEstado();
            case "CANCELADA" -> new CanceladaEstado();
            default -> new PendienteEstado();
        };
    }

    public void registrarListener(TransicionListener listener) {
        listeners.add(listener);
    }

    public boolean aprobar(String nivelResolutor) {
        return estadoActual.aprobar(this, nivelResolutor);
    }

    public boolean rechazar(String nivelResolutor) {
        return estadoActual.rechazar(this, nivelResolutor);
    }

    public boolean ejecutar() {
        return estadoActual.ejecutar(this);
    }

    public boolean cancelar() {
        return estadoActual.cancelar(this);
    }

    public String getEstadoActual() {
        return estadoActual.nombre();
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    /** Invocado unicamente por las clases EstadoXxx al resolver una transicion valida. */
    void transicionarA(EstadoSolicitud nuevoEstado, String nombreEstado) {
        String anterior = solicitud.getEstado();
        this.estadoActual = nuevoEstado;
        solicitud.setEstado(nombreEstado);
        for (TransicionListener listener : listeners) {
            listener.onTransicionCompletada(solicitud, anterior, nombreEstado);
        }
    }
}
