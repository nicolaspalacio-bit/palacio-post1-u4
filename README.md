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
  - [Necesidad 3 — Notificaciones ante cambio de estado](#necesidad-3--notificaciones-ante-cambio-de-estado)
  - [Necesidad 4 — Reglas de transición según el estado actual](#necesidad-4--reglas-de-transición-según-el-estado-actual)
  - [Reflexión — otros tres patrones](#reflexión--otros-tres-patrones-no-rubricada)
- [Herramientas utilizadas](#herramientas-utilizadas)
- [Conclusiones](#conclusiones)

## Descripción



| Necesidad | Síntoma de diseño                                            | Patrón aplicado           |
|-----------|---------------------------------------------------------------|----------------------------|
| 1         | Una solicitud debe recorrer decisores independientes hasta que alguien la resuelve, sin que el punto de entrada conozca cuántos son | Chain of Responsibility |
| 2         | Dos operaciones ejecutables sobre una solicitud deben poder deshacerse por separado, con historial completo | Command |
| 3         | Al cambiar el estado de una solicitud, tres módulos externos deben reaccionar sin que el punto de cambio los conozca | Observer |
| 4         | Las reglas sobre qué operaciones son válidas estaban dispersas en if/else que revisaban el estado en varios métodos | State |

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
├── ejecucion/                   Necesidad 2 — Command
│   ├── PresupuestoService.java          (dado — no modificado)
│   ├── OrdenCompraService.java          (dado — no modificado)
│   ├── OperacionSolicitud.java          contrato ejecutar/deshacer
│   ├── ReservarPresupuestoOperacion.java
│   ├── GenerarOrdenCompraOperacion.java
│   ├── HistorialOperaciones.java        historial ordenado por solicitud
│   └── EjecutorSolicitudService.java    invoker
├── notificacion/                Necesidad 3 — Observer
│   ├── ClientesNotificacion.java        (dado — no modificado)
│   ├── ObservadorCambioEstado.java      interfaz del observador
│   ├── NotificadorCorreo.java
│   ├── ActualizadorDashboard.java
│   ├── RegistradorAuditoria.java
│   └── GestorNotificacionEstado.java    sujeto (subject)
└── estado/                      Necesidad 4 — State
    ├── EstadoSolicitud.java             interfaz del estado
    ├── PendienteEstado.java
    ├── AprobadaEstado.java
    ├── RechazadaEstado.java
    ├── EjecutadaEstado.java
    ├── CanceladaEstado.java
    ├── TransicionListener.java          hook minimo hacia notificacion
    └── ContextoSolicitud.java           contexto: unico punto que cambia el estado
```

El paquete `estado` es, a propósito, el único lugar del sistema donde una
`Solicitud` cambia de estado: tanto `ServicioAprobacionImpl` (Necesidad 1) como
`EjecutorSolicitudService` (Necesidad 2) confirman sus decisiones a través de
`ContextoSolicitud` en lugar de asignar el estado directamente. Eso es lo que
permite que la Necesidad 3 se conecte en un único punto — el propio
`ContextoSolicitud`, mediante `TransicionListener` — sin que los niveles de
aprobación ni el ejecutor de órdenes conozcan nada sobre correos, dashboards
o auditoría.

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

### Necesidad 3 — Notificaciones ante cambio de estado

**Patrón aplicado: Observer.**

El requisito es desacoplar a quien cambia el estado de una solicitud de quienes
deben reaccionar a ese cambio: correo al solicitante, actualización del
dashboard de contabilidad y registro de auditoría son tres reacciones
independientes entre sí, y el punto donde el estado cambia no debe conocer a
ninguna de las tres directamente, ni modificarse si mañana se agrega una
cuarta. Ese es el problema que Observer resuelve por definición: un sujeto
(`GestorNotificacionEstado`) mantiene una lista de observadores
(`ObservadorCambioEstado`) y los notifica a todos por igual, sin saber qué
hace cada uno. Agregar una cuarta reacción (o, como exige el test, un
colector de prueba) es escribir una clase nueva e invocar `suscribir(...)`;
`ContextoSolicitud` y los niveles de aprobación no cambian en absoluto.

La comparación explícita pedida es con la Necesidad 4, porque a primera vista
ambas hablan de "reaccionar a un estado". La diferencia esta en qué objeto
cambia su comportamiento y quién es dueño de ese comportamiento: el
comportamiento que varía en la Necesidad 4 es el de la propia `Solicitud` —
qué operaciones tienen sentido según en qué momento de su historia se
encuentra ella misma—. Aquí, en cambio, la solicitud ya cambió de estado por
completo antes de que cualquiera de las tres reacciones exista; son objetos
enteramente ajenos a `Solicitud` —correo, contabilidad, auditoría— quienes se
enteran y actúan después del hecho, y ninguno de los tres tiene, por sí mismo,
un "estado propio" que gobierne qué operaciones puede o no realizar. Forzar
State en este escenario significaría inventar una jerarquía de estados para
objetos que en realidad siempre hacen lo mismo cada vez que se les notifica —
enviar el correo, escribir el registro—, sin ninguna transición real que
representar.

### Necesidad 4 — Reglas de transición según el estado actual

**Patrón aplicado: State.**

El síntoma reportado es literal: qué operaciones son válidas en cada momento
—aprobar, rechazar, ejecutar, cancelar— se revisaba repetidamente con
`if/else` sobre `getEstado()` en varios métodos, y agregar un estado nuevo
obligaba a revisar y modificar esos métodos a la vez para no dejar una
combinación sin cubrir. State resuelve exactamente esto: cada estado posible
(`PendienteEstado`, `AprobadaEstado`, `RechazadaEstado`, `EjecutadaEstado`,
`CanceladaEstado`) es una clase que decide, por sí misma, cuáles operaciones
acepta y a qué estado transiciona si las acepta; `ContextoSolicitud` solo
delega en el estado actual. Agregar `EN_ESPERA_PROVEEDOR` el próximo semestre
significa escribir una clase nueva que implemente `EstadoSolicitud`, no tocar
condicionales existentes en ningún otro punto del sistema.

Esta es la comparación más sutil del laboratorio porque Strategy comparte con
State la misma forma estructural: una interfaz común con varias
implementaciones intercambiables. Lo que los separa es quién decide qué
comportamiento está activo. En Strategy —como el patrón ya visto en la guía,
donde un cliente externo elige e inyecta explícitamente la estrategia que
quiere usar en ese momento (por ejemplo, un carrito de compras seleccionando
qué descuento aplicar)— la decisión vive fuera del objeto y no hay ninguna
transición entre estrategias: son alternativas paralelas sin relación entre
sí. Aquí no hay ningún cliente externo seleccionando entre implementaciones
intercambiables en cada llamada: es la propia solicitud, según en qué estado
se encuentra en ese instante de su historia, quien determina qué es válido
hacer con ella, y cada operación válida además la hace transicionar a otro
estado como parte de resolverse — algo que un conjunto de estrategias
independientes entre sí no hace por su cuenta. Forzar Strategy aquí exigiría
que algún componente externo vigile constantemente el estado de la solicitud
para inyectar el comportamiento correcto en cada operación, una vigilancia
innecesaria cuando la propia solicitud ya sabe en qué estado está.

### Reflexión — otros tres patrones (no rubricada)

ComprasUDES también planea tres mejoras que no forman parte de este laboratorio:

1. **Reporte secuencial de solicitudes por centro de costo.** Recorrer todas
   las solicitudes de un centro de costo sin exponer si están almacenadas en
   una lista, un mapa o cualquier otra estructura es el problema que resuelve
   **Iterator**: expondría un recorrido uniforme sobre la colección interna sin
   revelar su representación.

2. **Comprobantes con el mismo esqueleto de impresión.** Orden de compra,
   comprobante de reserva presupuestal y acta de rechazo comparten encabezado,
   cuerpo y pie, y solo difieren en cómo llenan el cuerpo: es el caso de libro
   de **Template Method**, con un método plantilla fijo en una clase base y un
   único paso variable que cada comprobante concreto sobrescribe.

3. **Instantáneas completas del estado de una solicitud.** Guardar y restaurar
   el estado íntegro de una solicitud en cualquier punto de su historia, sin
   que el código que guarda esas instantáneas conozca los detalles internos de
   `Solicitud`, es el problema de **Memento**. Se diferencia de la Necesidad 2
   en que Command encapsula una acción puntual y su reversión inmediata (deshacer
   la última reserva o la última orden generada), mientras que Memento captura
   una fotografía completa del objeto en un instante dado para restaurarlo tal
   cual estaba, sin importar cuántas acciones hayan ocurrido después.

## Herramientas utilizadas

- Java 17, Spring Boot 3.2, Apache Maven
- JUnit 5
- Visual Studio Code / IntelliJ IDEA, Git, GitHub

## Conclusiones

El reto real de este laboratorio no fue reconocer un patrón por su forma, sino
distinguirlo de su vecino más parecido cuando ambos podían "encajar" a simple
vista: Chain of Responsibility y Command comparten el vocabulario de
"resolver una solicitud", pero uno enruta y el otro revierte; State y Strategy
comparten la misma estructura de interfaz-con-implementaciones, pero difieren
en quién decide el comportamiento activo y si existe una transición real entre
alternativas. Lo más útil fue anclar cada decisión a una pregunta concreta —
¿quién origina el cambio?, ¿existe una secuencia de decisores o un conjunto de
acciones discretas?, ¿el objeto decide su propio comportamiento o alguien
externo lo elige por él? — en lugar de a la similitud superficial del código.
El resultado es un sistema donde los cuatro patrones conviven sin pisarse:
`ContextoSolicitud` (State) es el único punto donde el estado de una solicitud
cambia, y por eso mismo es también el punto natural donde el mecanismo de
notificación (Observer) se conecta a las decisiones ya tomadas por la cadena
de aprobación (Chain of Responsibility) y por el ejecutor de operaciones
(Command), sin que ninguno de estos tres necesite conocer a los demás
directamente.

