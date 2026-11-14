# Sistema de gestión de reparaciones - Taller de computadores

Solución de la **Actividad de Aprendizaje de la Unidad 2** de
**Estructuras de Datos**: una aplicación de consola en Java que administra
los equipos recibidos en un taller de computadores mediante una **lista
doblemente enlazada**, una **cola FIFO** y una **pila LIFO**,
implementadas desde cero con nodos enlazados.

- **Caso:** 19 - Taller de computadores
- **Entidad principal:** `EquipoReparacion`
- **Identificador:** código de servicio
- **Programa:** Ingeniería de Software
- **Asignatura:** Estructuras de Datos
- **Tutor:** John Carlos Arrieta Arrieta
- **Universidad:** Universidad de Cartagena
- **Estudiante:** Juan José Puello Valdez
- **Código:** 7502510043

## Repositorio

- **GitHub:** <https://github.com/JuanJoPV01/sistema-taller-computadores>

## ¿Qué problema resuelve?

El taller recibe equipos de clientes. Cada equipo debe registrarse una
sola vez, esperar su turno de atención, procesarse en el orden en que
llegó y quedar en un historial que permita deshacer el último trabajo
realizado. Además, el personal necesita consultar el registro completo,
buscar un equipo por su código y cancelar un equipo que aún no ha sido
atendido.

El sistema responde a esas necesidades con tres estructuras y una regla
clara para cada una:

| Estructura | Uso obligatorio en el caso | Operaciones principales |
|---|---|---|
| **Lista** | Registro general de todos los equipos | agregar en cualquier posición, recorrer en ambos sentidos, buscar, eliminar, contar |
| **Cola** | Equipos pendientes por atender, en orden de llegada (FIFO) | encolar, desencolar, consultar el frente, mostrar |
| **Pila** | Historial de equipos procesados y deshacer (LIFO) | apilar, desapilar, consultar la cima, buscar, mostrar |

El flujo central es:

```text
registrar -> listaGeneral.agregar(equipo) + colaPendientes.encolar(equipo)
procesar  -> colaPendientes.desencolar() + historialProcesados.apilar(equipo)
deshacer  -> historialProcesados.desapilar() + colaPendientes.encolar(equipo)
cancelar  -> cola auxiliar que conserva el orden de los demás pendientes
```

Las tres estructuras guardan **referencias al mismo objeto**
`EquipoReparacion`; los cambios de estado se reflejan en todas.

## Menú del sistema

```text
1. Registrar elemento
2. Ver todos los elementos registrados
3. Ver elementos pendientes
4. Procesar siguiente elemento
5. Ver historial de elementos procesados
6. Buscar elemento por código
7. Cancelar elemento pendiente
8. Deshacer último procesamiento
9. Ver cantidad de elementos
10. Salir
```

La opción 2 muestra el registro hacia adelante y hacia atrás, lo que
evidencia el enlace doble de la lista. La opción 9 informa cuántos
equipos están registrados, pendientes, procesados y cancelados.

## Estructura del proyecto

```text
sistema-taller-computadores/
├── src/
│   ├── main/java/edu/unicartagena/taller/
│   │   ├── Nodo.java                 # Nodo enlazado reutilizable
│   │   ├── Lista.java                # Lista doblemente enlazada
│   │   ├── Cola.java                 # Cola FIFO
│   │   ├── Pila.java                 # Pila LIFO
│   │   ├── EquipoReparacion.java     # Entidad del caso 19
│   │   └── SistemaDelCaso.java       # Menú, reglas y demostración
│   └── test/java/edu/unicartagena/taller/
│       └── PruebasTaller.java        # 130 verificaciones sin JUnit
├── docs/
│   ├── informe.md                    # Informe de desarrollo
│   ├── generar_informe.py            # Generador del PDF
│   ├── generar_evidencia.py          # Captura UTF-8 de la ejecución
│   └── entregables/                  # Evidencias y código comprimido
├── ejecutar.ps1                      # Compila y ejecuta el menú
├── probar.ps1                        # Compila y ejecuta las pruebas
├── generar-entregables.ps1           # Regenera evidencias, PDF y ZIP
├── .gitignore
└── README.md
```

## Requisitos

- **JDK 17** o superior (`javac` y `java` en el `PATH`).
- PowerShell 5.1 o superior en Windows.
- Python 3 con `reportlab` solo si se desea regenerar el PDF.

## Cómo ejecutar

### Menú interactivo

```powershell
.\ejecutar.ps1
```

### Demostración automática (evidencia)

```powershell
.\ejecutar.ps1 --demo
```

### Pruebas

```powershell
.\probar.ps1
```

Resultado esperado:

```text
OK: 130 pruebas ejecutadas correctamente.
```

Si PowerShell bloquea los scripts:

```powershell
powershell -ExecutionPolicy Bypass -File .\ejecutar.ps1 --demo
powershell -ExecutionPolicy Bypass -File .\probar.ps1
```

### Compilación manual

```powershell
javac -encoding UTF-8 -d out/main (Get-ChildItem -Recurse src/main/java/*.java).FullName
java -Dfile.encoding=UTF-8 -cp out/main edu.unicartagena.taller.SistemaDelCaso
```

## Ejemplo de ejecución

Fragmento de `.\ejecutar.ps1 --demo`:

```text
=== DEMOSTRACIÓN AUTOMÁTICA - CASO 19 ===

--- 6. PROCESAMIENTO DE DOS EQUIPOS ---
Procesado: EQ-001 | Cliente: Ana Torres | Equipo: Portátil Lenovo ThinkPad | Falla: No enciende | Estado: REPARADO
Procesado: EQ-002 | Cliente: Carlos Pérez | Equipo: Impresora HP LaserJet | Falla: Atasco de papel | Estado: REPARADO
Pendientes: 3 | Procesados: 2

--- 7. HISTORIAL DE PROCESADOS (LIFO, CIMA PRIMERO) ---
[1] EQ-002 | Cliente: Carlos Pérez | Equipo: Impresora HP LaserJet | Falla: Atasco de papel | Estado: REPARADO
[2] EQ-001 | Cliente: Ana Torres | Equipo: Portátil Lenovo ThinkPad | Falla: No enciende | Estado: REPARADO

--- 9. CANCELACIÓN DEL EQUIPO EQ-004 CON COLA AUXILIAR ---
Cancelado: EQ-004 | Cliente: Luis Ramírez | Equipo: MacBook Air | Falla: Batería no carga | Estado: CANCELADO
Pendientes después de cancelar:
[1] EQ-003 | Cliente: María Gómez | Equipo: PC de escritorio | Falla: Pantalla azul al iniciar | Estado: PENDIENTE
[2] EQ-005 | Cliente: Sofía Herrera | Equipo: Monitor Samsung | Falla: Sin señal HDMI | Estado: PENDIENTE
```

La evidencia completa está en
[`docs/entregables/evidencia-ejecucion.txt`](docs/entregables/evidencia-ejecucion.txt).

## Decisiones de diseño

- **`Object` en las estructuras:** las firmas exigidas por la actividad
  reciben `Object`. El sistema solo almacena `EquipoReparacion`, por lo
  que los casts son seguros y están concentrados en `SistemaDelCaso`.
- **Lista doblemente enlazada:** `mostrarAtras()` necesita la referencia
  `anterior`; además, `agregar` y `eliminarUltimo` son O(1) gracias a
  `ultimo`.
- **Cola con referencia `fin`:** encolar es O(1) sin recorrer la cola.
- **Pila con una sola referencia:** todas las operaciones ocurren en la
  cima, por lo que no hace falta un nodo centinela ni doble enlace.
- **`equals()` por código de servicio:** es el identificador del negocio;
  permite detectar duplicados y buscar sin confundir equipos del mismo
  cliente o modelo.
- **Estado con `enum`:** evita valores inválidos y documenta las
  transiciones `PENDIENTE -> REPARADO -> PENDIENTE` y
  `PENDIENTE -> CANCELADO`.
- **Separación lógica/interfaz:** las operaciones del sistema no imprimen;
  el menú y la demostración se encargan de la presentación. Así las
  pruebas no dependen del teclado ni de la consola.
- **Excepciones específicas:** `IllegalArgumentException` para datos
  inválidos, `IllegalStateException` para transiciones no permitidas y
  `NoSuchElementException` para estructuras vacías o búsquedas sin
  resultado. El menú las captura y muestra un mensaje claro.

## Historial de Git

El repositorio se construyó con commits progresivos, uno por cada avance
funcional:

```text
chore: inicializar repositorio y estructura base
feat: implementar nodo enlazado reutilizable
feat: implementar lista doblemente enlazada con recorridos
feat: implementar cola FIFO con nodos enlazados
feat: implementar pila LIFO para el historial
feat: modelar equipo de reparacion del caso 19
feat: implementar sistema y menu con flujo lista-cola-pila
test: agregar 130 verificaciones de estructuras y flujo
docs: documentar sistema y resultados
docs: generar informe PDF y empaquetar entregables
docs: corregir referencia del video y actualizar entregables
actualizacion: quitar referencia pendiente del video
actualizacion: quitar PDF del repositorio
```

Cada commit se subió al repositorio remoto después de compilar y, cuando
correspondía, ejecutar las pruebas.

## Entregables

- [`docs/entregables/evidencia-ejecucion.txt`](docs/entregables/evidencia-ejecucion.txt)
- [`docs/entregables/codigo-fuente.zip`](docs/entregables/codigo-fuente.zip)
- Código fuente en `src/`.
