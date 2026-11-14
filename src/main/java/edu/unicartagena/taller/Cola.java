package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Cola FIFO (First In, First Out) que administra los equipos pendientes de
 * reparación.
 *
 * <p>El primer equipo que se encola es el primero que se atiende. Para
 * lograrlo se mantienen dos referencias:</p>
 * <ul>
 *   <li>{@code frente}: nodo que se atenderá en el próximo
 *       {@code desencolar()}.</li>
 *   <li>{@code fin}: último nodo encolado. Permite insertar en O(1) sin
 *       recorrer la cola.</li>
 * </ul>
 *
 * <p>La cola no permite eliminar del centro: por diseño solo se extrae el
 * frente. Cuando el sistema necesita cancelar un elemento intermedio, usa
 * una cola auxiliar y reconstruye el orden.</p>
 */
public final class Cola {

    private Nodo frente;
    private Nodo fin;
    private int tamanio;

    /**
     * Agrega un dato al final de la cola.
     *
     * <p>Si la cola está vacía, el nuevo nodo es a la vez frente y fin. En
     * caso contrario, el antiguo fin apunta al nuevo nodo y el fin se
     * actualiza. La referencia {@code siguiente} del nuevo nodo queda en
     * {@code null} para marcar el final.</p>
     *
     * @param dato dato que se desea encolar
     */
    public void encolar(Object dato) {
        validarDato(dato);
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamanio++;
    }

    /**
     * Elimina y devuelve el dato que está al frente de la cola.
     *
     * <p>Se avanza {@code frente} al siguiente nodo y se aísla el nodo
     * eliminado. Si la cola queda vacía, {@code fin} también debe quedar en
     * {@code null}; de lo contrario apuntaría a un nodo que ya no
     * pertenece a la estructura.</p>
     *
     * @return dato que estaba al frente
     * @throws NoSuchElementException si la cola está vacía
     */
    public Object desencolar() {
        if (esVacia()) {
            throw new NoSuchElementException("No hay equipos pendientes en la cola.");
        }
        Nodo eliminado = frente;
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        eliminado.setSiguiente(null);
        tamanio--;
        return eliminado.getDato();
    }

    /**
     * Consulta el dato del frente sin eliminarlo.
     *
     * @return dato del frente o {@code null} si la cola está vacía
     */
    public Object peek() {
        return esVacia() ? null : frente.getDato();
    }

    /**
     * @return cantidad de elementos encolados
     */
    public int tamanio() {
        return tamanio;
    }

    /**
     * @return {@code true} si la cola no tiene elementos
     */
    public boolean esVacia() {
        return frente == null;
    }

    /**
     * Indica si un dato está en la cola comparando con
     * {@link Objects#equals(Object, Object)}.
     *
     * @param dato dato buscado
     * @return {@code true} si aparece al menos una vez
     */
    public boolean contiene(Object dato) {
        if (dato == null) {
            return false;
        }
        Nodo actual = frente;
        while (actual != null) {
            if (Objects.equals(dato, actual.getDato())) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Elimina todos los elementos y libera los enlaces.
     */
    public void limpiar() {
        Nodo actual = frente;
        while (actual != null) {
            Nodo siguiente = actual.getSiguiente();
            actual.setSiguiente(null);
            actual = siguiente;
        }
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /**
     * Recorre la cola desde el frente hasta el final y devuelve el
     * contenido numerado. El elemento [1] es el próximo en atenderse.
     *
     * @return texto con los elementos pendientes o un mensaje si está vacía
     */
    public String mostrar() {
        if (esVacia()) {
            return "No hay equipos pendientes en la cola.";
        }
        StringBuilder texto = new StringBuilder();
        Nodo actual = frente;
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

    private static void validarDato(Object dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede encolar un dato nulo.");
        }
    }
}
