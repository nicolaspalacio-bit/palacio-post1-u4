package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/** Puede aprobar solicitudes de hasta $2.000.000; por encima, remite al Gerente de Area. */
public class SupervisorArea extends NivelAprobacionBase {

    private static final double LIMITE = 2_000_000;

    @Override
    protected boolean puedeResolver(Solicitud solicitud) {
        return solicitud.getMonto() <= LIMITE;
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, nombreNivel(),
                "Aprobada dentro del limite del Supervisor de Area ($" + LIMITE + ")");
    }

    @Override
    protected String nombreNivel() {
        return "Supervisor de Área";
    }
}
