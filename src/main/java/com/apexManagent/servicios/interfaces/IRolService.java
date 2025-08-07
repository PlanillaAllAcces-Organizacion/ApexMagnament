package com.apexManagent.servicios.interfaces;
import com.apexManagent.modelos.Rol;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IRolService{

    List<Rol> obtenerTodos();

    Optional<Rol> obtenerPorId(Integer id);

    Page<Rol> findByNombreContaining(String nombre, Pageable pageable);

    Rol guardar(Rol rol);

    boolean existsByNombre(String nombre);
}