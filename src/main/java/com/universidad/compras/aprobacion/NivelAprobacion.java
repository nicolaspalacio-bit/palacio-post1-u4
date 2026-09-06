package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

/**
 * Necesidad 1 - Patron Chain of Responsibility.
 *
 * Cada nivel decide, con el monto y la categoria de la solicitud, si esta dentro de su
 * propia autoridad (y entonces la resuelve) o si debe remitirla al siguiente eslabon.
 * ServicioAprobacionImpl es el unico responsable de decidir el orden de la cadena;
 * ningun nivel concreto conoce cuantos niveles existen despues de el ni antes.
 * Agregar, quitar o reordenar un nivel (como el Revisor de Cumplimiento Normativo)
 * es cuestion de cambiar como se ensambla la cadena, no de tocar esta interfaz
 * ni ControladorSolicitudes.
 */
public interface NivelAprobacion {

    void setSiguiente(NivelAprobacion siguiente);

    ResultadoAprobacion evaluar(Solicitud solicitud);
}
