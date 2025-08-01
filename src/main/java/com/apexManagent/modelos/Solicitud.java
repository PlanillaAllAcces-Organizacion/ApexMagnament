package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Solicitud {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "La fecha de registro requerido")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @NotBlank(message = "La descripcion es requerida")
    @Column(nullable = false)
    private String descripcion; 

    @NotBlank(message = "El estado es requerido")
    private short estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacionEquipoId", nullable = false)
    private AsignacionEquipo asignacionEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personalId", nullable = false)
    private Personal personal;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReporteCorrectivo reporteCorrectivo;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        this.Id = id;
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

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public AsignacionEquipo getAsignacionEquipo() {
        return asignacionEquipo;
    }

}
