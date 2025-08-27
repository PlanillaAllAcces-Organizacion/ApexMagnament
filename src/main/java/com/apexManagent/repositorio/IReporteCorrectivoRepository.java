package com.apexManagent.repositorio;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReporteCorrectivoRepository extends JpaRepository<ReporteCorrectivo, Integer> {
    
    Optional<ReporteCorrectivo> findBySolicitud(Solicitud solicitud);
    
    Optional<ReporteCorrectivo> findBySolicitudId(Integer solicitudId);
    
    boolean existsBySolicitudId(Integer solicitudId);

    Page<ReporteCorrectivo> findByPersonal_NombreContainingIgnoreCaseOrPersonal_ApellidoContainingIgnoreCaseAndTipoMantenimiento(
        String nombre, String apellido, Short tipoMantenimiento, Pageable pageable);
}