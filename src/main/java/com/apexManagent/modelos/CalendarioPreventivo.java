package com.apexManagent.modelos;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;


@Entity
public class CalendarioPreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipoId", nullable = false)
    private Equipo equipo;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a la fecha actual") 
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotBlank(message = "La fecha fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    @Future(message = "La fecha de fin debe ser posterior a la fecha actual")
    private LocalDateTime fechaFin;

    @Column(name = "estado_mantenimiento")
    private short estadoMantenimiento;

    @OneToOne(mappedBy = "calendarioPreventivo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReportePreventivo reportePreventivo;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
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

    public short getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(short estadoMantenimiento) {
        this.estadoMantenimiento = estadoMantenimiento;
    }


}
