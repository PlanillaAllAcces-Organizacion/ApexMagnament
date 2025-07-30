package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es requerido")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El telefono es requerido")
    private String telefono;

    @NotBlank(message = "La imagen es requerida")
    @Lob
    @Column(name = "img_personal", columnDefinition = "BLOB")
    private byte[] imgPersonal;

    @NotBlank(message = "El email es requerido")
    @Column(nullable = false, unique = true)    
    private String email;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RolId", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Solicitud> solicitud = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReporteCorrectivo> reporteCorrectivo = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReportePreventivo> reportePreventivo = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AsignacionEquipo> asignacionEquipos = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

     public byte[] getImgPersonal() {
        return imgPersonal;
    }

    public void setImgPersonal(byte[] imgPersonal) {
        this.imgPersonal = imgPersonal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
