package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Equipo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEquipoService {
    // Operaciones CRUD básicas
    Equipo guardarEquipo(Equipo equipo);
    List<Equipo> listarTodos();
    void eliminarPorNserie(String nserie);
    
    // Búsquedas
    Optional<Equipo> buscarPorNserie(String nserie);
    List<Equipo> buscarPorNombre(String nombre);
    List<Equipo> buscarPorModelo(String modelo);
    
    // Operaciones específicas
    void actualizarUbicacion(Integer equipoId, Integer ubicacionId);
    void modificarEquipo(Integer equipoId, String nombre, String modelo, String descripcion);
    boolean existePorNserie(String nserie);

    Page<Equipo> listarTodosPaginados(Pageable pageable);
Page<Equipo> buscarPorNombreModeloOSerie(String search, Pageable pageable);
Optional<Equipo> buscarPorId(Integer id);
void eliminarPorId(Integer id);
}