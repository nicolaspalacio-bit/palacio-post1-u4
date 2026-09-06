package com.universidad.compras.ejecucion;

/** Comando concreto: reserva el presupuesto de un centro de costo y puede liberarlo despues. */
public class ReservarPresupuestoOperacion implements OperacionSolicitud {

    private final PresupuestoService presupuestoService;
    private final String centroCosto;
    private final double monto;
    private boolean ejecutada = false;

    public ReservarPresupuestoOperacion(PresupuestoService presupuestoService, String centroCosto, double monto) {
        this.presupuestoService = presupuestoService;
        this.centroCosto = centroCosto;
        this.monto = monto;
    }

    @Override
    public void ejecutar() {
        presupuestoService.reservar(centroCosto, monto);
        ejecutada = true;
    }

    @Override
    public void deshacer() {
        if (ejecutada) {
            presupuestoService.liberar(centroCosto, monto);
            ejecutada = false;
        }
    }

    @Override
    public String getDescripcion() {
        return "Reserva de $" + monto + " en el centro de costo " + centroCosto;
    }

    @Override
    public boolean isEjecutada() {
        return ejecutada;
    }
}
