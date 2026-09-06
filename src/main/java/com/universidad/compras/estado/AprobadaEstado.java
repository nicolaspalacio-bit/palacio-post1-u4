package com.universidad.compras.estado;

/** Una solicitud aprobada solo puede ejecutarse o cancelarse (antes de contactar al proveedor). */
public class AprobadaEstado implements EstadoSolicitud {

    @Override
    public boolean ejecutar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EjecutadaEstado(), "EJECUTADA");
        return true;
    }

    @Override
    public boolean cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new CanceladaEstado(), "CANCELADA");
        return true;
    }

    @Override
    public String nombre() {
        return "APROBADA";
    }
}
