package com.apexManagent.repositorio;

import com.apexManagent.modelos.AsignacionEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IAsignacionEquipoRepository extends JpaRepository<AsignacionEquipo, Integer> {
    
@Query("SELECT COUNT(a) > 0 FROM AsignacionEquipo a WHERE a.personal.id = :personalId AND a.equipo.id = :equipoId")
    boolean existsByPersonalIdAndEquipoId(@Param("personalId") Integer personalId, 
                                         @Param("equipoId") Integer equipoId);

}