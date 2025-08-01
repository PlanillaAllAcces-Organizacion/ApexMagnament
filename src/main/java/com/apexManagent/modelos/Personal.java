package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RolId", nullable = false)
    private Rol rol;

    @NotBlank(message = "El nombre es requerido")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El telefono es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El formato del teléfono debe ser 8 dígitos numéricos")
    @Pattern(regexp = "^(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Formato de teléfono inválido")
    @Column(nullable = false)
    private String telefono;

    @Lob
    @Column(name = "img_personal", columnDefinition = "BLOB")
    private byte[] imgPersonal;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(nullable = false, unique = true)    
    private String email;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Column(nullable = false)
    @Size(min = 8, max = 255, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Solicitud> solicitud = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReporteCorrectivo> reporteCorrectivo = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReportePreventivo> reportePreventivo = new HashSet<>();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AsignacionEquipo> asignacionEquipos = new HashSet<>();

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
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
