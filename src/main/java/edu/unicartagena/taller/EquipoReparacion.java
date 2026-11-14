package edu.unicartagena.taller;

import java.util.Locale;
import java.util.Objects;

public final class EquipoReparacion {

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

    public EquipoReparacion(String codigoServicio, String cliente,
                            String tipoEquipo, String fallaReportada) {
        this(codigoServicio, cliente, tipoEquipo, fallaReportada, Estado.PENDIENTE);
    }

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

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }
}
