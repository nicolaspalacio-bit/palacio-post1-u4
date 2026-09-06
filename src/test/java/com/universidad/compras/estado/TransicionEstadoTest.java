// src/test/java/com/universidad/compras/estado/TransicionEstadoTest.java
package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransicionEstadoTest {

    @Test
    void ejecutarUnaSolicitudAprobadaLaDejaEjecutada() {
        Solicitud s = new Solicitud("S-030", "luis@udes.edu.co", 3000000, "MATERIAL_OFICINA", "CC-200");
        s.setEstado("APROBADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        // invocar aqui la operacion de ejecutar sobre "contexto"
        boolean resultado = contexto.ejecutar();
        assertTrue(resultado);
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void ejecutarUnaSolicitudPendienteSeRechazaSinCambiarElEstado() {
        Solicitud s = new Solicitud("S-031", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        // invocar aqui la operacion de ejecutar sobre "contexto"
        boolean resultado = contexto.ejecutar();
        assertFalse(resultado);
        assertEquals("PENDIENTE", s.getEstado());
    }

    @Test
    void unaSolicitudEjecutadaNoPuedeVolverAEjecutarse() {
        Solicitud s = new Solicitud("S-032", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        s.setEstado("EJECUTADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        // invocar aqui la operacion de ejecutar sobre "contexto"
        boolean resultado = contexto.ejecutar();
        assertFalse(resultado);
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void unaSolicitudRechazadaNoPuedeCancelarseNiAprobarse() {
        Solicitud s = new Solicitud("S-033", "ana@udes.edu.co", 700000, "SOFTWARE", "CC-100");
        s.setEstado("RECHAZADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        assertFalse(contexto.cancelar());
        assertFalse(contexto.aprobar("Supervisor de Área"));
        assertEquals("RECHAZADA", s.getEstado());
    }

    @Test
    void unaSolicitudAprobadaPuedeCancelarseAntesDeContactarAlProveedor() {
        Solicitud s = new Solicitud("S-034", "ana@udes.edu.co", 700000, "SOFTWARE", "CC-100");
        s.setEstado("APROBADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);
        assertTrue(contexto.cancelar());
        assertEquals("CANCELADA", s.getEstado());
    }
}
