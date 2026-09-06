package com.universidad.compras.estado;

/** Estado inicial: puede aprobarse, rechazarse o cancelarse; no puede ejecutarse. */
public class PendienteEstado implements EstadoSolicitud {

    @Override
    public boolean aprobar(ContextoSolicitud contexto, String nivelResolutor) {
        contexto.getSolicitud().setNivelResolutor(nivelResolutor);
        contexto.transicionarA(new AprobadaEstado(), "APROBADA");
        return true;
    }

    @Override
    public boolean rechazar(ContextoSolicitud contexto, String nivelResolutor) {
        contexto.getSolicitud().setNivelResolutor(nivelResolutor);
        contexto.transicionarA(new RechazadaEstado(), "RECHAZADA");
        return true;
    }

    @Override
    public boolean cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new CanceladaEstado(), "CANCELADA");
        return true;
    }

    @Override
    public String nombre() {
        return "PENDIENTE";
    }
}
