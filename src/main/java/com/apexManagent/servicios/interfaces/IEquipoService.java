package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IEquipoService {
    // Métodos básicos
    Page<Equipo> buscarTodosPaginados(Pageable pageable);
    Equipo guardarEquipo(Equipo equipo);
    List<Equipo> obtenerTodos();
    Optional<Equipo> buscarPorId(Integer id);
    void eliminarPorId(Integer id);
    
    // Búsquedas específicas
    Page<Equipo> buscarPorSerie(String serie, Pageable pageable); 
    List<Equipo> buscarPorNombre(String nombre); 
    List<Equipo> buscarPorModelo(String modelo); 
    
    // Métodos adicionales
    Optional<Equipo> buscarPorNserie(String nserie);
    void actualizarUbicacion(Integer equipoId, Integer ubicacionId);
    void modificarEquipo(Integer equipoId, String nombre, String modelo, String descripcion);
    boolean existePorNserie(String nserie);
    Optional<Equipo> buscarSerie(String nserie);
}