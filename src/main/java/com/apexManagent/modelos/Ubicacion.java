package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La dirección es requerida")
    @Column(nullable = false)
    private String NombreUbicacion;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Equipo> equipo = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

     public String getNombreUbicacion() {
        return NombreUbicacion;
    }

    public void setNombreUbicacion(String nombreUbicacion) {
        NombreUbicacion = nombreUbicacion;
    }

}
