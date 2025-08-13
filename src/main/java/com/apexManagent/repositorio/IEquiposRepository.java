package com.apexManagent.repositorio;

import com.apexManagent.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEquiposRepository extends JpaRepository<Equipo, Integer> {

    Optional<Equipo> findByNserie(String nserie);
    
    // Búsquedas individuales paginadas
    Page<Equipo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Equipo> findByModeloContainingIgnoreCase(String modelo, Pageable pageable);
    Page<Equipo> findByNserieContainingIgnoreCase(String nserie, Pageable pageable);
    
    // Búsqueda combinada (opcional)
    Page<Equipo> findByNombreContainingIgnoreCaseOrModeloContainingIgnoreCaseOrNserieContainingIgnoreCase(
        String nombre, String modelo, String nserie, Pageable pageable);
    
    boolean existsByNserie(String nserie);
    
    @Modifying
    @Query("DELETE FROM Equipo e WHERE e.nserie = :nserie")
    void deleteByNserie(@Param("nserie") String nserie);

    @Modifying
    @Query("UPDATE Equipo e SET e.ubicacion.id = :ubicacionId WHERE e.id = :equipoId") 
    void actualizarUbicacion(@Param("equipoId") Integer equipoId, 
                           @Param("ubicacionId") Integer ubicacionId);

    @Modifying
    @Query("UPDATE Equipo e SET e.nombre = :nombre, e.modelo = :modelo, e.descripcion = :descripcion WHERE e.id = :equipoId") 
    void modificarEquipo(@Param("equipoId") Integer equipoId,
                       @Param("nombre") String nombre,
                       @Param("modelo") String modelo, 
                       @Param("descripcion") String descripcion);

                       
}