package com.apexManagent.repositorio;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IAsignacionEquipoRepository extends JpaRepository<AsignacionEquipo, Integer> {

       // Nuevo método en el repositorio
       AsignacionEquipo findByPersonalAndEquipoId(Personal personal, Integer equipoId);

       Page<AsignacionEquipo> findByPersonalAndEquipo_NserieContainingAndEquipo_NombreContainingAndEquipo_ModeloContaining(
                     Personal personal, String nserie, String nombre,
                     String model,
                     Pageable pageable);

       boolean existsByEquipoId(Integer equipoId);

       boolean existsByPersonalIdAndEquipoId(Integer personalId, Integer equipoId);

       @Query("SELECT a.equipo FROM AsignacionEquipo a WHERE a.personal.id = :personalId")
       List<Equipo> findEquiposAsignadosPorPersonal(@Param("personalId") Integer personalId);

       @Modifying
       @Transactional
       @Query("DELETE FROM AsignacionEquipo a WHERE a.personal.id = :personalId AND a.equipo.id = :equipoId")
       int eliminarAsignacion(@Param("personalId") Integer personalId, @Param("equipoId") Integer equipoId);

       @Query("SELECT e FROM Equipo e WHERE NOT EXISTS (SELECT a FROM AsignacionEquipo a WHERE a.equipo.id = e.id) " +
                     "AND (:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
                     "AND (:nserie IS NULL OR LOWER(e.nserie) LIKE LOWER(CONCAT('%', :nserie, '%'))) " +
                     "AND (:ubicacion IS NULL OR e.ubicacion.id = :ubicacion)")
       Page<Equipo> findEquiposDisponiblesConFiltros(
                     @Param("nombre") String nombre,
                     @Param("nserie") String nserie,
                     @Param("ubicacion") Integer ubicacion,
                     Pageable pageable);
}