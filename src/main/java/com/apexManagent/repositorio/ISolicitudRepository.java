package com.apexManagent.repositorio;

import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    
    Page<Solicitud> findByPersonalId(@Param("personalId") Integer personalId, Pageable pageable);
}