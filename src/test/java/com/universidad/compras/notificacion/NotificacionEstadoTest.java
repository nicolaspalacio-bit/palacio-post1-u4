// src/test/java/com/universidad/compras/notificacion/NotificacionEstadoTest.java
package com.universidad.compras.notificacion;

import com.universidad.compras.estado.ContextoSolicitud;
import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionEstadoTest {

    @Test
    void cambiarEstadoDisparaLasTresReaccionesSinLanzarExcepcion() {
        Solicitud s = new Solicitud("S-020", "ana@udes.edu.co", 2500000, "SOFTWARE", "CC-100");
        GestorNotificacionEstado mecanismo = GestorNotificacionEstado.conReaccionesEstandar();
        assertEquals(3, mecanismo.totalSuscriptores());

        ContextoSolicitud contexto = new ContextoSolicitud(s);
        contexto.registrarListener(mecanismo);

        assertDoesNotThrow(() -> contexto.aprobar("Supervisor de Área"));
    }

    @Test
    void agregarUnCuartoSuscriptorDePruebaNoRequiereModificarElMecanismo() {
        // registrar un colector de prueba adicional (sin tocar la clase central
        // del mecanismo) y verificar que tambien reacciona al cambio de estado.
        Solicitud s = new Solicitud("S-021", "luis@udes.edu.co", 900000, "MATERIAL_OFICINA", "CC-200");
        GestorNotificacionEstado mecanismo = GestorNotificacionEstado.conReaccionesEstandar();

        List<String> eventosCapturados = new ArrayList<>();
        ObservadorCambioEstado colectorDePrueba =
                (solicitud, estadoAnterior, estadoNuevo) ->
                        eventosCapturados.add(estadoAnterior + "->" + estadoNuevo);
        mecanismo.suscribir(colectorDePrueba);
        assertEquals(4, mecanismo.totalSuscriptores());

        ContextoSolicitud contexto = new ContextoSolicitud(s);
        contexto.registrarListener(mecanismo);

        assertDoesNotThrow(() -> contexto.aprobar("Gerente de Área"));
        assertEquals(1, eventosCapturados.size());
        assertEquals("PENDIENTE->APROBADA", eventosCapturados.get(0));
    }
}
