package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/** Puede aprobar solicitudes de hasta $10.000.000; por encima, remite al Director Financiero. */
public class GerenteArea extends NivelAprobacionBase {

    private static final double LIMITE = 10_000_000;

    @Override
    protected boolean puedeResolver(Solicitud solicitud) {
        return solicitud.getMonto() <= LIMITE;
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, nombreNivel(),
                "Aprobada dentro del limite del Gerente de Area ($" + LIMITE + ")");
    }

    @Override
    protected String nombreNivel() {
        return "Gerente de Área";
    }
}
