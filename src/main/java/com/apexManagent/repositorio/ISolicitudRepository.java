package com.apexManagent.repositorio;

import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    @Query("SELECT s FROM Solicitud s JOIN FETCH s.asignacionEquipo a JOIN FETCH a.equipo JOIN FETCH s.personal WHERE s.estado = :estado")
    Page<Solicitud> findByEstado(@Param("estado") short estado, Pageable pageable);
    
    @Query("SELECT s FROM Solicitud s JOIN FETCH s.asignacionEquipo a JOIN FETCH a.equipo JOIN FETCH s.personal " +
           "WHERE a.equipo.nombre LIKE %:search% OR s.personal.nombre LIKE %:search% OR s.descripcion LIKE %:search%")
    Page<Solicitud> search(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT s FROM Solicitud s JOIN FETCH s.asignacionEquipo a JOIN FETCH a.equipo JOIN FETCH s.personal " +
           "WHERE s.personal.id = :personalId")
    Page<Solicitud> findByPersonalId(@Param("personalId") Integer personalId, Pageable pageable);
}