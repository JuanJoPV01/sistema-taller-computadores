package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class Pila {

    private Nodo cima;
    private int tamanio;

    public void apilar(Object dato) {
        validarDato(dato);
        cima = new Nodo(dato, cima, null);
        tamanio++;
    }

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

    public Object peek() {
        return esVacia() ? null : cima.getDato();
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean esVacia() {
        return cima == null;
    }

    public boolean contiene(Object dato) {
        return buscar(dato) != null;
    }

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
