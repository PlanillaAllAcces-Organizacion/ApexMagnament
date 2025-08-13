package com.apexManagent.repositorio;

import com.apexManagent.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEquiposRepository extends JpaRepository<Equipo, Integer> {
    boolean existsByNserie(String nserie);
    
    Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
        String nserie, String nombre, String modelo, Pageable pageable);
}