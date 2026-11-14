package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista doblemente enlazada que actúa como registro general del sistema.
 *
 * <p>Guarda todos los elementos que entran al taller, en el mismo orden en
 * que fueron registrados. Al ser doblemente enlazada, cada nodo conoce a su
 * vecino anterior y a su vecino siguiente; por eso se puede recorrer de
 * adelante hacia atrás y de atrás hacia adelante sin invertir la
 * estructura.</p>
 *
 * <p>La lista mantiene tres datos de control:</p>
 * <ul>
 *   <li>{@code primero}: nodo inicial. Si es {@code null}, la lista está
 *       vacía.</li>
 *   <li>{@code ultimo}: nodo final. Permite agregar al final y eliminar el
 *       último en O(1), sin recorrer toda la lista.</li>
 *   <li>{@code tamanio}: cantidad de elementos. Evita recorrer la lista
 *       solo para contar y sirve para validar índices.</li>
 * </ul>
 */
public final class Lista {

    private Nodo primero;
    private Nodo ultimo;
    private int tamanio;

    /**
     * Agrega un dato al final de la lista.
     *
     * <p>El nuevo nodo queda enlazado con el último actual mediante
     * {@code siguiente} y {@code anterior}. Luego se actualiza la
     * referencia {@code ultimo}. Si la lista estaba vacía, el nodo también
     * pasa a ser el primero.</p>
     *
     * @param dato dato que se desea registrar
     */
    public void agregar(Object dato) {
        validarDato(dato);
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            primero = nuevo;
        } else {
            nuevo.setAnterior(ultimo);
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamanio++;
    }

    /**
     * Agrega un dato al inicio de la lista.
     *
     * <p>El nuevo nodo apunta al antiguo primero y el antiguo primero
     * apunta hacia atrás al nuevo nodo. La referencia {@code ultimo} solo
     * cambia cuando la lista estaba vacía.</p>
     *
     * @param dato dato que se desea registrar
     */
    public void agregarAlInicio(Object dato) {
        validarDato(dato);
        Nodo nuevo = new Nodo(dato, primero, null);
        if (esVacia()) {
            ultimo = nuevo;
        } else {
            primero.setAnterior(nuevo);
        }
        primero = nuevo;
        tamanio++;
    }

    /**
     * Inserta un dato en una posición específica.
     *
     * <p>Se acepta el índice {@code tamanio} para agregar al final. Para
     * una posición intermedia se busca el nodo que ocupa ese índice y se
     * conecta el nuevo nodo entre su anterior y él, actualizando las cuatro
     * referencias involucradas.</p>
     *
     * @param indice posición donde quedará el nuevo dato (0 a tamanio)
     * @param dato   dato que se desea registrar
     * @throws IndexOutOfBoundsException si el índice no está en el rango
     */
    public void agregarEnPosicion(int indice, Object dato) {
        validarDato(dato);
        if (indice < 0 || indice > tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: " + indice + " (tamaño " + tamanio + ").");
        }
        if (indice == 0) {
            agregarAlInicio(dato);
            return;
        }
        if (indice == tamanio) {
            agregar(dato);
            return;
        }

        Nodo siguiente = nodoEn(indice);
        Nodo anterior = siguiente.getAnterior();
        Nodo nuevo = new Nodo(dato, siguiente, anterior);
        anterior.setSiguiente(nuevo);
        siguiente.setAnterior(nuevo);
        tamanio++;
    }

    /**
     * Elimina y devuelve el primer elemento.
     *
     * @return dato que estaba en la primera posición
     * @throws NoSuchElementException si la lista está vacía
     */
    public Object eliminarPrimero() {
        if (esVacia()) {
            throw new NoSuchElementException("No se puede eliminar: la lista general está vacía.");
        }
        Nodo eliminado = primero;
        primero = primero.getSiguiente();
        if (primero == null) {
            ultimo = null;
        } else {
            primero.setAnterior(null);
        }
        eliminado.setSiguiente(null);
        eliminado.setAnterior(null);
        tamanio--;
        return eliminado.getDato();
    }

    /**
     * Elimina y devuelve el último elemento.
     *
     * @return dato que estaba en la última posición
     * @throws NoSuchElementException si la lista está vacía
     */
    public Object eliminarUltimo() {
        if (esVacia()) {
            throw new NoSuchElementException("No se puede eliminar: la lista general está vacía.");
        }
        Nodo eliminado = ultimo;
        ultimo = ultimo.getAnterior();
        if (ultimo == null) {
            primero = null;
        } else {
            ultimo.setSiguiente(null);
        }
        eliminado.setSiguiente(null);
        eliminado.setAnterior(null);
        tamanio--;
        return eliminado.getDato();
    }

    /**
     * Elimina y devuelve el elemento de una posición específica.
     *
     * @param indice posición del elemento (0 a tamanio - 1)
     * @return dato que ocupaba la posición indicada
     * @throws IndexOutOfBoundsException si el índice no está en el rango
     */
    public Object eliminarEnPosicion(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: " + indice + " (tamaño " + tamanio + ").");
        }
        if (indice == 0) {
            return eliminarPrimero();
        }
        if (indice == tamanio - 1) {
            return eliminarUltimo();
        }

        Nodo eliminado = nodoEn(indice);
        Nodo anterior = eliminado.getAnterior();
        Nodo siguiente = eliminado.getSiguiente();
        anterior.setSiguiente(siguiente);
        siguiente.setAnterior(anterior);
        eliminado.setSiguiente(null);
        eliminado.setAnterior(null);
        tamanio--;
        return eliminado.getDato();
    }

    /**
     * Busca un dato por su posición.
     *
     * @param indice posición del elemento (0 a tamanio - 1)
     * @return dato almacenado en esa posición
     * @throws IndexOutOfBoundsException si el índice no está en el rango
     */
    public Object buscarDato(int indice) {
        return nodoEn(indice).getDato();
    }

    /**
     * Busca un dato por igualdad.
     *
     * <p>Recorre la lista desde el primero y compara con
     * {@link Objects#equals(Object, Object)}. Esa decisión permite que la
     * búsqueda use el {@code equals()} del objeto almacenado, que en este
     * caso compara por código de servicio.</p>
     *
     * @param dato dato buscado
     * @return primera coincidencia o {@code null} si no existe
     */
    public Object buscarDato(Object dato) {
        if (dato == null) {
            return null;
        }
        Nodo actual = primero;
        while (actual != null) {
            if (Objects.equals(dato, actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Indica si un dato está registrado en la lista.
     *
     * @param dato dato buscado
     * @return {@code true} si existe una coincidencia
     */
    public boolean contiene(Object dato) {
        return buscarDato(dato) != null;
    }

    /**
     * @return cantidad de elementos registrados
     */
    public int cuentaElementos() {
        return tamanio;
    }

    /**
     * @return {@code true} si la lista no tiene elementos
     */
    public boolean esVacia() {
        return primero == null;
    }

    /**
     * Elimina todos los elementos y libera los enlaces entre nodos para que
     * el recolector de basura pueda recuperarlos.
     */
    public void limpiar() {
        Nodo actual = primero;
        while (actual != null) {
            Nodo siguiente = actual.getSiguiente();
            actual.setSiguiente(null);
            actual.setAnterior(null);
            actual = siguiente;
        }
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    /**
     * Recorre la lista desde el primero hasta el último y devuelve el
     * contenido numerado.
     *
     * @return texto con todos los elementos o un mensaje si está vacía
     */
    public String mostrarAdelante() {
        if (esVacia()) {
            return "La lista general está vacía.";
        }
        StringBuilder texto = new StringBuilder();
        Nodo actual = primero;
        int posicion = 1;
        while (actual != null) {
            texto.append('[').append(posicion++).append("] ").append(actual.getDato());
            if (actual.getSiguiente() != null) {
                texto.append(System.lineSeparator());
            }
            actual = actual.getSiguiente();
        }
        return texto.toString();
    }

    /**
     * Recorre la lista desde el último hasta el primero usando la referencia
     * {@code anterior}. Es la operación que demuestra el enlace doble.
     *
     * @return texto con todos los elementos en orden inverso
     */
    public String mostrarAtras() {
        if (esVacia()) {
            return "La lista general está vacía.";
        }
        StringBuilder texto = new StringBuilder();
        Nodo actual = ultimo;
        int posicion = tamanio;
        while (actual != null) {
            texto.append('[').append(posicion--).append("] ").append(actual.getDato());
            if (actual.getAnterior() != null) {
                texto.append(System.lineSeparator());
            }
            actual = actual.getAnterior();
        }
        return texto.toString();
    }

    /**
     * Ubica el nodo de una posición recorriendo desde el extremo más
     * cercano. Así, buscar el índice 1 en una lista larga no obliga a
     * recorrer toda la estructura.
     */
    private Nodo nodoEn(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: " + indice + " (tamaño " + tamanio + ").");
        }
        Nodo actual;
        if (indice < tamanio / 2) {
            actual = primero;
            for (int i = 0; i < indice; i++) {
                actual = actual.getSiguiente();
            }
        } else {
            actual = ultimo;
            for (int i = tamanio - 1; i > indice; i--) {
                actual = actual.getAnterior();
            }
        }
        return actual;
    }

    private static void validarDato(Object dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede almacenar un dato nulo.");
        }
    }
}
