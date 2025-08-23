package com.apexManagent.repositorio;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReporteCorrectivoRepository extends JpaRepository<ReporteCorrectivo, Integer> {
    
    Optional<ReporteCorrectivo> findBySolicitud(Solicitud solicitud);
    
    @Query("SELECT r FROM ReporteCorrectivo r WHERE r.solicitud.id = :solicitudId")
    Optional<ReporteCorrectivo> findBySolicitudId(@Param("solicitudId") Integer solicitudId);
    
    boolean existsBySolicitudId(Integer solicitudId);
}