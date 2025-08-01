package com.apexManagent.modelos;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ReporteCorrectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "La observacion es requerida")
    @Column(nullable = false)
    private String observacion;

    @NotBlank(message = "La fecha de creacion es requerido")
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @NotBlank(message = "El tipo de mantenimiento es requerido")
    @Column(nullable = false)
    private short tipoMantenimiento;

    @NotBlank(message = "El estado es requerido")
    @Column(nullable = false)
    private short estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SolicitudId", nullable = false)
    private Solicitud solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonalId", nullable = false)
    private Personal personal;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        this.Id = id;
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

    
    public short getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(short tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

}
