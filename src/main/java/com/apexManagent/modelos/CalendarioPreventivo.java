package com.apexManagent.modelos;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

//@Entity
public class CalendarioPreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotBlank(message = "La fecha fin es obligatoria")
    private LocalDateTime fechaFin;

    private byte EstadoMantenimiento;

    private Equipo equipo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public byte getEstadoMantenimiento() {
        return EstadoMantenimiento;
    }

    public void setEstadoMantenimiento(byte estadoMantenimiento) {
        EstadoMantenimiento = estadoMantenimiento;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

}
