package com.apexManagent.repositorio;

import com.apexManagent.modelos.Rol;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer> {

    Page<Rol> findByNombreContaining(String nombre, Pageable pageable);
    
}
