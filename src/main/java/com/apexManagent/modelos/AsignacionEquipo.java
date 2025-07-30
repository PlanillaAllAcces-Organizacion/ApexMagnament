package com.apexManagent.modelos;

import jakarta.persistence.*;

//@Entity
@Table(name = "asignacion_equipo")
public class AsignacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

   // @ManyToOne
    @JoinColumn(name = "asignacionEquipoId")
    private AsignacionEquipo asignacionEquipo;

    //@ManyToOne
    @JoinColumn(name = "PersonalId", nullable = false)
    private Personal personal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
