package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

//@Entity
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
    private byte[] ImgPersonal;

    @NotBlank(message = "El email es requerido")
    @Column(nullable = false, unique = true)    
    private String email;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Column(nullable = false)
    private String password;

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

    public byte getImgPersonal() {
        return ImgPersonal;
    }

    public void setImgPersonal(byte imgPersonal) {
        ImgPersonal = imgPersonal;
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
