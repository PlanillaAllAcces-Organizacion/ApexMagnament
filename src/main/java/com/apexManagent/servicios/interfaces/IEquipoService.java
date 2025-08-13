package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Equipo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEquipoService {

    Page<Equipo> buscarTodosPaginados(Pageable pageable);

    // Operaciones CRUD básicas
    Equipo guardarEquipo(Equipo equipo);
    List<Equipo> obtenerTodos();
    Optional<Equipo> buscarSerie(String nserie);
    Equipo createOrEditOne(Equipo equipo);
    void eliminarPorId(Integer id);

    // Búsquedas
    Optional<Equipo> buscarPorNserie(String nserie);
    Page<Equipo> buscarPorNombre(String nombre, Pageable pageable);
    Page<Equipo> buscarPorModelo(String modelo, Pageable pageable);
    Page<Equipo> buscarPorSerie(String serie, Pageable pageable);
    Page<Equipo> buscarPorNombreModeloOSerie(String search, Pageable pageable);


    // Operaciones específicas
    void actualizarUbicacion(Integer equipoId, Integer ubicacionId);
    void modificarEquipo(Integer equipoId, String nombre, String modelo, String descripcion);
    boolean existePorNserie(String nserie);
    Optional<Equipo> buscarPorId(Integer id);
}