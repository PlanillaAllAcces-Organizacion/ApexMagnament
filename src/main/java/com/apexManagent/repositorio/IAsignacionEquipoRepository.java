package com.apexManagent.repositorio;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IAsignacionEquipoRepository extends JpaRepository<AsignacionEquipo, Integer> {
    
    // Buscar asignaciones por personal
    List<AsignacionEquipo> findByPersonalId(Integer personalId);
    
    // Verificar si un equipo está asignado
    boolean existsByEquipoId(Integer equipoId);
    
    // Verificar si un equipo está asignado a un personal específico
    boolean existsByPersonalIdAndEquipoId(Integer personalId, Integer equipoId);
    
    // Obtener asignaciones completas por personal
    @Query("SELECT a FROM AsignacionEquipo a WHERE a.personal.id = :personalId")
    List<AsignacionEquipo> findAsignacionesCompletasPorPersonal(@Param("personalId") Integer personalId);
    
    // Eliminar asignación por personal y equipo
    @Modifying
    @Query("DELETE FROM AsignacionEquipo a WHERE a.personal.id = :personalId AND a.equipo.id = :equipoId")
    void deleteByPersonalIdAndEquipoId(@Param("personalId") Integer personalId, 
                                     @Param("equipoId") Integer equipoId);
    
    // Buscar equipos asignados a un personal (usando join)
    @Query("SELECT a.equipo FROM AsignacionEquipo a WHERE a.personal.id = :personalId")
    List<Equipo> findEquiposAsignadosPorPersonal(@Param("personalId") Integer personalId);
}