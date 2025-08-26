package com.apexManagent.servicios.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.apexManagent.modelos.Categoria;

public interface ICategoriaService {

    Page<Categoria> buscarTodosPaginados(Pageable pageable);

    List<Categoria> obtenerTodos();

    Optional<Categoria> obtenerPorId(Integer id);

    Page<Categoria> findByNombreCategoriaContaining(String nombreCategoria, Pageable pageable);

    boolean existsByNombreCategoria(String nombreCategoria);

    Categoria guardar(Categoria categoria);
}
