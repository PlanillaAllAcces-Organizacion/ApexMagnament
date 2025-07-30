package com.apexManagent.modelos;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "asignacion_equipo")
public class AsignacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonalId", nullable = false)
    private Personal personal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EquipoId", nullable = false)
    private Equipo equipo;

    @OneToMany(mappedBy = "asignacionEquipo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Solicitud> solicitud = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
