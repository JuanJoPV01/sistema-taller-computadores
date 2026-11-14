package edu.unicartagena.taller;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

public final class PruebasTaller {

    private static int pruebas;
    private static int fallos;

    private PruebasTaller() {
    }

    public static void main(String[] args) {
        probarLista();
        probarCola();
        probarPila();
        probarEquipoReparacion();
        probarSistema();

        System.out.println();
        if (fallos == 0) {
            System.out.println("OK: " + pruebas + " pruebas ejecutadas correctamente.");
        } else {
            System.out.println("FALLOS: " + fallos + " de " + pruebas + " pruebas.");
            System.exit(1);
        }
    }

    private static void probarLista() {
        Lista lista = new Lista();
        verificar("lista nueva vacía", lista.esVacia());
        verificarIgual("lista nueva sin elementos", 0, lista.cuentaElementos());
        verificar("mostrar lista vacía", lista.mostrarAdelante().contains("vacía"));
        verificar("mostrar atrás lista vacía", lista.mostrarAtras().contains("vacía"));

        lista.agregar("A");
        lista.agregar("B");
        lista.agregar("C");
        verificarIgual("agregar al final", 3, lista.cuentaElementos());
        verificarIgual("buscar por índice 0", "A", lista.buscarDato(0));
        verificarIgual("buscar por índice 2", "C", lista.buscarDato(2));
        verificar("contiene B", lista.contiene("B"));
        verificar("no contiene X", !lista.contiene("X"));
        verificarIgual("buscar por dato", "B", lista.buscarDato("B"));
        verificar("buscar dato inexistente", lista.buscarDato("X") == null);
        verificar("no contiene nulo", !lista.contiene(null));

        String adelante = lista.mostrarAdelante();
        verificar("recorrido adelante inicia en A", adelante.startsWith("[1] A"));
        verificar("recorrido adelante termina en C", adelante.endsWith("[3] C"));
        String atras = lista.mostrarAtras();
        verificar("recorrido atrás inicia en C", atras.startsWith("[3] C"));
        verificar("recorrido atrás termina en A", atras.endsWith("[1] A"));

        lista.agregarAlInicio("X");
        verificarIgual("agregar al inicio", "X", lista.buscarDato(0));
        verificarIgual("agregar al inicio conserva tamaño", 4, lista.cuentaElementos());

        lista.agregarEnPosicion(2, "Y");
        verificarIgual("insertar en posición 2", "Y", lista.buscarDato(2));
        verificarIgual("insertar en posición 2 conserva orden", "B", lista.buscarDato(3));
        lista.agregarEnPosicion(0, "Z");
        verificarIgual("insertar en posición 0", "Z", lista.buscarDato(0));
        int ultimoIndice = lista.cuentaElementos();
        lista.agregarEnPosicion(ultimoIndice, "W");
        verificarIgual("insertar al final con índice tamaño", "W", lista.buscarDato(ultimoIndice));

        verificarIgual("eliminar primero", "Z", lista.eliminarPrimero());
        verificarIgual("eliminar último", "W", lista.eliminarUltimo());
        verificarIgual("eliminar en posición 1", "A", lista.eliminarEnPosicion(1));
        verificarIgual("tamaño después de eliminar", 4, lista.cuentaElementos());

        verificarExcepcion("índice negativo", IndexOutOfBoundsException.class,
                () -> lista.buscarDato(-1));
        verificarExcepcion("índice fuera de rango", IndexOutOfBoundsException.class,
                () -> lista.buscarDato(99));
        verificarExcepcion("insertar en índice inválido", IndexOutOfBoundsException.class,
                () -> lista.agregarEnPosicion(99, "Q"));
        verificarExcepcion("eliminar en índice inválido", IndexOutOfBoundsException.class,
                () -> lista.eliminarEnPosicion(99));
        verificarExcepcion("agregar nulo", IllegalArgumentException.class,
                () -> lista.agregar(null));

        lista.limpiar();
        verificar("limpiar lista", lista.esVacia());
        verificarIgual("limpiar deja tamaño cero", 0, lista.cuentaElementos());
        verificarExcepcion("eliminar primero en lista vacía", NoSuchElementException.class,
                () -> lista.eliminarPrimero());
        verificarExcepcion("eliminar último en lista vacía", NoSuchElementException.class,
                () -> lista.eliminarUltimo());
    }

    private static void probarCola() {
        Cola cola = new Cola();
        verificar("cola nueva vacía", cola.esVacia());
        verificarIgual("cola nueva sin elementos", 0, cola.tamanio());
        verificar("peek en cola vacía", cola.peek() == null);

        cola.encolar("A");
        cola.encolar("B");
        cola.encolar("C");
        verificarIgual("peek devuelve el frente", "A", cola.peek());
        verificarIgual("tamaño de la cola", 3, cola.tamanio());
        verificar("cola contiene B", cola.contiene("B"));
        verificar("cola no contiene X", !cola.contiene("X"));
        verificar("cola no contiene nulo", !cola.contiene(null));
        verificar("mostrar cola inicia en el frente", cola.mostrar().startsWith("[1] A"));
        verificar("mostrar cola termina en el final", cola.mostrar().endsWith("[3] C"));

        verificarIgual("desencolar primer elemento", "A", cola.desencolar());
        verificarIgual("nuevo frente después de desencolar", "B", cola.peek());
        verificarIgual("desencolar segundo elemento", "B", cola.desencolar());
        verificarIgual("desencolar último elemento", "C", cola.desencolar());
        verificar("cola vacía después de desencolar todo", cola.esVacia());
        verificarExcepcion("desencolar en cola vacía", NoSuchElementException.class, cola::desencolar);
        verificarExcepcion("encolar nulo", IllegalArgumentException.class, () -> cola.encolar(null));

        cola.encolar("A");
        cola.encolar("B");
        cola.limpiar();
        verificar("limpiar cola", cola.esVacia());
        verificarIgual("limpiar cola deja tamaño cero", 0, cola.tamanio());
    }

    private static void probarPila() {
        Pila pila = new Pila();
        verificar("pila nueva vacía", pila.esVacia());
        verificarIgual("pila nueva sin elementos", 0, pila.tamanio());
        verificar("peek en pila vacía", pila.peek() == null);

        pila.apilar("A");
        pila.apilar("B");
        pila.apilar("C");
        verificarIgual("peek devuelve la cima", "C", pila.peek());
        verificarIgual("tamaño de la pila", 3, pila.tamanio());
        verificar("pila contiene A", pila.contiene("A"));
        verificar("pila no contiene X", !pila.contiene("X"));
        verificarIgual("buscar en pila", "B", pila.buscar("B"));
        verificar("buscar inexistente en pila", pila.buscar("X") == null);
        verificar("mostrar pila inicia en la cima", pila.mostrar().startsWith("[1] C"));
        verificar("mostrar pila termina en el fondo", pila.mostrar().endsWith("[3] A"));

        verificarIgual("desapilar la cima", "C", pila.desapilar());
        verificarIgual("nueva cima después de desapilar", "B", pila.peek());
        verificarIgual("desapilar segundo elemento", "B", pila.desapilar());
        verificarIgual("desapilar último elemento", "A", pila.desapilar());
        verificar("pila vacía después de desapilar todo", pila.esVacia());
        verificarExcepcion("desapilar en pila vacía", NoSuchElementException.class, pila::desapilar);
        verificarExcepcion("apilar nulo", IllegalArgumentException.class, () -> pila.apilar(null));

        pila.apilar("A");
        pila.limpiar();
        verificar("limpiar pila", pila.esVacia());
        verificarIgual("limpiar pila deja tamaño cero", 0, pila.tamanio());
    }

    private static void probarEquipoReparacion() {
        EquipoReparacion equipo = new EquipoReparacion(
                " eq-001 ", "Ana Torres", "Portátil", "No enciende");
        verificarIgual("código normalizado", "EQ-001", equipo.getCodigoServicio());
        verificarIgual("cliente", "Ana Torres", equipo.getCliente());
        verificarIgual("tipo de equipo", "Portátil", equipo.getTipoEquipo());
        verificarIgual("falla reportada", "No enciende", equipo.getFallaReportada());
        verificarIgual("estado inicial", EquipoReparacion.Estado.PENDIENTE, equipo.getEstado());
        verificar("toString incluye el código", equipo.toString().contains("EQ-001"));
        verificar("toString incluye el estado", equipo.toString().contains("PENDIENTE"));

        equipo.setCliente("Ana María Torres");
        equipo.setTipoEquipo("Portátil Lenovo");
        equipo.setFallaReportada("No enciende ni carga");
        equipo.setEstado(EquipoReparacion.Estado.REPARADO);
        verificarIgual("setter de cliente", "Ana María Torres", equipo.getCliente());
        verificarIgual("setter de estado", EquipoReparacion.Estado.REPARADO, equipo.getEstado());

        EquipoReparacion mismoCodigo = new EquipoReparacion(
                "EQ-001", "Otra persona", "Otro equipo", "Otra falla");
        EquipoReparacion otroCodigo = new EquipoReparacion(
                "EQ-002", "Otra persona", "Otro equipo", "Otra falla");
        verificar("equals por código", equipo.equals(mismoCodigo));
        verificar("equals ignora mayúsculas", equipo.equals(new EquipoReparacion(
                "eq-001", "X", "Y", "Z")));
        verificar("equals rechaza otro código", !equipo.equals(otroCodigo));
        verificar("equals rechaza nulo", !equipo.equals(null));
        verificar("equals rechaza otra clase", !equipo.equals("EQ-001"));
        verificarIgual("hashCode coherente con equals", equipo.hashCode(), mismoCodigo.hashCode());
        verificar("hashCode distinto para otro código", equipo.hashCode() != otroCodigo.hashCode());

        verificarExcepcion("código obligatorio", IllegalArgumentException.class,
                () -> new EquipoReparacion(" ", "Cliente", "Equipo", "Falla"));
        verificarExcepcion("cliente obligatorio", IllegalArgumentException.class,
                () -> new EquipoReparacion("EQ-003", null, "Equipo", "Falla"));
        verificarExcepcion("estado obligatorio", IllegalArgumentException.class,
                () -> equipo.setEstado(null));
    }

    private static void probarSistema() {
        SistemaDelCaso sistema = nuevoSistema();
        verificarIgual("sistema inicia sin registros", 0, sistema.contarRegistrados());
        verificarIgual("sistema inicia sin pendientes", 0, sistema.contarPendientes());
        verificarIgual("sistema inicia sin procesados", 0, sistema.contarProcesados());

        sistema.registrar("EQ-001", "Ana Torres", "Portátil", "No enciende");
        sistema.registrar("EQ-002", "Carlos Pérez", "Impresora", "Atasco de papel");
        sistema.registrar("EQ-003", "María Gómez", "PC", "Pantalla azul");
        verificarIgual("tres equipos registrados", 3, sistema.contarRegistrados());
        verificarIgual("tres equipos pendientes", 3, sistema.contarPendientes());
        verificarIgual("ningún equipo procesado", 0, sistema.contarProcesados());
        verificar("cola respeta orden de llegada",
                sistema.listarPendientes().indexOf("EQ-001") < sistema.listarPendientes().indexOf("EQ-002"));

        verificarExcepcion("rechazar código duplicado", IllegalArgumentException.class,
                () -> sistema.registrar("eq-001", "Otra", "Otro", "Otra"));
        verificarIgual("duplicado no altera el registro", 3, sistema.contarRegistrados());
        verificarIgual("duplicado no altera la cola", 3, sistema.contarPendientes());

        EquipoReparacion buscado = sistema.buscarPorCodigo(" eq-002 ");
        verificarIgual("búsqueda normaliza el código", "EQ-002", buscado.getCodigoServicio());
        verificarExcepcion("búsqueda inexistente", NoSuchElementException.class,
                () -> sistema.buscarPorCodigo("EQ-999"));
        verificarExcepcion("búsqueda con código vacío", IllegalArgumentException.class,
                () -> sistema.buscarPorCodigo(" "));

        EquipoReparacion primero = sistema.procesarSiguiente();
        verificarIgual("procesar respeta FIFO", "EQ-001", primero.getCodigoServicio());
        verificarIgual("procesado cambia de estado", EquipoReparacion.Estado.REPARADO, primero.getEstado());
        verificarIgual("un equipo procesado", 1, sistema.contarProcesados());
        verificarIgual("dos equipos pendientes", 2, sistema.contarPendientes());
        verificar("historial inicia con el último procesado",
                sistema.listarHistorial().startsWith("[1] EQ-001"));

        sistema.procesarSiguiente();
        verificar("historial apila en la cima", sistema.listarHistorial().startsWith("[1] EQ-002"));
        verificarIgual("dos equipos procesados", 2, sistema.contarProcesados());

        EquipoReparacion deshecho = sistema.deshacerUltimoProcesamiento();
        verificarIgual("deshacer devuelve el último procesado", "EQ-002", deshecho.getCodigoServicio());
        verificarIgual("deshacer restaura el estado", EquipoReparacion.Estado.PENDIENTE, deshecho.getEstado());
        verificarIgual("deshacer reduce el historial", 1, sistema.contarProcesados());
        verificarIgual("deshacer aumenta pendientes", 2, sistema.contarPendientes());
        verificar("deshacer reencola al final",
                sistema.listarPendientes().indexOf("EQ-003") < sistema.listarPendientes().indexOf("EQ-002"));

        SistemaDelCaso cancelaciones = nuevoSistema();
        cancelaciones.registrar("EQ-101", "Cliente 1", "PC", "Falla 1");
        cancelaciones.registrar("EQ-102", "Cliente 2", "PC", "Falla 2");
        cancelaciones.registrar("EQ-103", "Cliente 3", "PC", "Falla 3");
        cancelaciones.registrar("EQ-104", "Cliente 4", "PC", "Falla 4");
        cancelaciones.registrar("EQ-105", "Cliente 5", "PC", "Falla 5");

        EquipoReparacion cancelado = cancelaciones.cancelarPendiente("EQ-103");
        verificarIgual("cancelar marca el estado", EquipoReparacion.Estado.CANCELADO, cancelado.getEstado());
        verificarIgual("cancelar reduce la cola", 4, cancelaciones.contarPendientes());
        verificarIgual("cancelar conserva el registro", 5, cancelaciones.contarRegistrados());
        verificar("cancelar conserva el orden FIFO",
                cancelaciones.listarPendientes().indexOf("EQ-102")
                        < cancelaciones.listarPendientes().indexOf("EQ-104"));
        verificar("cancelado no está en la cola",
                !cancelaciones.listarPendientes().contains("EQ-103"));
        verificarIgual("cancelado permanece en la lista",
                "CANCELADO", cancelaciones.buscarPorCodigo("EQ-103").getEstado().name());
        verificarExcepcion("no cancelar dos veces", IllegalStateException.class,
                () -> cancelaciones.cancelarPendiente("EQ-103"));
        verificarExcepcion("no cancelar un equipo ya procesado", IllegalStateException.class,
                () -> {
                    cancelaciones.procesarSiguiente();
                    cancelaciones.cancelarPendiente("EQ-101");
                });
        verificarIgual("conteo por estado cancelado", 1,
                cancelaciones.contarPorEstado(EquipoReparacion.Estado.CANCELADO));

        SistemaDelCaso vacio = nuevoSistema();
        verificarExcepcion("procesar sin pendientes", NoSuchElementException.class,
                vacio::procesarSiguiente);
        verificarExcepcion("deshacer sin historial", NoSuchElementException.class,
                vacio::deshacerUltimoProcesamiento);
        verificar("lista vacía muestra mensaje",
                vacio.listarTodos().contains("vacía"));
    }

    private static SistemaDelCaso nuevoSistema() {
        PrintStream salida = new PrintStream(OutputStream.nullOutputStream());
        return new SistemaDelCaso(new Scanner(""), salida);
    }

    private static void verificar(String descripcion, boolean condicion) {
        pruebas++;
        if (!condicion) {
            fallos++;
            System.out.println("FALLO: " + descripcion);
        }
    }

    private static void verificarIgual(String descripcion, Object esperado, Object obtenido) {
        verificar(descripcion + " (esperado=" + esperado + ", obtenido=" + obtenido + ")",
                Objects.equals(esperado, obtenido));
    }

    private static void verificarExcepcion(String descripcion,
                                           Class<? extends RuntimeException> tipo,
                                           Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (no se lanzó la excepción)", false);
        } catch (RuntimeException e) {
            verificar(descripcion + " (" + e.getClass().getSimpleName() + ")",
                    tipo.isInstance(e));
        }
    }
}
