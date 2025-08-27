package com.apexManagent.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apexManagent.modelos.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {

    Page<Categoria> findByNombreCategoriaContaining(String nombreCategoria, Pageable pageable);

    boolean existsByNombreCategoria(String nombreCategoria);
}
