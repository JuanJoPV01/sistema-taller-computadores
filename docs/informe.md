# Informe de desarrollo - Actividad de Aprendizaje Unidad 2

**Asignatura:** Estructuras de Datos
**Programa:** Ingeniería de Software
**Universidad:** Universidad de Cartagena
**Tutor:** John Carlos Arrieta Arrieta
**Estudiante:** Juan José Puello Valdez
**Código:** 7502510043
**Fecha de entrega:** 14 de noviembre de 2026
**Caso asignado:** 19 - Taller de computadores
**Entidad principal:** EquipoReparacion
**Identificador:** código de servicio

---

## 1. Logros de aprendizaje

### 1.1 Qué aprendí sobre estructuras de datos

Aprendí que una estructura de datos no es solo una forma de guardar
información, sino una decisión de diseño que condiciona las operaciones
posibles y el costo de cada una. En esta actividad comprobé que la misma
entidad `EquipoReparacion` puede vivir en tres estructuras distintas con
propósitos diferentes: la lista conserva el registro completo, la cola
ordena la atención por llegada y la pila recuerda el último trabajo
realizado para poder deshacerlo.

También entendí que la estructura correcta se elige por la operación
dominante. Para atender equipos en orden de llegada, una lista con
eliminación en el centro sería innecesariamente compleja y rompería la
política FIFO; para deshacer, una lista obligaría a buscar el último
elemento procesado, mientras que la pila lo entrega en O(1). La cola y la
pila no son versiones "más simples" de la lista: son estructuras con
reglas de acceso más estrictas que simplifican el razonamiento y evitan
errores.

### 1.2 Diferencias entre listas, colas y pilas

| Característica | Lista | Cola | Pila |
|---|---|---|---|
| Política de acceso | Acceso por posición y por igualdad | FIFO: primero en entrar, primero en salir | LIFO: último en entrar, primero en salir |
| Inserción | Al inicio, al final o en una posición | Solo al final | Solo en la cima |
| Eliminación | En cualquier posición | Solo al frente | Solo en la cima |
| Recorrido | Hacia adelante y hacia atrás | Del frente al final | De la cima al fondo |
| Uso en el caso 19 | Registro general de equipos | Equipos pendientes de atención | Historial de procesados y deshacer |
| Estructura interna | Doble enlace (`anterior` y `siguiente`) | Enlace simple con `frente` y `fin` | Enlace simple con `cima` |
| Ejemplo cotidiano | Agenda de contactos | Fila de un banco | Pila de platos |

La diferencia esencial está en **dónde se permite operar**. La lista
admite operaciones en cualquier punto; la cola restringe la salida al
frente; la pila restringe la salida a la cima. Esa restricción es una
ventaja: el código de la cola no necesita considerar eliminaciones
intermedias y el de la pila no necesita recorrer para deshacer.

### 1.3 Cómo funcionan internamente con nodos enlazados

Un nodo enlazado es un objeto que contiene un dato y una o más
referencias a otros nodos. En este proyecto, `Nodo` guarda un `Object` y
dos referencias:

```java
public final class Nodo {
    private Object dato;
    private Nodo siguiente;
    private Nodo anterior;
}
```

La lista usa ambas referencias: `siguiente` para avanzar y `anterior`
para retroceder. La cola y la pila usan solo `siguiente`; la referencia
`anterior` queda en `null`. El final de la estructura se representa con
`null`, y por eso todos los recorridos comprueban `actual != null` antes
de leer el dato.

Insertar en la lista no consiste en desplazar elementos como en un
arreglo, sino en cambiar referencias. Por ejemplo, para insertar entre
`anterior` y `siguiente` se actualizan cuatro enlaces:

```java
Nodo nuevo = new Nodo(dato, siguiente, anterior);
anterior.setSiguiente(nuevo);
siguiente.setAnterior(nuevo);
tamanio++;
```

Eliminar es el proceso inverso: se conectan los vecinos entre sí y se
aíslan las referencias del nodo eliminado. En los extremos hay casos
especiales: al eliminar el primero, el nuevo primero debe tener
`anterior = null`; al eliminar el último, el nuevo último debe tener
`siguiente = null`; si la estructura queda vacía, también se anulan
`primero` y `ultimo` (o `frente` y `fin`, o `cima`).

En la lista doblemente enlazada, las referencias "izquierda/derecha" del
modelo mental se materializan como `anterior` y `siguiente`. Esa doble
referencia es la que permite `mostrarAtras()` sin invertir la lista y la
que hace que `agregar` al final y `eliminarUltimo` sean O(1) gracias a
`ultimo`.

### 1.4 Dificultades encontradas

La primera dificultad fue **cancelar un equipo que no está al frente de
la cola**. La regla del ejercicio prohíbe eliminar directamente del
centro, así que la solución fue desencolar todo, enviar a una cola
auxiliar los equipos que no coinciden y devolverlos a la cola original en
el mismo orden. El detalle delicado fue no perder el orden relativo y no
marcar como cancelado un equipo que no se hubiera encontrado. Se resolvió
con una bandera `encontrado` y una validación posterior.

La segunda dificultad fue **mantener consistentes los estados** al
procesar y deshacer. Un equipo procesado está en la lista y en el
historial, pero ya no en la cola; un equipo cancelado está en la lista,
pero no en la cola ni en el historial; un equipo deshecho vuelve a la
cola y sale del historial. Para no generar combinaciones imposibles se
usó el `enum Estado` con tres valores y se validó la transición antes de
modificar las estructuras.

La tercera dificultad fue **manipular los enlaces sin perder nodos**,
sobre todo al eliminar el primero, el último o el único elemento. Un
orden incorrecto de asignaciones puede dejar `primero` apuntando a un
nodo que ya no pertenece a la lista o dejar `ultimo` con un `siguiente`
que debería ser `null`. La solución fue separar los casos extremos en
métodos (`eliminarPrimero`, `eliminarUltimo`, `eliminarEnPosicion`) y
probar cada uno con listas de cero, uno y varios elementos.

También apareció una dificultad de **codificación de caracteres**: los
nombres con tildes se veían mal en la consola de Windows. Se corrigió
compilando con `-encoding UTF-8`, ejecutando con
`-Dfile.encoding=UTF-8` y configurando `[Console]::OutputEncoding` en los
scripts de PowerShell.

Finalmente, **probar sin JUnit** obligó a diseñar un pequeño arnés de
pruebas con contador de verificaciones. Fue una dificultad y a la vez un
aprendizaje: las operaciones del sistema se separaron de la impresión
para poder invocarlas directamente y comprobar sus resultados.

### 1.5 Conceptos de programación orientada a objetos aplicados

- **Encapsulamiento:** los atributos de `Nodo`, `Lista`, `Cola`, `Pila` y
  `EquipoReparacion` son privados; el exterior solo interactúa mediante
  métodos públicos. La lista no expone sus nodos, solo datos y textos.
- **Abstracción:** `Lista`, `Cola` y `Pila` ofrecen una interfaz pequeña
  (agregar, eliminar, consultar, recorrer) y ocultan la manipulación de
  referencias.
- **Composición:** `SistemaDelCaso` contiene una lista, una cola y una
  pila; las tres contienen nodos. La entidad `EquipoReparacion` es el
  dato que fluye entre ellas.
- **Polimorfismo:** las estructuras almacenan `Object` y se apoyan en
  `equals()` y `toString()` del objeto concreto. Al llamar
  `Objects.equals(...)`, Java despacha al `equals()` de
  `EquipoReparacion`, que compara por código.
- **Sobrecarga de constructores:** `EquipoReparacion` tiene un
  constructor sin estado (asume `PENDIENTE`) y otro con estado explícito.
- **Sobrescritura:** se sobrescriben `toString()`, `equals()` y
  `hashCode()` en la entidad.
- **Tipos enumerados:** `Estado` limita los valores posibles y evita
  cadenas mágicas como "pendiente" o "reparado".
- **Clases finales y métodos estáticos auxiliares:** `Nodo`, `Lista`,
  `Cola`, `Pila` y `EquipoReparacion` son `final`; las validaciones
  internas son `private static` porque no dependen del estado.

### 1.6 Conocimientos de lógica de programación fortalecidos

Esta actividad fortaleció la lógica de **manipulación de referencias**:
asignar `siguiente` y `anterior` en el orden correcto, actualizar los
extremos y aislar nodos eliminados. También reforzó el razonamiento con
**casos borde**: estructura vacía, un solo elemento, inserción al inicio,
inserción al final, eliminación del único nodo, búsqueda sin resultado y
cancelación de un equipo inexistente.

Otro aprendizaje fue el uso de **invariantes**. En la lista, por ejemplo,
`tamanio` debe coincidir con la cantidad de nodos; `primero.anterior`
debe ser `null`; `ultimo.siguiente` debe ser `null`; y
`primero == null` si y solo si `ultimo == null`. En la cola,
`frente == null` implica `fin == null`. En la pila, `cima` siempre es el
último nodo apilado. Las pruebas verifican esas invariantes indirectamente
al comprobar tamaños, recorridos y excepciones.

Finalmente, se trabajó la **separación de responsabilidades**: la entidad
valida sus datos, las estructuras administran nodos, el sistema aplica las
reglas del negocio y el menú se ocupa de la entrada y la salida. Esa
división hace que cada método tenga una razón clara para cambiar.

### 1.7 Validaciones, manejo de errores y modularización

Las validaciones se aplicaron en tres niveles:

1. **Entidad:** `validarTexto` rechaza `null`, cadenas vacías y cadenas
   con solo espacios; el estado no puede ser `null`; el código se
   normaliza a mayúsculas.
2. **Estructuras:** no se permiten datos `null`; los índices se validan
   contra `0..tamanio-1` (o `0..tamanio` al insertar); eliminar o
   desapilar de una estructura vacía lanza `NoSuchElementException`.
3. **Sistema:** no se registran códigos duplicados; no se cancela un
   equipo que no esté pendiente; no se busca un código inexistente; el
   menú captura las excepciones y muestra un mensaje sin terminar la
   aplicación.

Se usaron excepciones específicas en lugar de códigos de error:
`IllegalArgumentException` para datos inválidos, `IndexOutOfBoundsException`
para posiciones fuera de rango, `IllegalStateException` para
transiciones no permitidas y `NoSuchElementException` para estructuras
vacías o búsquedas sin resultado.

La modularización se refleja en métodos pequeños y con una sola tarea:
`agregar`, `agregarAlInicio`, `agregarEnPosicion`, `eliminarPrimero`,
`eliminarUltimo`, `eliminarEnPosicion`, `buscarDato`, `contiene`,
`limpiar`, `mostrarAdelante`, `mostrarAtras` en la lista; `encolar`,
`desencolar`, `peek`, `tamanio`, `esVacia`, `contiene`, `limpiar`,
`mostrar` en la cola; `apilar`, `desapilar`, `peek`, `buscar`, `contiene`,
`limpiar`, `mostrar` en la pila. En el sistema, cada opción del menú tiene
su propio método y las reglas del negocio están separadas de la lectura
de datos.

### 1.8 Qué aprendí sobre el flujo de datos dentro de la aplicación

El flujo comienza en el `Scanner`, que entrega texto al menú. El menú
valida y construye un `EquipoReparacion`; el sistema verifica que el
código no exista, fuerza el estado `PENDIENTE` y entrega **la misma
referencia** a la lista y a la cola. Cuando se procesa, la cola entrega el
frente, el sistema cambia el estado a `REPARADO` y apila esa referencia en
el historial. Cuando se deshace, la pila entrega la cima, el sistema
cambia el estado a `PENDIENTE` y la encola al final. Cuando se cancela, la
cola auxiliar permite retirar un elemento intermedio sin romper el orden
y el equipo queda en la lista con estado `CANCELADO`.

Entendí que el dato no se copia entre estructuras: se comparte la
referencia. Eso ahorra memoria y mantiene la coherencia, pero exige
cuidado: si se modificara el objeto desde un lugar, el cambio se vería en
los demás. En este sistema eso es deseable, porque el estado del equipo es
único. La lista actúa como fuente de verdad del registro; la cola y la
pila son vistas especializadas del mismo conjunto de objetos.

### 1.9 Qué aprendí sobre Git y control de versiones

Aprendí a construir un repositorio de forma progresiva: primero la
estructura base, luego el nodo, después cada estructura, la entidad, el
sistema, las pruebas y la documentación. Cada commit tiene un mensaje
descriptivo con un prefijo (`chore`, `feat`, `test`, `docs`) que indica
el tipo de cambio. También comprobé la importancia de compilar y ejecutar
antes de confirmar, y de subir cada avance al repositorio remoto para que
el historial evidencie el proceso real.

El repositorio remoto es público y contiene el historial completo, no un
único commit con todo el proyecto. Ese historial permite reconstruir el
orden en que se tomaron las decisiones y demuestra que el código se
desarrolló por etapas.

---

## 2. Informe de desarrollo

### 2.1 Descripción funcional del sistema

El sistema administra las reparaciones de un taller de computadores. Cada
equipo que llega se registra con un código de servicio único, el nombre
del cliente, el tipo de equipo y la falla reportada. El equipo entra a una
cola de pendientes y se atiende en orden de llegada. Al procesarlo, pasa a
un historial de procesados. El sistema también permite consultar el
registro completo, buscar por código, cancelar un equipo pendiente,
deshacer el último procesamiento y consultar cantidades.

El contexto del ejercicio es el caso 19 de la actividad: un taller recibe
equipos de distintos clientes y necesita organizar la atención sin perder
trazabilidad. La entidad principal es `EquipoReparacion` y su
identificador es el código de servicio, que se usa para detectar
duplicados y para buscar.

Las operaciones que permite el sistema son:

1. Registrar un equipo y encolarlo.
2. Ver todos los equipos registrados, hacia adelante y hacia atrás.
3. Ver los equipos pendientes en orden FIFO.
4. Procesar el siguiente equipo pendiente.
5. Ver el historial de procesados en orden LIFO.
6. Buscar un equipo por su código.
7. Cancelar un equipo pendiente usando una cola auxiliar.
8. Deshacer el último procesamiento.
9. Ver cantidades de registrados, pendientes, procesados y cancelados.
10. Salir.

El usuario interactúa mediante un menú numérico de consola. El sistema
valida las entradas, muestra mensajes claros cuando algo falla y mantiene
la aplicación activa hasta que se elige la opción 10. También existe el
modo `--demo`, que ejecuta un recorrido automático con cinco equipos para
generar evidencia reproducible.

### 2.2 Componentes desarrollados

| Clase | Responsabilidad | Relación con las demás |
|---|---|---|
| `Nodo` | Guardar un dato y las referencias `siguiente` y `anterior` | Es la unidad de almacenamiento de `Lista`, `Cola` y `Pila` |
| `Lista` | Registro general doblemente enlazado | Contiene nodos y es usada por `SistemaDelCaso` |
| `Cola` | Administrar pendientes con política FIFO | Contiene nodos y es usada por `SistemaDelCaso` |
| `Pila` | Guardar el historial con política LIFO | Contiene nodos y es usada por `SistemaDelCaso` |
| `EquipoReparacion` | Modelar el equipo, su cliente, falla y estado | Es el dato almacenado en las tres estructuras |
| `SistemaDelCaso` | Aplicar las reglas, coordinar las estructuras y presentar el menú | Compone una lista, una cola y una pila |

La relación es de **composición**: el sistema contiene las tres
estructuras; las estructuras contienen nodos; los nodos contienen
referencias a otros nodos y a un `EquipoReparacion`. La entidad no conoce
las estructuras, y las estructuras no conocen el menú. Esa dirección de
dependencias mantiene bajo el acoplamiento.

El flujo de información se puede resumir así:

```text
Usuario -> SistemaDelCaso -> Lista / Cola / Pila -> Nodo -> EquipoReparacion
```

La lista general es el registro completo. La cola de pendientes es una
vista ordenada de los equipos que aún no se atienden. La pila de historial
es una vista ordenada de los equipos ya procesados. Las tres comparten los
mismos objetos, no copias.

### 2.3 Explicación de las estructuras de datos usadas

#### 2.3.1 Lista doblemente enlazada

La lista mantiene `primero`, `ultimo` y `tamanio`. `agregar` inserta al
final en O(1) usando `ultimo`; `agregarAlInicio` inserta al comienzo en
O(1); `agregarEnPosicion` recorre hasta el índice y conecta el nuevo nodo
entre sus vecinos. `eliminarPrimero`, `eliminarUltimo` y
`eliminarEnPosicion` actualizan los enlaces y aíslan el nodo eliminado.
`buscarDato(int)` devuelve el dato de una posición y `buscarDato(Object)`
recorre comparando con `equals`. `contiene` reutiliza esa búsqueda.
`cuentaElementos` devuelve `tamanio`, `limpiar` rompe los enlaces y
`mostrarAdelante`/`mostrarAtras` recorren en ambos sentidos.

Los métodos implementados son exactamente los solicitados:

```text
agregar(Object dato)
agregarAlInicio(Object dato)
agregarEnPosicion(int indice, Object dato)
eliminarPrimero()
eliminarUltimo()
eliminarEnPosicion(int indice)
buscarDato(int indice)
buscarDato(Object dato)
contiene(Object dato)
cuentaElementos()
limpiar()
mostrarAdelante()
mostrarAtras()
```

La lista resuelve el requerimiento de **registro completo**: todos los
equipos permanecen en ella aunque ya se hayan procesado o cancelado. Es
la fuente de verdad para buscar y para los reportes. Su recorrido inverso
demuestra el uso de la referencia `anterior`.

#### 2.3.2 Cola FIFO

La cola mantiene `frente`, `fin` y `tamanio`. `encolar` agrega al final:
si está vacía, el nuevo nodo es frente y fin; si no, el antiguo fin apunta
al nuevo y `fin` se actualiza. `desencolar` extrae el frente, avanza
`frente` al siguiente y, si la cola queda vacía, pone `fin` en `null`.
`peek` consulta el frente sin eliminarlo; `tamanio` y `esVacia` informan
el estado; `contiene` recorre desde el frente; `limpiar` rompe los enlaces;
`mostrar` recorre del frente al final.

```text
encolar(Object dato)
desencolar()
peek()
tamanio()
esVacia()
contiene(Object dato)
limpiar()
mostrar()
```

La cola resuelve la **atención en orden de llegada**. El primer equipo
registrado es el primero que se procesa, y el menú muestra ese orden. La
cola no permite eliminar del centro; para cancelar se usa una cola
auxiliar. Esa restricción es la que garantiza que la política FIFO no se
rompa por accidente.

#### 2.3.3 Pila LIFO

La pila mantiene solo `cima` y `tamanio`. `apilar` crea un nodo cuyo
`siguiente` es la cima anterior y lo convierte en la nueva cima.
`desapilar` extrae la cima, avanza al siguiente y aísla el nodo.
`peek` consulta la cima; `tamanio` y `esVacia` informan el estado;
`contiene` y `buscar` recorren desde la cima; `limpiar` rompe los enlaces;
`mostrar` presenta la cima primero.

```text
apilar(Object dato)
desapilar()
peek()
tamanio()
esVacia()
contiene(Object dato)
limpiar()
mostrar()
buscar(Object dato)
```

La pila resuelve el **historial y el deshacer**. El último equipo
procesado queda en la cima; `deshacerUltimoProcesamiento` lo desapila, lo
marca como pendiente y lo vuelve a encolar. No hace falta buscar el último
procesado: la estructura ya lo tiene en la cima.

### 2.4 Explicación del flujo del sistema paso a paso

1. **Registro de un elemento.** El menú pide código, cliente, tipo y
   falla. `EquipoReparacion` valida y normaliza el código. El sistema
   comprueba con `listaGeneral.contiene(equipo)` que no exista; si no
   existe, fuerza `PENDIENTE`, ejecuta `listaGeneral.agregar(equipo)` y
   `colaPendientes.encolar(equipo)`. La lista y la cola reciben la misma
   referencia.
2. **Paso a la cola de pendientes.** El equipo queda al final de la cola.
   Si la cola estaba vacía, también es el frente. El orden de llegada se
   conserva porque `encolar` siempre agrega al final y actualiza `fin`.
3. **Procesamiento.** `procesarSiguiente` ejecuta
   `colaPendientes.desencolar()`, cambia el estado del equipo a `REPARADO`
   y ejecuta `historialProcesados.apilar(equipo)`. El equipo sale de la
   cola, pero sigue en la lista general.
4. **Movimiento al historial.** La pila recibe el equipo en la cima. El
   historial muestra primero el último procesado. El registro general
   conserva el orden de llegada original, no el orden de procesamiento.
5. **Búsquedas.** `buscarPorCodigo` normaliza el código y recorre la lista
   general por índices, comparando el código de cada equipo. Si no
   encuentra, lanza `NoSuchElementException`. La búsqueda muestra también
   la ubicación lógica del equipo: cola, historial o lista (cancelado).
6. **Cancelaciones.** `cancelarPendiente` verifica que el equipo exista y
   esté `PENDIENTE`. Luego desencola todo: los equipos distintos al
   buscado pasan a una cola auxiliar y después regresan a la cola original
   en el mismo orden; el equipo buscado se descarta de la cola y se marca
   `CANCELADO` en la lista. No se elimina del registro general.
7. **Deshacer una operación.** `deshacerUltimoProcesamiento` ejecuta
   `historialProcesados.desapilar()`, cambia el estado a `PENDIENTE` y
   ejecuta `colaPendientes.encolar(equipo)`. Por la regla de la cola, el
   equipo vuelve al final, no a su posición original. La lista general no
   se modifica.

### 2.5 Explicación técnica del código

**Nodos enlazados.** `Nodo` es una clase final con `dato`, `siguiente` y
`anterior`. No contiene lógica de estructura: solo almacena y enlaza. La
lista usa el doble enlace; la cola y la pila usan `siguiente` y dejan
`anterior` en `null`. Esa reutilización evita duplicar una clase de nodo
para cada estructura.

**Recorridos.** Todos los recorridos siguen el mismo patrón:

```java
Nodo actual = primero;
while (actual != null) {
    // leer actual.getDato()
    actual = actual.getSiguiente();
}
```

El recorrido inverso de la lista parte de `ultimo` y avanza con
`getAnterior()`. La cola parte de `frente` y la pila de `cima`; ambos usan
`getSiguiente()`. La condición `actual != null` es la que detecta el final
de la estructura.

**Manipulación de referencias.** En la lista, insertar en una posición
intermedia actualiza cuatro enlaces; eliminar actualiza los enlaces de los
vecinos y anula los del nodo eliminado. En los extremos se actualizan
`primero`/`ultimo` y se pone `anterior`/`siguiente` en `null` cuando
corresponde. En la cola, `encolar` actualiza `fin` y `desencolar` actualiza
`frente` (y `fin` si queda vacía). En la pila, `apilar` y `desapilar`
trabajan únicamente con `cima`.

**FIFO.** La cola encola al final y desencola del frente. La invariante es
que `frente` es el equipo más antiguo y `fin` el más reciente. El menú
numera desde 1 el frente, de modo que el usuario ve el próximo equipo a
atender.

**LIFO.** La pila apila en la cima y desapila de la cima. La invariante es
que `cima` es el último equipo procesado. `deshacer` siempre toma la cima,
por lo que respeta el orden inverso al procesamiento.

**Decisiones tomadas.** Se eligió `Object` porque las firmas del taller
lo exigen; los casts a `EquipoReparacion` se concentran en
`SistemaDelCaso`. Se eligió que `mostrarAdelante`, `mostrarAtras`,
`mostrar` y `listar...` devolvieran `String` en lugar de imprimir, para
que el menú controle la presentación y las pruebas puedan verificar el
contenido sin capturar la consola. Se eligió `enum Estado` para evitar
estados inválidos y `equals` por código para que la identidad del negocio
coincida con la identidad del objeto. Se eligió una cola auxiliar para
cancelar porque la regla prohíbe eliminar del centro de la cola.

**Excepciones.** `desencolar`, `desapilar`, `eliminarPrimero` y
`eliminarUltimo` lanzan `NoSuchElementException` si la estructura está
vacía. Los índices fuera de rango lanzan `IndexOutOfBoundsException`. Los
datos nulos o vacíos lanzan `IllegalArgumentException`. Las transiciones
no permitidas (cancelar un equipo procesado, por ejemplo) lanzan
`IllegalStateException`. El menú captura esas excepciones y muestra
`ERROR: ...` sin cerrar la aplicación.

### 2.6 Pruebas y resultados

`probar.ps1` compila todo el código y ejecuta `PruebasTaller`, un arnés sin
dependencias externas. Las 130 verificaciones cubren:

| Grupo | Qué se verifica |
|---|---|
| Lista | Inserción al inicio, al final y en posición; eliminación en los tres casos; búsqueda por índice y por dato; recorridos en ambos sentidos; tamaño; limpieza; índices inválidos; datos nulos |
| Cola | Orden FIFO; frente y fin; `peek`; tamaño; búsqueda; recorrido; vaciado; excepción al desencolar vacía; dato nulo |
| Pila | Orden LIFO; cima; `peek`; tamaño; búsqueda; recorrido; vaciado; excepción al desapilar vacía; dato nulo |
| Entidad | Atributos y getters/setters; estado inicial; normalización del código; `equals` por código; `hashCode`; `toString`; validaciones |
| Sistema | Registro en lista y cola; rechazo de duplicados; búsqueda; procesamiento FIFO; historial LIFO; deshacer; cancelación intermedia con cola auxiliar; conteos; estructuras vacías |

Resultado de la ejecución:

```text
OK: 130 pruebas ejecutadas correctamente.
```

Además, el modo `--demo` recorre el sistema completo con cinco equipos:
registra, muestra la lista en ambos sentidos, muestra la cola, rechaza un
duplicado, procesa dos equipos, muestra el historial, busca, cancela un
equipo intermedio, deshace el último procesamiento y presenta las
cantidades finales. La salida completa está en el Anexo A.

### 2.7 Análisis de complejidad

| Operación | Estructura | Complejidad |
|---|---|---|
| Agregar al final | Lista con `ultimo` | O(1) |
| Agregar al inicio | Lista | O(1) |
| Insertar o eliminar en posición | Lista | O(n) por el recorrido |
| Buscar por índice | Lista | O(n) en el peor caso |
| Buscar por dato | Lista | O(n) |
| Encolar | Cola | O(1) |
| Desencolar | Cola | O(1) |
| Apilar | Pila | O(1) |
| Desapilar | Pila | O(1) |
| Cancelar un pendiente | Cola con auxiliar | O(n) |
| Deshacer | Pila | O(1) |

El espacio es O(n) en las tres estructuras. La lista usa dos referencias
por nodo; la cola y la pila usan una. La cancelación con cola auxiliar
usa O(n) espacio adicional temporal, pero conserva el orden y respeta la
regla de no eliminar del centro.

### 2.8 Conclusiones

- La lista, la cola y la pila resolvieron necesidades distintas del mismo
  caso sin duplicar la entidad: registro, orden de atención e historial.
- La implementación con nodos permitió entender que insertar y eliminar
  son operaciones de referencias, no de desplazamiento de elementos.
- La cola auxiliar fue la solución correcta para cancelar un elemento
  intermedio sin violar FIFO y sin perder el orden de los demás.
- La pila simplificó el deshacer a dos operaciones O(1): desapilar y
  encolar.
- Las validaciones y excepciones específicas hicieron que el menú pudiera
  fallar de forma controlada y comprensible.
- Las 130 pruebas y la demostración automática dan evidencia reproducible
  del funcionamiento y de los casos borde.
- El historial de Git muestra la construcción progresiva del sistema y
  permite revisar cada decisión en su contexto.

---

## 3. Enlace del repositorio GitHub

Repositorio público del proyecto:

<https://github.com/JuanJoPV01/sistema-taller-computadores>

El repositorio contiene el código fuente, las pruebas, los scripts, el
informe y las evidencias. El historial tiene commits progresivos con
mensajes descriptivos; no se trata de un único commit con todo el
proyecto.

## 4. Enlace del video de sustentación

- **Video:** pendiente de publicar.
- **Contenido previsto:** demostración del registro, la lista en ambos
  sentidos, la cola FIFO, el procesamiento, la búsqueda, la cancelación
  con cola auxiliar, el deshacer con pila, las validaciones y una
  explicación del código de los nodos y de los recorridos.

> Antes de la entrega se debe reemplazar este apartado por la URL pública
> del video y actualizar el PDF y el README.

## 5. Anexo A. Evidencia de ejecución

La evidencia completa de la demostración y de las pruebas se encuentra en
`docs/entregables/evidencia-ejecucion.txt` y se reproduce en las páginas
siguientes.
