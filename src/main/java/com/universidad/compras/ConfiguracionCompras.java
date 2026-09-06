package com.universidad.compras;

import com.universidad.compras.aprobacion.ServicioAprobacion;
import com.universidad.compras.aprobacion.ServicioAprobacionImpl;
import com.universidad.compras.ejecucion.EjecutorSolicitudService;
import com.universidad.compras.ejecucion.HistorialOperaciones;
import com.universidad.compras.ejecucion.OrdenCompraService;
import com.universidad.compras.ejecucion.PresupuestoService;
import com.universidad.compras.notificacion.GestorNotificacionEstado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cablea los componentes de las cuatro necesidades en el contexto de Spring.
 * Ni ServicioAprobacion, PresupuestoService, OrdenCompraService ni ControladorSolicitudes
 * se modifican: esta clase es nueva y solo los conecta entre si.
 */
@Configuration
public class ConfiguracionCompras {

    @Bean
    public GestorNotificacionEstado gestorNotificacionEstado() {
        return GestorNotificacionEstado.conReaccionesEstandar();
    }

    @Bean
    public ServicioAprobacion servicioAprobacion(GestorNotificacionEstado gestorNotificacionEstado) {
        return new ServicioAprobacionImpl(gestorNotificacionEstado);
    }

    @Bean
    public PresupuestoService presupuestoService() {
        return new PresupuestoService();
    }

    @Bean
    public OrdenCompraService ordenCompraService() {
        return new OrdenCompraService();
    }

    @Bean
    public HistorialOperaciones historialOperaciones() {
        return new HistorialOperaciones();
    }

    @Bean
    public EjecutorSolicitudService ejecutorSolicitudService(PresupuestoService presupuestoService,
                                                              OrdenCompraService ordenCompraService,
                                                              HistorialOperaciones historialOperaciones,
                                                              GestorNotificacionEstado gestorNotificacionEstado) {
        return new EjecutorSolicitudService(presupuestoService, ordenCompraService,
                historialOperaciones, gestorNotificacionEstado);
    }
}
