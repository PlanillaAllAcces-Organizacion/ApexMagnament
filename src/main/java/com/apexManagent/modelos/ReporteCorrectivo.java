package com.apexManagent.modelos;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

//@Entity
public class ReporteCorrectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La observacion es requerida")
    private String observacion;

    @NotBlank(message = "La fecha de creacion es requerido")
    private LocalDateTime fechaCreacion;

    @NotBlank(message = "El tipo de mantenimiento es requerido")
    private byte tipoMantenimiento;

    @NotBlank(message = "El estado es requerido")
    private byte estado;

    private Solicitud solicitud;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public byte getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(byte tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

}
