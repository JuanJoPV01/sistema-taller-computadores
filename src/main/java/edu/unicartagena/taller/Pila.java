package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Pila LIFO (Last In, First Out) que guarda el historial de equipos
 * procesados.
 *
 * <p>El último equipo reparado queda en la {@code cima} y es el primero
 * que se devuelve al deshacer. La pila solo necesita una referencia y un
 * contador, porque todas las operaciones ocurren en la cima:</p>
 * <ul>
 *   <li>{@code apilar}: el nuevo nodo apunta a la cima anterior y pasa a
 *       ser la nueva cima.</li>
 *   <li>{@code desapilar}: la cima avanza al siguiente nodo y el nodo
 *       extraído se aísla.</li>
 * </ul>
 *
 * <p>Esta estructura resuelve el requisito de deshacer el último
 * procesamiento sin recorrer el historial completo.</p>
 */
public final class Pila {

    private Nodo cima;
    private int tamanio;

    /**
     * Agrega un dato en la cima de la pila.
     *
     * @param dato dato que se desea apilar
     */
    public void apilar(Object dato) {
        validarDato(dato);
        cima = new Nodo(dato, cima, null);
        tamanio++;
    }

    /**
     * Elimina y devuelve el dato de la cima.
     *
     * @return último dato apilado
     * @throws NoSuchElementException si la pila está vacía
     */
    public Object desapilar() {
        if (esVacia()) {
            throw new NoSuchElementException("No hay procesamientos en el historial.");
        }
        Nodo eliminado = cima;
        cima = cima.getSiguiente();
        eliminado.setSiguiente(null);
        eliminado.setAnterior(null);
        tamanio--;
        return eliminado.getDato();
    }

    /**
     * Consulta la cima sin eliminarla.
     *
     * @return último dato apilado o {@code null} si la pila está vacía
     */
    public Object peek() {
        return esVacia() ? null : cima.getDato();
    }

    /**
     * @return cantidad de elementos en el historial
     */
    public int tamanio() {
        return tamanio;
    }

    /**
     * @return {@code true} si la pila no tiene elementos
     */
    public boolean esVacia() {
        return cima == null;
    }

    /**
     * Indica si un dato está en la pila.
     *
     * @param dato dato buscado
     * @return {@code true} si aparece al menos una vez
     */
    public boolean contiene(Object dato) {
        return buscar(dato) != null;
    }

    /**
     * Busca un dato recorriendo desde la cima hacia el fondo.
     *
     * <p>La búsqueda es lineal porque la pila solo ofrece acceso directo a
     * la cima. En este sistema se usa para consultas puntuales; el flujo
     * normal de deshacer siempre trabaja con {@code desapilar()}.</p>
     *
     * @param dato dato buscado
     * @return primera coincidencia desde la cima o {@code null}
     */
    public Object buscar(Object dato) {
        if (dato == null) {
            return null;
        }
        Nodo actual = cima;
        while (actual != null) {
            if (Objects.equals(dato, actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Elimina todos los elementos y libera los enlaces.
     */
    public void limpiar() {
        Nodo actual = cima;
        while (actual != null) {
            Nodo siguiente = actual.getSiguiente();
            actual.setSiguiente(null);
            actual.setAnterior(null);
            actual = siguiente;
        }
        cima = null;
        tamanio = 0;
    }

    /**
     * Recorre la pila desde la cima hasta el fondo. El elemento [1] es el
     * último equipo procesado.
     *
     * @return texto con el historial o un mensaje si está vacía
     */
    public String mostrar() {
        if (esVacia()) {
            return "El historial de procesados está vacío.";
        }
        StringBuilder texto = new StringBuilder();
        Nodo actual = cima;
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
            throw new IllegalArgumentException("No se puede apilar un dato nulo.");
        }
    }
}
