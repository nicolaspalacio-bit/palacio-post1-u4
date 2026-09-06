# ComprasUDES — Patrones de Comportamiento

Post-contenido de la Unidad 4 de Patrones de Diseño de Software. Un único proyecto
Spring Boot (`compras-comportamiento`) que resuelve cuatro necesidades reales del
backend de ComprasUDES, el sistema interno de solicitudes de compra corporativas de
la universidad, aplicando patrones de comportamiento sobre código ya existente que
no podía modificarse.

## Tabla de contenido

- [Descripción](#descripción)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Cómo ejecutar](#cómo-ejecutar)
- [Decisiones de diseño](#decisiones-de-diseño)
  - [Necesidad 1 — Aprobación por niveles jerárquicos](#necesidad-1--aprobación-por-niveles-jerárquicos)
  - [Necesidad 2 — Ejecución reversible de solicitudes](#necesidad-2--ejecución-reversible-de-solicitudes)
  - Necesidad 3 y 4 — se documentan al cerrar la Parte 2
- [Herramientas utilizadas](#herramientas-utilizadas)

## Descripción

El equipo de Compras reportó cuatro problemas de diseño sobre el mismo backend.
Para cada uno, el enunciado describía el síntoma y el código que no podía tocarse,
pero no el patrón a aplicar; identificarlo — y descartar con argumentos técnicos la
alternativa más parecida — es el objetivo de este repositorio.

| Necesidad | Síntoma de diseño                                            | Patrón aplicado           |
|-----------|---------------------------------------------------------------|----------------------------|
| 1         | Una solicitud debe recorrer decisores independientes hasta que alguien la resuelve, sin que el punto de entrada conozca cuántos son | Chain of Responsibility |
| 2         | Dos operaciones ejecutables sobre una solicitud deben poder deshacerse por separado, con historial completo | Command |
| 3         | *(Parte 2)* | *(por definir)* |
| 4         | *(Parte 2)* | *(por definir)* |

## Arquitectura del proyecto

```
com.universidad.compras
├── ComprasApp.java              punto de entrada Spring Boot
├── ConfiguracionCompras.java    ensambla los beans de las 4 necesidades
├── modelo/
│   └── Solicitud.java           entidad compartida (dada)
├── aprobacion/                  Necesidad 1 — Chain of Responsibility
│   ├── ServicioAprobacion.java          (dado — contrato)
│   ├── ResultadoAprobacion.java         (dado)
│   ├── ControladorSolicitudes.java      (dado — no modificado)
│   ├── NivelAprobacion.java             eslabon de la cadena
│   ├── NivelAprobacionBase.java         mecanismo comun de delegacion
│   ├── SupervisorArea.java              hasta $2.000.000
│   ├── GerenteArea.java                 hasta $10.000.000
│   ├── DirectorFinanciero.java          sin limite superior
│   ├── RevisorCumplimientoNormativo.java  nivel condicional (INTERNACIONAL)
│   └── ServicioAprobacionImpl.java      ensambla y resuelve la cadena
└── ejecucion/                   Necesidad 2 — Command
    ├── PresupuestoService.java          (dado — no modificado)
    ├── OrdenCompraService.java          (dado — no modificado)
    ├── OperacionSolicitud.java          contrato ejecutar/deshacer
    ├── ReservarPresupuestoOperacion.java
    ├── GenerarOrdenCompraOperacion.java
    ├── HistorialOperaciones.java        historial ordenado por solicitud
    └── EjecutorSolicitudService.java    invoker
```

## Cómo ejecutar

```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```

## Decisiones de diseño

### Necesidad 1 — Aprobación por niveles jerárquicos

**Patrón aplicado: Chain of Responsibility.**

El síntoma real no es "elegir entre varias reglas de negocio", sino que una
solicitud debe recorrer una secuencia de decisores independientes —Supervisor,
Gerente, Director, y ahora también el Revisor de Cumplimiento Normativo para
categoría INTERNACIONAL— donde cada uno decide con autonomía si resuelve o
delega, y esa secuencia debe poder crecer o reordenarse sin que
`ControladorSolicitudes` ni el punto que dispara la evaluación sepan cuántos
niveles existen. Eso es exactamente lo que Chain of Responsibility modela: cada
`NivelAprobacion` solo conoce a quien delega si no puede resolver, nunca al
conjunto completo de la cadena.

La alternativa mas cercana, Command, se descarto para este problema puntual
porque Command resuelve un requisito distinto: convertir una accion en un
objeto con capacidad de deshacerse y quedar en un historial. Aqui no hay
ninguna accion que deba revertirse — una vez que un nivel aprueba o rechaza,
la decision es definitiva — y no hay ningun historial de "multiples niveles
que resolvieron la misma solicitud" que consultar despues. Encajar Command en
este escenario obligaria a reconstruir manualmente, dentro de cada comando, la
logica de enrutamiento que Chain of Responsibility ya resuelve de forma
estructural: quien evalua primero, a quien le toca despues y bajo que condicion
se detiene la cadena. Ese enrutamiento secuencial condicional es precisamente
lo que Command no modela por si solo.

El nivel adicional para solicitudes internacionales se resolvio insertando
`RevisorCumplimientoNormativo` como primer eslabon de la cadena, construida
en `ServicioAprobacionImpl`. Ese nivel solo toma la decision cuando la
categoria es INTERNACIONAL; para cualquier otra categoria delega de inmediato
al siguiente eslabon, dejando la evaluacion por monto intacta. Agregar, quitar
o reordenar un nivel es cuestion de cambiar la lista con la que se construye
la cadena en un unico lugar — ningun nivel concreto, ni `ControladorSolicitudes`,
necesitan modificarse para ello.

### Necesidad 2 — Ejecución reversible de solicitudes

**Patrón aplicado: Command.**

Reservar presupuesto y generar la orden de compra son dos operaciones
discretas que un mismo actor (el equipo de Compras) decide ejecutar y,
eventualmente, deshacer de forma independiente, y que deben quedar
registradas en orden para poder inspeccionarse despues. Eso es exactamente el
problema que Command resuelve: cada operacion se encapsula como un objeto
(`ReservarPresupuestoOperacion`, `GenerarOrdenCompraOperacion`) con su propio
`ejecutar()` y `deshacer()`, y un invoker (`EjecutorSolicitudService`) las
dispara y las registra en un `HistorialOperaciones` que conserva la lista
completa por solicitud, no solo la ultima operacion.

Comparada explicitamente con la Necesidad 1: aqui no hay ningun decisor
evaluando condiciones para decidir si delega o resuelve una peticion entrante,
que es el escenario que justifica Chain of Responsibility. Aplicar Chain of
Responsibility a este problema no tendria sentido porque no existe una
secuencia de decisores por los que la solicitud deba "pasar" — reservar
presupuesto y generar orden no son alternativas entre las que alguien elige
ni pasos que se delegan condicionalmente, son dos acciones concretas que se
ejecutan ambas, en orden, y que individualmente deben poder deshacerse sin
tocar la otra. Ese requisito de reversibilidad granular y de historial
inspeccionable es ajeno a Chain of Responsibility, que ni siquiera conserva un
objeto persistente por decision tomada.

## Herramientas utilizadas

- Java 17, Spring Boot 3.2, Apache Maven
- JUnit 5
- Visual Studio Code / IntelliJ IDEA, Git, GitHub
