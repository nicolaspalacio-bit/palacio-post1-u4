package com.universidad.compras.ejecucion;

import com.universidad.compras.estado.ContextoSolicitud;
import com.universidad.compras.estado.TransicionListener;
import com.universidad.compras.modelo.Solicitud;

import java.util.List;

/**
 * Necesidad 2 - Invoker del patron Command.
 *
 * Sobre una solicitud APROBADA, crea y ejecuta los dos comandos (reservar presupuesto,
 * generar orden de compra), los registra en el HistorialOperaciones para que puedan
 * inspeccionarse o deshacerse de forma independiente, y confirma la transicion a
 * EJECUTADA a traves de ContextoSolicitud (Necesidad 4), que dispara la notificacion
 * (Necesidad 3) sin que esta clase conozca ese mecanismo directamente.
 */
public class EjecutorSolicitudService {

    private final PresupuestoService presupuestoService;
    private final OrdenCompraService ordenCompraService;
    private final HistorialOperaciones historial;
    private final TransicionListener notificador;

    public EjecutorSolicitudService(PresupuestoService presupuestoService,
                                     OrdenCompraService ordenCompraService,
                                     HistorialOperaciones historial,
                                     TransicionListener notificador) {
        this.presupuestoService = presupuestoService;
        this.ordenCompraService = ordenCompraService;
        this.historial = historial;
        this.notificador = notificador;
    }

    /**
     * Ejecuta las dos operaciones sobre una solicitud aprobada y, si el estado lo permite,
     * la transiciona a EJECUTADA. Devuelve los comandos ejecutados en orden para que el
     * llamador pueda deshacer cualquiera de ellos despues.
     */
    public List<OperacionSolicitud> ejecutar(Solicitud solicitud, String proveedor) {
        ContextoSolicitud contexto = new ContextoSolicitud(solicitud);
        if (notificador != null) {
            contexto.registrarListener(notificador);
        }

        if (!contexto.ejecutar()) {
            return List.of();
        }

        OperacionSolicitud reserva = new ReservarPresupuestoOperacion(
                presupuestoService, solicitud.getCentroCosto(), solicitud.getMonto());
        OperacionSolicitud orden = new GenerarOrdenCompraOperacion(
                ordenCompraService, solicitud.getId(), proveedor);

        reserva.ejecutar();
        historial.registrar(solicitud.getId(), reserva);

        orden.ejecutar();
        historial.registrar(solicitud.getId(), orden);

        return List.of(reserva, orden);
    }

    public void deshacer(OperacionSolicitud operacion) {
        operacion.deshacer();
    }

    public List<OperacionSolicitud> obtenerHistorial(String solicitudId) {
        return historial.historialDe(solicitudId);
    }
}
