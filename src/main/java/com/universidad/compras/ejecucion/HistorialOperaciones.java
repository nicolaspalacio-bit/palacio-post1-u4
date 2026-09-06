package com.universidad.compras.ejecucion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registra, en orden de ejecucion y por solicitud, cada OperacionSolicitud (Command) que
 * se ha invocado sobre ella. A diferencia de guardar solo "la ultima operacion", conserva
 * la lista completa para que cualquiera de ellas pueda inspeccionarse o deshacerse despues
 * de forma independiente, sin importar cuantas operaciones se hayan ejecutado desde entonces.
 */
public class HistorialOperaciones {

    private final Map<String, List<OperacionSolicitud>> operacionesPorSolicitud = new HashMap<>();

    public void registrar(String solicitudId, OperacionSolicitud operacion) {
        operacionesPorSolicitud
                .computeIfAbsent(solicitudId, id -> new ArrayList<>())
                .add(operacion);
    }

    public List<OperacionSolicitud> historialDe(String solicitudId) {
        return Collections.unmodifiableList(
                operacionesPorSolicitud.getOrDefault(solicitudId, List.of()));
    }

    public int totalOperaciones(String solicitudId) {
        return historialDe(solicitudId).size();
    }
}
