package edu.unicartagena.taller;

/**
 * Nodo enlazado que sirve como unidad de almacenamiento para las
 * estructuras {@link Lista}, {@link Cola} y {@link Pila}.
 *
 * <p>Un nodo no conoce la estructura a la que pertenece: solamente guarda
 * un dato y las referencias hacia los nodos vecinos. Esa separación es la
 * que permite reutilizar la misma clase en las tres estructuras.</p>
 *
 * <p>Las referencias se manipulan directamente desde cada estructura:</p>
 * <ul>
 *   <li>{@code siguiente}: enlaza con el nodo que está hacia el final o
 *       hacia el frente, según la estructura.</li>
 *   <li>{@code anterior}: solo la utiliza la lista doblemente enlazada
 *       para poder recorrerla en sentido inverso. La cola y la pila la
 *       dejan en {@code null}.</li>
 * </ul>
 *
 * <p>El final de una estructura se representa con {@code null}. Por eso
 * todos los recorridos comprueban {@code actual != null} antes de leer el
 * dato o de avanzar.</p>
 */
public final class Nodo {

    private Object dato;
    private Nodo siguiente;
    private Nodo anterior;

    /**
     * Crea un nodo aislado, sin vecinos.
     *
     * @param dato información que se almacenará en el nodo
     */
    public Nodo(Object dato) {
        this(dato, null, null);
    }

    /**
     * Crea un nodo y lo enlaza con sus vecinos.
     *
     * @param dato      información que se almacenará en el nodo
     * @param siguiente nodo que queda hacia el final de la estructura
     * @param anterior  nodo que queda hacia el inicio de la estructura
     */
    public Nodo(Object dato, Nodo siguiente, Nodo anterior) {
        this.dato = dato;
        this.siguiente = siguiente;
        this.anterior = anterior;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    public Nodo getAnterior() {
        return anterior;
    }

    public void setAnterior(Nodo anterior) {
        this.anterior = anterior;
    }

    /**
     * Devuelve el dato del nodo como texto. Se apoya en el
     * {@code toString()} del objeto almacenado, de modo que la salida de
     * las estructuras sea legible sin acoplarlas a un tipo concreto.
     */
    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
