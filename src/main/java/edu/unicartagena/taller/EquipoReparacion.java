package edu.unicartagena.taller;

import java.util.Locale;
import java.util.Objects;

/**
 * Entidad principal del caso 19: un equipo recibido en el taller de
 * computadores para diagnóstico y reparación.
 *
 * <p>El identificador del negocio es el <strong>código de servicio</strong>.
 * Por eso {@link #equals(Object)} compara únicamente ese atributo: dos
 * registros con el mismo código representan el mismo equipo, aunque sus
 * demás datos se hayan corregido. Esta decisión permite detectar
 * duplicados al registrar y buscar sin ambigüedad.</p>
 *
 * <p>El estado es un {@link Estado} y no un texto libre, para impedir
 * valores inválidos y hacer explícitas las transiciones del flujo:</p>
 * <ul>
 *   <li>{@code PENDIENTE}: está en la cola de atención.</li>
 *   <li>{@code REPARADO}: fue procesado y está en el historial.</li>
 *   <li>{@code CANCELADO}: fue retirado de la cola, pero permanece en el
 *       registro general.</li>
 * </ul>
 */
public final class EquipoReparacion {

    /**
     * Estados posibles de un equipo dentro del taller.
     */
    public enum Estado {
        PENDIENTE,
        REPARADO,
        CANCELADO
    }

    private String codigoServicio;
    private String cliente;
    private String tipoEquipo;
    private String fallaReportada;
    private Estado estado;

    /**
     * Crea un equipo nuevo en estado {@link Estado#PENDIENTE}.
     *
     * @param codigoServicio código único del servicio
     * @param cliente        nombre del cliente
     * @param tipoEquipo     tipo o modelo del equipo
     * @param fallaReportada descripción de la falla
     */
    public EquipoReparacion(String codigoServicio, String cliente,
                            String tipoEquipo, String fallaReportada) {
        this(codigoServicio, cliente, tipoEquipo, fallaReportada, Estado.PENDIENTE);
    }

    /**
     * Crea un equipo con un estado explícito. Se usa al reconstruir
     * registros de prueba o al cargar información existente.
     *
     * @param codigoServicio código único del servicio
     * @param cliente        nombre del cliente
     * @param tipoEquipo     tipo o modelo del equipo
     * @param fallaReportada descripción de la falla
     * @param estado         estado inicial del equipo
     */
    public EquipoReparacion(String codigoServicio, String cliente,
                            String tipoEquipo, String fallaReportada,
                            Estado estado) {
        setCodigoServicio(codigoServicio);
        setCliente(cliente);
        setTipoEquipo(tipoEquipo);
        setFallaReportada(fallaReportada);
        setEstado(estado);
    }

    public String getCodigoServicio() {
        return codigoServicio;
    }

    /**
     * Asigna el código y lo normaliza a mayúsculas para que la búsqueda no
     * dependa de cómo lo escriba el usuario.
     *
     * @param codigoServicio código único del servicio
     */
    public void setCodigoServicio(String codigoServicio) {
        this.codigoServicio = validarTexto(codigoServicio, "código de servicio")
                .toUpperCase(Locale.ROOT);
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = validarTexto(cliente, "cliente");
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = validarTexto(tipoEquipo, "tipo de equipo");
    }

    public String getFallaReportada() {
        return fallaReportada;
    }

    public void setFallaReportada(String fallaReportada) {
        this.fallaReportada = validarTexto(fallaReportada, "falla reportada");
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del equipo es obligatorio.");
        }
        this.estado = estado;
    }

    /**
     * Compara por el identificador principal del caso: el código de
     * servicio. Se ignora el estado y los demás atributos porque el código
     * es la clave del negocio.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof EquipoReparacion)) {
            return false;
        }
        EquipoReparacion otro = (EquipoReparacion) objeto;
        return codigoServicio.equalsIgnoreCase(otro.codigoServicio);
    }

    /**
     * Mantiene el contrato de {@code equals}: dos equipos iguales deben
     * producir el mismo hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigoServicio.toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Cliente: %s | Equipo: %s | Falla: %s | Estado: %s",
                codigoServicio, cliente, tipoEquipo, fallaReportada, estado);
    }

    /**
     * Valida que un texto no sea nulo ni esté compuesto solo por espacios.
     * Se aplica en el constructor y en los setters para que el objeto nunca
     * quede en un estado inconsistente.
     */
    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }
}
