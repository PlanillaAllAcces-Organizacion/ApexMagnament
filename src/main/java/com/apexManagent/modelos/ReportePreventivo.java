package com.apexManagent.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

//@Entity
public class ReportePreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La observacion es requerida")
    private String observacion;

    @NotBlank(message = "La fecha de atencion es requerida")
    private LocalDateTime fechaAtencion;

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

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

}
