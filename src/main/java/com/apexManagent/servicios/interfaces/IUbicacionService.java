package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Ubicacion;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUbicacionService {

    Page<Ubicacion> buscarTodosPaginados(Pageable pageable);

    List<Ubicacion> obtenerTodos();

    Optional<Ubicacion> obtenerPorId(Integer id);

    Page<Ubicacion> findByNombreUbicacionContaining(String nombreUbicacion, Pageable pageable);

    boolean existsByNombreUbicacion(String nombreUbicacion);

    Ubicacion guardar(Ubicacion ubicacion);

}