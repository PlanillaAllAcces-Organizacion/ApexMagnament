package com.apexManagent.repositorio;


import com.apexManagent.modelos.Ubicacion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Page<Ubicacion> findByNombreUbicacionContaining(String nombreUbicacion, Pageable pageable);

}