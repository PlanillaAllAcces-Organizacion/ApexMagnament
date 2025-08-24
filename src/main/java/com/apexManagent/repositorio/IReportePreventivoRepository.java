package com.apexManagent.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.apexManagent.modelos.ReportePreventivo;
import java.util.Optional;

public interface IReportePreventivoRepository extends JpaRepository<ReportePreventivo, Integer> {
    Optional<ReportePreventivo> findByCalendarioPreventivoId(Integer calendarioId);
    
    Page<ReportePreventivo> findByTipoMantenimiento(Short tipoMantenimiento, Pageable pageable);
    
    @Query("SELECT r FROM ReportePreventivo r WHERE " +
           "(r.calendarioPreventivo.equipo.nombre LIKE %:search% OR r.observacion LIKE %:search%) " +
           "AND (:tipoMantenimiento IS NULL OR r.tipoMantenimiento = :tipoMantenimiento)")
    Page<ReportePreventivo> buscarReportes(@Param("search") String search, 
                                          @Param("tipoMantenimiento") Short tipoMantenimiento, 
                                          Pageable pageable);
}