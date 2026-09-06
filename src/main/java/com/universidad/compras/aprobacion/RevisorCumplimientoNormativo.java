package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/**
 * Nivel adicional anunciado por el equipo de Compras: se intercala como primer eslabon
 * de la cadena y solo toma la decision cuando la categoria es INTERNACIONAL, sin que el
 * resto de la cadena (ni ControladorSolicitudes) necesite saber que este nivel existe.
 * Para cualquier otra categoria, simplemente delega en el siguiente nivel, dejando intacta
 * la evaluacion por monto.
 */
public class RevisorCumplimientoNormativo extends NivelAprobacionBase {

    private static final String CATEGORIA_APLICABLE = "INTERNACIONAL";

    @Override
    protected boolean puedeResolver(Solicitud solicitud) {
        return CATEGORIA_APLICABLE.equals(solicitud.getCategoria());
    }

    @Override
    protected ResultadoAprobacion resolver(Solicitud solicitud) {
        return new ResultadoAprobacion(true, nombreNivel(),
                "Solicitud internacional evaluada y aprobada por cumplimiento normativo");
    }

    @Override
    protected String nombreNivel() {
        return "Revisor de Cumplimiento Normativo";
    }
}
