// src/test/java/com/universidad/compras/ejecucion/EjecucionSolicitudTest.java
package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EjecucionSolicitudTest {

    @Test
    void ejecutarReservaPresupuestoYGeneraOrden() {
        Solicitud s = new Solicitud("S-010", "ana@udes.edu.co", 3000000, "SOFTWARE", "CC-100");
        s.setEstado("APROBADA");
        EjecutorSolicitudService ejecutor = new EjecutorSolicitudService(
                new PresupuestoService(), new OrdenCompraService(), new HistorialOperaciones(), null);
        List<OperacionSolicitud> operaciones = ejecutor.ejecutar(s, "Proveedor XYZ");
        assertEquals(2, operaciones.size());
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void deshacerSoloLaUltimaOperacionNoAfectaLaAnterior() {
        // ejecutar reservar presupuesto, luego generar orden; deshacer una vez
        // debe revertir solo la generacion de la orden, no la reserva.
        Solicitud s = new Solicitud("S-011", "luis@udes.edu.co", 4000000, "MATERIAL_OFICINA", "CC-200");
        s.setEstado("APROBADA");
        EjecutorSolicitudService ejecutor = new EjecutorSolicitudService(
                new PresupuestoService(), new OrdenCompraService(), new HistorialOperaciones(), null);
        assertDoesNotThrow(() -> {
            List<OperacionSolicitud> operaciones = ejecutor.ejecutar(s, "Proveedor ABC");
            OperacionSolicitud reserva = operaciones.get(0);
            OperacionSolicitud orden = operaciones.get(1);

            ejecutor.deshacer(orden);

            assertFalse(orden.isEjecutada());
            assertTrue(reserva.isEjecutada());
        });
    }

    @Test
    void elHistorialConservaTodasLasOperacionesNoSoloLaUltima() {
        HistorialOperaciones historial = new HistorialOperaciones();
        EjecutorSolicitudService ejecutor = new EjecutorSolicitudService(
                new PresupuestoService(), new OrdenCompraService(), historial, null);
        // tras ejecutar dos operaciones sobre una misma solicitud, el historial
        // consultable debe reportar tamano 2, no solo la ultima operacion.
        assertDoesNotThrow(() -> {
            Solicitud s = new Solicitud("S-012", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
            s.setEstado("APROBADA");
            ejecutor.ejecutar(s, "Proveedor QRS");
            assertEquals(2, ejecutor.obtenerHistorial("S-012").size());
        });
    }
}
