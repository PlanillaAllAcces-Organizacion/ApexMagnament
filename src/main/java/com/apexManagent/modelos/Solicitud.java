package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

//@Entity
public class Solicitud {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La fecha de registro requerido")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @NotBlank(message = "La descripcion es requerida")
    @Column(nullable = false)
    private String descripcion; 

    @NotBlank(message = "El estado es requerido")
    private byte estado;

    //@JoinColumn(name = "asignacion_equipo_id", nullable = false)
    private AsignacionEquipo asignacionEquipo;

   // @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

   
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public AsignacionEquipo getAsignacionEquipo() {
        return asignacionEquipo;
    }

    public void setAsignacionEquipo(AsignacionEquipo asignacionEquipo) {
        this.asignacionEquipo = asignacionEquipo;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }
}
