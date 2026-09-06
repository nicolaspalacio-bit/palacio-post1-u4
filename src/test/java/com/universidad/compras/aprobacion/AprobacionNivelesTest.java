// src/test/java/com/universidad/compras/aprobacion/AprobacionNivelesTest.java
package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AprobacionNivelesTest {

    @Test
    void solicitudDentroDeAutoridadDelSupervisorSeAprueba() {
        ServicioAprobacion servicio = new ServicioAprobacionImpl(null);
        Solicitud s = new Solicitud("S-001", "ana@udes.edu.co", 1500000, "MATERIAL_OFICINA", "CC-100");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Supervisor de Área", r.getNivelResolutor());
    }

    @Test
    void solicitudQueSuperaAlSupervisorEscalaAlGerente() {
        ServicioAprobacion servicio = new ServicioAprobacionImpl(null);
        Solicitud s = new Solicitud("S-002", "luis@udes.edu.co", 6000000, "SOFTWARE", "CC-200");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Gerente de Área", r.getNivelResolutor());
    }

    @Test
    void solicitudInternacionalPasaPorCumplimientoAntesDelNivelPorMonto() {
        ServicioAprobacion servicio = new ServicioAprobacionImpl(null);
        Solicitud s = new Solicitud("S-003", "gerencia@udes.edu.co", 1000000, "INTERNACIONAL", "CC-300");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertEquals("Revisor de Cumplimiento Normativo", r.getNivelResolutor());
    }

    @Test
    void solicitudQueSuperaAlGerenteEscalaAlDirectorFinanciero() {
        ServicioAprobacion servicio = new ServicioAprobacionImpl(null);
        Solicitud s = new Solicitud("S-004", "cfo@udes.edu.co", 25000000, "SOFTWARE", "CC-400");
        ResultadoAprobacion r = servicio.evaluar(s);
        assertTrue(r.isAprobada());
        assertEquals("Director Financiero", r.getNivelResolutor());
    }

    @Test
    void laSolicitudQuedaEnEstadoAprobadaTrasResolverse() {
        ServicioAprobacion servicio = new ServicioAprobacionImpl(null);
        Solicitud s = new Solicitud("S-005", "ana@udes.edu.co", 500000, "MATERIAL_OFICINA", "CC-100");
        servicio.evaluar(s);
        assertEquals("APROBADA", s.getEstado());
        assertEquals("Supervisor de Área", s.getNivelResolutor());
    }
}
