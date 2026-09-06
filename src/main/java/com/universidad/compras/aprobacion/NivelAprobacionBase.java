package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/**
 * Encapsula el mecanismo generico de la cadena (guardar el siguiente eslabon y
 * delegarle la solicitud cuando este nivel no puede resolverla) para que cada
 * nivel concreto solo tenga que declarar su propio criterio de resolucion.
 */
public abstract class NivelAprobacionBase implements NivelAprobacion {

    private NivelAprobacion siguiente;

    @Override
    public void setSiguiente(NivelAprobacion siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public final ResultadoAprobacion evaluar(Solicitud solicitud) {
        if (puedeResolver(solicitud)) {
            return resolver(solicitud);
        }
        if (siguiente != null) {
            return siguiente.evaluar(solicitud);
        }
        return new ResultadoAprobacion(false, nombreNivel(),
                "Ningun nivel de la cadena pudo resolver la solicitud");
    }

    /** ¿Este nivel tiene autoridad para tomar la decision final sobre esta solicitud? */
    protected abstract boolean puedeResolver(Solicitud solicitud);

    /** Produce la decision final; solo se invoca cuando puedeResolver(...) es true. */
    protected abstract ResultadoAprobacion resolver(Solicitud solicitud);

    protected abstract String nombreNivel();
}
