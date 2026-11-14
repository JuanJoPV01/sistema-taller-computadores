package edu.unicartagena.taller;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class Cola {

    private Nodo frente;
    private Nodo fin;
    private int tamanio;

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

    public Object peek() {
        return esVacia() ? null : frente.getDato();
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean esVacia() {
        return frente == null;
    }

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
