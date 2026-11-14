package edu.unicartagena.taller;

import java.io.PrintStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Clase de ejecución del caso 19: coordina la lista general, la cola de
 * pendientes y la pila de historial, y expone el menú de consola.
 *
 * <p>La clase separa dos responsabilidades para que la lógica pueda
 * probarse sin depender del teclado:</p>
 * <ul>
 *   <li>Los métodos públicos {@code registrar}, {@code procesarSiguiente},
 *       {@code cancelarPendiente}, {@code deshacerUltimoProcesamiento} y
 *       {@code buscarPorCodigo} contienen las reglas del negocio y no
 *       imprimen nada.</li>
 *   <li>Los métodos privados del menú leen datos, llaman a esas reglas y
 *       presentan el resultado en el {@link PrintStream} recibido.</li>
 * </ul>
 *
 * <p>Las tres estructuras guardan referencias al mismo objeto
 * {@link EquipoReparacion}. No se crean copias: cuando un equipo cambia de
 * estado, el cambio se refleja en la lista, en la cola y en la pila. Esa
 * decisión mantiene consistente el flujo y evita duplicar información.</p>
 */
public final class SistemaDelCaso {

    private final Lista listaGeneral = new Lista();
    private final Cola colaPendientes = new Cola();
    private final Pila historialProcesados = new Pila();
    private final Scanner entrada;
    private final PrintStream salida;

    /**
     * @param entrada lector de datos del usuario
     * @param salida  destino de los mensajes del sistema
     */
    public SistemaDelCaso(Scanner entrada, PrintStream salida) {
        if (entrada == null || salida == null) {
            throw new IllegalArgumentException("La entrada y la salida son obligatorias.");
        }
        this.entrada = entrada;
        this.salida = salida;
    }

    /**
     * Punto de entrada. Sin argumentos inicia el menú interactivo; con el
     * argumento {@code --demo} ejecuta una demostración automática que
     * sirve como evidencia de funcionamiento.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SistemaDelCaso sistema = new SistemaDelCaso(new Scanner(System.in), System.out);
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            sistema.ejecutarDemostracion();
        } else {
            sistema.ejecutarMenu();
        }
    }

    // ------------------------------------------------------------------
    // Reglas del negocio (sin entrada ni salida)
    // ------------------------------------------------------------------

    /**
     * Construye y registra un equipo nuevo.
     *
     * @return equipo registrado
     */
    public EquipoReparacion registrar(String codigo, String cliente,
                                      String tipoEquipo, String fallaReportada) {
        return registrar(new EquipoReparacion(codigo, cliente, tipoEquipo, fallaReportada));
    }

    /**
     * Registra un equipo en la lista general y lo encola para atención.
     *
     * <p>Primero valida que el código no exista. Después fuerza el estado
     * {@code PENDIENTE}, agrega el objeto a la lista y lo encola. La lista
     * conserva el registro histórico; la cola define el orden de atención
     * FIFO.</p>
     *
     * @param equipo equipo que se desea registrar
     * @return el mismo equipo registrado
     * @throws IllegalArgumentException si el equipo es nulo o su código ya existe
     */
    public EquipoReparacion registrar(EquipoReparacion equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo es obligatorio.");
        }
        if (listaGeneral.contiene(equipo)) {
            throw new IllegalArgumentException(
                    "Ya existe un equipo registrado con el código " + equipo.getCodigoServicio() + ".");
        }
        equipo.setEstado(EquipoReparacion.Estado.PENDIENTE);
        listaGeneral.agregar(equipo);
        colaPendientes.encolar(equipo);
        return equipo;
    }

    /**
     * Atiende el siguiente equipo pendiente.
     *
     * <p>Desencola el frente (FIFO), lo marca como {@code REPARADO} y lo
     * apila en el historial. El equipo sigue estando en la lista general,
     * por lo que su registro completo no se pierde.</p>
     *
     * @return equipo procesado
     * @throws NoSuchElementException si no hay equipos pendientes
     */
    public EquipoReparacion procesarSiguiente() {
        EquipoReparacion equipo = (EquipoReparacion) colaPendientes.desencolar();
        equipo.setEstado(EquipoReparacion.Estado.REPARADO);
        historialProcesados.apilar(equipo);
        return equipo;
    }

    /**
     * Deshace el último procesamiento.
     *
     * <p>Desapila la cima del historial, devuelve el equipo al estado
     * {@code PENDIENTE} y lo encola de nuevo. Por la regla de la cola, la
     * reincorporación ocurre al final, no en la posición original.</p>
     *
     * @return equipo devuelto a la cola
     * @throws NoSuchElementException si el historial está vacío
     */
    public EquipoReparacion deshacerUltimoProcesamiento() {
        EquipoReparacion equipo = (EquipoReparacion) historialProcesados.desapilar();
        equipo.setEstado(EquipoReparacion.Estado.PENDIENTE);
        colaPendientes.encolar(equipo);
        return equipo;
    }

    /**
     * Cancela un equipo pendiente usando una cola auxiliar.
     *
     * <p>La cola no permite extraer un nodo del centro. Por eso se
     * desencola todo: los equipos que no coinciden pasan a la cola
     * auxiliar y luego regresan a la cola original en el mismo orden. El
     * equipo buscado se descarta de la cola, pero permanece en la lista
     * general con estado {@code CANCELADO}.</p>
     *
     * @param codigo código de servicio del equipo
     * @return equipo cancelado
     * @throws NoSuchElementException  si el código no existe
     * @throws IllegalStateException   si el equipo no está pendiente
     */
    public EquipoReparacion cancelarPendiente(String codigo) {
        EquipoReparacion equipo = buscarPorCodigo(codigo);
        if (equipo.getEstado() != EquipoReparacion.Estado.PENDIENTE) {
            throw new IllegalStateException(
                    "El equipo " + equipo.getCodigoServicio() + " no está pendiente (estado actual: "
                            + equipo.getEstado() + ").");
        }

        Cola auxiliar = new Cola();
        boolean encontrado = false;
        while (!colaPendientes.esVacia()) {
            EquipoReparacion actual = (EquipoReparacion) colaPendientes.desencolar();
            if (!encontrado && actual.equals(equipo)) {
                encontrado = true;
            } else {
                auxiliar.encolar(actual);
            }
        }
        while (!auxiliar.esVacia()) {
            colaPendientes.encolar(auxiliar.desencolar());
        }
        if (!encontrado) {
            throw new IllegalStateException(
                    "El equipo " + equipo.getCodigoServicio() + " figura pendiente pero no está en la cola.");
        }

        equipo.setEstado(EquipoReparacion.Estado.CANCELADO);
        return equipo;
    }

    /**
     * Busca un equipo por su código en la lista general.
     *
     * <p>El recorrido es secuencial porque la lista enlazada no permite
     * acceso directo por clave. Se compara con el código normalizado de la
     * entidad, que es la clave definida en {@code equals}.</p>
     *
     * @param codigo código de servicio buscado
     * @return equipo encontrado
     * @throws NoSuchElementException si no existe
     */
    public EquipoReparacion buscarPorCodigo(String codigo) {
        String buscado = normalizarCodigo(codigo);
        for (int i = 0; i < listaGeneral.cuentaElementos(); i++) {
            EquipoReparacion actual = (EquipoReparacion) listaGeneral.buscarDato(i);
            if (actual.getCodigoServicio().equals(buscado)) {
                return actual;
            }
        }
        throw new NoSuchElementException("No se encontró un equipo con código " + buscado + ".");
    }

    /**
     * @return cantidad de equipos en la lista general
     */
    public int contarRegistrados() {
        return listaGeneral.cuentaElementos();
    }

    /**
     * @return cantidad de equipos pendientes en la cola
     */
    public int contarPendientes() {
        return colaPendientes.tamanio();
    }

    /**
     * @return cantidad de equipos en el historial
     */
    public int contarProcesados() {
        return historialProcesados.tamanio();
    }

    /**
     * Cuenta los equipos que tienen un estado determinado.
     *
     * @param estado estado consultado
     * @return cantidad de coincidencias en la lista general
     */
    public int contarPorEstado(EquipoReparacion.Estado estado) {
        int total = 0;
        for (int i = 0; i < listaGeneral.cuentaElementos(); i++) {
            EquipoReparacion actual = (EquipoReparacion) listaGeneral.buscarDato(i);
            if (actual.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }

    public String listarTodos() {
        return listaGeneral.mostrarAdelante();
    }

    public String listarTodosEnOrdenInverso() {
        return listaGeneral.mostrarAtras();
    }

    public String listarPendientes() {
        return colaPendientes.mostrar();
    }

    public String listarHistorial() {
        return historialProcesados.mostrar();
    }

    // ------------------------------------------------------------------
    // Menú interactivo
    // ------------------------------------------------------------------

    /**
     * Ejecuta el menú obligatorio de diez opciones hasta que el usuario
     * elige salir.
     */
    public void ejecutarMenu() {
        salida.println("=== TALLER DE COMPUTADORES - GESTIÓN DE REPARACIONES ===");
        salida.println("Caso 19: lista general, cola de pendientes e historial de procesados");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            atenderOpcion(opcion);
        } while (opcion != 10);
        salida.println();
        salida.println("Sesión finalizada. Equipos registrados: " + contarRegistrados() + ".");
    }

    private void mostrarMenu() {
        salida.println();
        salida.println("--- MENÚ PRINCIPAL ---");
        salida.println("1. Registrar elemento");
        salida.println("2. Ver todos los elementos registrados");
        salida.println("3. Ver elementos pendientes");
        salida.println("4. Procesar siguiente elemento");
        salida.println("5. Ver historial de elementos procesados");
        salida.println("6. Buscar elemento por código");
        salida.println("7. Cancelar elemento pendiente");
        salida.println("8. Deshacer último procesamiento");
        salida.println("9. Ver cantidad de elementos");
        salida.println("10. Salir");
    }

    private void atenderOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> registrarDesdeConsola();
                case 2 -> mostrarRegistrados();
                case 3 -> mostrarPendientes();
                case 4 -> procesarDesdeConsola();
                case 5 -> mostrarHistorial();
                case 6 -> buscarDesdeConsola();
                case 7 -> cancelarDesdeConsola();
                case 8 -> deshacerDesdeConsola();
                case 9 -> mostrarCantidades();
                case 10 -> salida.println("Cerrando el sistema...");
                default -> salida.println("Opción no válida.");
            }
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            salida.println("ERROR: " + e.getMessage());
        }
    }

    private void registrarDesdeConsola() {
        salida.println();
        salida.println("--- REGISTRO DE EQUIPO ---");
        String codigo = leerTextoObligatorio("Código de servicio: ");
        String cliente = leerTextoObligatorio("Cliente: ");
        String tipo = leerTextoObligatorio("Tipo de equipo: ");
        String falla = leerTextoObligatorio("Falla reportada: ");
        EquipoReparacion equipo = registrar(codigo, cliente, tipo, falla);
        salida.println("Equipo registrado y encolado: " + equipo);
    }

    private void mostrarRegistrados() {
        salida.println();
        salida.println("--- TODOS LOS EQUIPOS REGISTRADOS ---");
        salida.println(listarTodos());
        salida.println();
        salida.println("--- MISMO REGISTRO EN ORDEN INVERSO (enlaces anteriores) ---");
        salida.println(listarTodosEnOrdenInverso());
    }

    private void mostrarPendientes() {
        salida.println();
        salida.println("--- EQUIPOS PENDIENTES (ORDEN FIFO) ---");
        salida.println(listarPendientes());
    }

    private void procesarDesdeConsola() {
        EquipoReparacion equipo = procesarSiguiente();
        salida.println();
        salida.println("Equipo procesado: " + equipo);
        salida.println("Se apiló en el historial de procesados.");
    }

    private void mostrarHistorial() {
        salida.println();
        salida.println("--- HISTORIAL DE PROCESADOS (CIMA PRIMERO) ---");
        salida.println(listarHistorial());
    }

    private void buscarDesdeConsola() {
        salida.println();
        salida.println("--- BÚSQUEDA POR CÓDIGO ---");
        String codigo = leerTextoObligatorio("Código a buscar: ");
        EquipoReparacion equipo = buscarPorCodigo(codigo);
        salida.println("Encontrado: " + equipo);
        salida.println("Ubicación: " + ubicacion(equipo));
    }

    private void cancelarDesdeConsola() {
        salida.println();
        salida.println("--- CANCELAR EQUIPO PENDIENTE ---");
        String codigo = leerTextoObligatorio("Código del equipo a cancelar: ");
        EquipoReparacion equipo = cancelarPendiente(codigo);
        salida.println("Equipo cancelado: " + equipo);
        salida.println("Se retiró de la cola con una cola auxiliar y permanece en la lista general.");
    }

    private void deshacerDesdeConsola() {
        EquipoReparacion equipo = deshacerUltimoProcesamiento();
        salida.println();
        salida.println("Se deshizo el último procesamiento: " + equipo);
        salida.println("El equipo volvió al final de la cola de pendientes.");
    }

    private void mostrarCantidades() {
        salida.println();
        salida.println("--- CANTIDAD DE ELEMENTOS ---");
        salida.println("Registrados en la lista general : " + contarRegistrados());
        salida.println("Pendientes en la cola          : " + contarPendientes());
        salida.println("Procesados en el historial      : " + contarProcesados());
        salida.println("Cancelados en la lista          : "
                + contarPorEstado(EquipoReparacion.Estado.CANCELADO));
    }

    /**
     * Traduce el estado del equipo a una ubicación comprensible para el
     * usuario.
     */
    private String ubicacion(EquipoReparacion equipo) {
        return switch (equipo.getEstado()) {
            case PENDIENTE -> "cola de pendientes";
            case REPARADO -> "historial de procesados";
            case CANCELADO -> "lista general (cancelado, fuera de la cola)";
        };
    }

    // ------------------------------------------------------------------
    // Entrada de datos
    // ------------------------------------------------------------------

    private int leerOpcion() {
        while (true) {
            salida.print("Seleccione una opción (1-10): ");
            String linea = leerLinea();
            try {
                int opcion = Integer.parseInt(linea.trim());
                if (opcion >= 1 && opcion <= 10) {
                    return opcion;
                }
                salida.println("La opción debe estar entre 1 y 10.");
            } catch (NumberFormatException e) {
                salida.println("Debe ingresar un número entero.");
            }
        }
    }

    private String leerTextoObligatorio(String mensaje) {
        while (true) {
            salida.print(mensaje);
            String valor = leerLinea().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            salida.println("El dato es obligatorio; intente de nuevo.");
        }
    }

    private String leerLinea() {
        if (!entrada.hasNextLine()) {
            throw new NoSuchElementException("No hay más datos de entrada.");
        }
        return entrada.nextLine();
    }

    // ------------------------------------------------------------------
    // Demostración automática
    // ------------------------------------------------------------------

    /**
     * Ejecuta un recorrido completo del sistema sin intervención del
     * usuario. Sirve como evidencia reproducible para el informe y el
     * video de sustentación.
     */
    public void ejecutarDemostracion() {
        salida.println("=== DEMOSTRACIÓN AUTOMÁTICA - CASO 19 ===");
        salida.println("Taller de computadores: lista, cola FIFO y pila LIFO");

        seccion("1. REGISTRO DE CINCO EQUIPOS");
        registrar("EQ-001", "Ana Torres", "Portátil Lenovo ThinkPad", "No enciende");
        registrar("EQ-002", "Carlos Pérez", "Impresora HP LaserJet", "Atasco de papel");
        registrar("EQ-003", "María Gómez", "PC de escritorio", "Pantalla azul al iniciar");
        registrar("EQ-004", "Luis Ramírez", "MacBook Air", "Batería no carga");
        registrar("EQ-005", "Sofía Herrera", "Monitor Samsung", "Sin señal HDMI");
        salida.println("Registrados: " + contarRegistrados() + " equipos.");
        salida.println("Pendientes: " + contarPendientes() + " equipos.");

        seccion("2. LISTA GENERAL (RECORRIDO HACIA ADELANTE)");
        salida.println(listarTodos());

        seccion("3. LISTA GENERAL (RECORRIDO HACIA ATRÁS)");
        salida.println(listarTodosEnOrdenInverso());

        seccion("4. COLA DE PENDIENTES (FIFO)");
        salida.println(listarPendientes());

        seccion("5. VALIDACIÓN DE CÓDIGO DUPLICADO");
        try {
            registrar("eq-001", "Otra persona", "Otro equipo", "Otra falla");
        } catch (IllegalArgumentException e) {
            salida.println("Registro rechazado correctamente: " + e.getMessage());
        }

        seccion("6. PROCESAMIENTO DE DOS EQUIPOS");
        salida.println("Procesado: " + procesarSiguiente());
        salida.println("Procesado: " + procesarSiguiente());
        salida.println("Pendientes: " + contarPendientes() + " | Procesados: " + contarProcesados());

        seccion("7. HISTORIAL DE PROCESADOS (LIFO, CIMA PRIMERO)");
        salida.println(listarHistorial());

        seccion("8. BÚSQUEDA DEL EQUIPO EQ-003");
        salida.println("Encontrado: " + buscarPorCodigo("eq-003"));

        seccion("9. CANCELACIÓN DEL EQUIPO EQ-004 CON COLA AUXILIAR");
        salida.println("Cancelado: " + cancelarPendiente("EQ-004"));
        salida.println("Pendientes después de cancelar:");
        salida.println(listarPendientes());

        seccion("10. DESHACER EL ÚLTIMO PROCESAMIENTO (EQ-002)");
        salida.println("Devuelto a la cola: " + deshacerUltimoProcesamiento());
        salida.println("Pendientes después de deshacer:");
        salida.println(listarPendientes());
        salida.println("Historial después de deshacer:");
        salida.println(listarHistorial());

        seccion("11. CANTIDADES FINALES");
        salida.println("Registrados: " + contarRegistrados());
        salida.println("Pendientes : " + contarPendientes());
        salida.println("Procesados : " + contarProcesados());
        salida.println("Cancelados : " + contarPorEstado(EquipoReparacion.Estado.CANCELADO));

        seccion("12. ESTADO FINAL DE LA LISTA GENERAL");
        salida.println(listarTodos());
        salida.println();
        salida.println("=== FIN DE LA DEMOSTRACIÓN ===");
    }

    private void seccion(String titulo) {
        salida.println();
        salida.println("--- " + titulo + " ---");
    }

    private static String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de servicio es obligatorio.");
        }
        return codigo.trim().toUpperCase(Locale.ROOT);
    }
}
