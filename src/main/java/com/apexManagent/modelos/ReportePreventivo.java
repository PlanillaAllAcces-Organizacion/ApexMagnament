package com.apexManagent.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ReportePreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CalendarioPreventivoId", nullable = false) 
    private CalendarioPreventivo calendarioPreventivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonalId", nullable = false)
    private Personal personal;

    @NotBlank(message = "La observacion es requerida")
    @Column(nullable = false)
    private String observacion;

    @NotBlank(message = "La fecha de atencion es requerida")
    @Column(nullable = false)
    private LocalDateTime fechaAtencion;

    @Column(nullable = false)
    private Short  tipoMantenimiento;

    @Column(nullable = false)
    private Short estado;

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

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public Short getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(Short tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

}
