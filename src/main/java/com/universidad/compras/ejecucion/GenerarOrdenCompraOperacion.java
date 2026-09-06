package com.universidad.compras.ejecucion;

/** Comando concreto: genera la orden de compra con el proveedor y puede cancelarla despues. */
public class GenerarOrdenCompraOperacion implements OperacionSolicitud {

    private final OrdenCompraService ordenCompraService;
    private final String solicitudId;
    private final String proveedor;
    private String numeroOrden;
    private boolean ejecutada = false;

    public GenerarOrdenCompraOperacion(OrdenCompraService ordenCompraService, String solicitudId, String proveedor) {
        this.ordenCompraService = ordenCompraService;
        this.solicitudId = solicitudId;
        this.proveedor = proveedor;
    }

    @Override
    public void ejecutar() {
        numeroOrden = ordenCompraService.generar(solicitudId, proveedor);
        ejecutada = true;
    }

    @Override
    public void deshacer() {
        if (ejecutada) {
            ordenCompraService.cancelar(numeroOrden);
            ejecutada = false;
        }
    }

    @Override
    public String getDescripcion() {
        return "Orden de compra para " + solicitudId + " con proveedor " + proveedor;
    }

    @Override
    public boolean isEjecutada() {
        return ejecutada;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }
}
