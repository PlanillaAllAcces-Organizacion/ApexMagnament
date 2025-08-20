package com.apexManagent.repositorio;

import com.apexManagent.modelos.Personal;
import com.apexManagent.modelos.Solicitud;

import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends JpaRepository<Solicitud, Integer> {

       // Buscar por estado (0=Pendiente, 1=Aceptado, 2=Rechazado, 3=Finalizado)
       Page<Solicitud> findByEstado(short estado, Pageable pageable);

       // Buscar por estado + nombre del empleado
       Page<Solicitud> findByEstadoAndPersonalNombreContaining(
                     short estado, String nombreEmpleado, Pageable pageable);

       @Query("SELECT s FROM Solicitud s " +
                     "WHERE s.personal = :personal " +
                     "AND (:nombre IS NULL OR s.asignacionEquipo.equipo.nombre LIKE %:nombre%) " +
                     "AND (:modelo IS NULL OR s.asignacionEquipo.equipo.modelo LIKE %:modelo%) " +
                     "AND (:fecha IS NULL OR CAST(s.fechaRegistro AS DATE) = :fecha)")
       Page<Solicitud> findByPersonalWithFilters(
                     @Param("personal") Personal personal,
                     @Param("nombre") String nombre,
                     @Param("modelo") String modelo,
                     @Param("fecha") Date fecha,
                     Pageable pageable);
}