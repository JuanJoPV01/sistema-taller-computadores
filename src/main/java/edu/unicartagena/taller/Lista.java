package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class Lista {

    private Nodo primero;
    private Nodo ultimo;
    private int tamanio;

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

    public Object buscarDato(int indice) {
        return nodoEn(indice).getDato();
    }

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

    public boolean contiene(Object dato) {
        return buscarDato(dato) != null;
    }

    public int cuentaElementos() {
        return tamanio;
    }

    public boolean esVacia() {
        return primero == null;
    }

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
