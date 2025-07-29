package com.apexManagent.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

//@Entity
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El numero de serie es requerido")
    private String Nserie;

    @NotBlank(message = "El nombre del equipo es requerido")
    private String nombre;

    @NotBlank(message = "El modelo es requerido")
    private String modelo;

    @NotBlank(message = "El descripcion es requerida")
    private String descripcion;

    @NotBlank(message = "La garantia es requerida")
    private byte garantia;

    @NotBlank(message = "La imagen es requerida")
    private byte[] img;

    @NotBlank(message = "La fecha de registro es requerida")
    private LocalDateTime fechaRegistro;

    private Ubicacion ubicacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNserie() {
        return Nserie;
    }

    public void setNserie(String nserie) {
        Nserie = nserie;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getGarantia() {
        return garantia;
    }

    public void setGarantia(byte garantia) {
        this.garantia = garantia;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }
   

}
