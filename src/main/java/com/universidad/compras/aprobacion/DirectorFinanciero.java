package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/** Ultimo eslabon de la cadena por monto: aprueba cualquier monto, sin limite superior. */
public class DirectorFinanciero extends NivelAprobacionBase {

    @Override
    protected boolean puedeResolver(Solicitud solicitud) {
        return true;
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, nombreNivel(),
                "Aprobada por el Director Financiero, sin limite superior de monto");
    }

    @Override
    protected String nombreNivel() {
        return "Director Financiero";
    }
}
