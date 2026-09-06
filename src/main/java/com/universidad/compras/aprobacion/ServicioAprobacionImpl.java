package com.universidad.compras.aprobacion;

import com.universidad.compras.estado.ContextoSolicitud;
import com.universidad.compras.estado.TransicionListener;
import com.universidad.compras.modelo.Solicitud;

import java.util.List;

/**
 * Punto de ensamblaje de la cadena de responsabilidad (Necesidad 1).
 *
 * El orden y la composicion de los niveles se decide unicamente aqui, a partir de una
 * lista ordenada de eslabones: agregar, quitar o reordenar un nivel (o insertar el
 * Revisor de Cumplimiento Normativo antes del nivel que corresponda por monto) es
 * cuestion de cambiar esta lista, sin tocar NivelAprobacion, ControladorSolicitudes
 * ni ningun nivel concreto ya existente.
 *
 * Tras obtener la decision de la cadena, este servicio es quien confirma la transicion
 * de la Solicitud a traves de ContextoSolicitud (Necesidad 4), que a su vez dispara la
 * notificacion (Necesidad 3) si se registro un listener. Los niveles concretos nunca
 * tocan el estado de la solicitud ni conocen el mecanismo de notificacion.
 */
public class ServicioAprobacionImpl implements ServicioAprobacion {

    private final NivelAprobacion primerNivel;
    private final TransicionListener notificador;

    public ServicioAprobacionImpl(TransicionListener notificador) {
        this.notificador = notificador;
        this.primerNivel = construirCadena();
    }

    private NivelAprobacion construirCadena() {
        List<NivelAprobacion> niveles = List.of(
                new RevisorCumplimientoNormativo(),
                new SupervisorArea(),
                new GerenteArea(),
                new DirectorFinanciero()
        );
        for (int i = 0; i < niveles.size() - 1; i++) {
            niveles.get(i).setSiguiente(niveles.get(i + 1));
        }
        return niveles.get(0);
    }

    @Override
    public ResultadoAprobacion evaluar(Solicitud solicitud) {
        ResultadoAprobacion resultado = primerNivel.evaluar(solicitud);

        ContextoSolicitud contexto = new ContextoSolicitud(solicitud);
        if (notificador != null) {
            contexto.registrarListener(notificador);
        }
        if (resultado.isAprobada()) {
            contexto.aprobar(resultado.getNivelResolutor());
        } else {
            contexto.rechazar(resultado.getNivelResolutor());
        }
        return resultado;
    }
}
