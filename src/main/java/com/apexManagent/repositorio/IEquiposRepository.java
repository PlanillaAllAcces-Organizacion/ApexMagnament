package com.apexManagent.repositorio;

import com.apexManagent.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IEquiposRepository extends JpaRepository<Equipo, Integer> {

    // Método existente
    boolean existsByNserie(String nserie);
    
    // Método existente
    Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
        String nserie, String nombre, String modelo, Pageable pageable);
    
    // Método existente
    @Query("SELECT e FROM Equipo e WHERE NOT EXISTS (SELECT a FROM AsignacionEquipo a WHERE a.equipo.id = e.id)")
    List<Equipo> findEquiposDisponibles();
    
    // Nuevo método: Búsqueda paginada con filtros para equipos disponibles
    @Query("SELECT e FROM Equipo e WHERE " +
           "NOT EXISTS (SELECT a FROM AsignacionEquipo a WHERE a.equipo.id = e.id) AND " +
           "(:nombre IS NULL OR e.nombre LIKE %:nombre%) AND " +
           "(:ubicacion IS NULL OR e.ubicacion LIKE %:ubicacion%) AND " +
           "(:nserie IS NULL OR e.nserie LIKE %:nserie%)")
    Page<Equipo> findEquiposDisponiblesConFiltros(
            @Param("nombre") String nombre,
            @Param("ubicacion") String ubicacion,
            @Param("nserie") String nserie,
            Pageable pageable);
    
    // Método existente
    @Query("SELECT e FROM Equipo e WHERE EXISTS (SELECT a FROM AsignacionEquipo a WHERE a.equipo.id = e.id AND a.personal.id = :personalId)")
    List<Equipo> findEquiposAsignadosAPersonal(@Param("personalId") Integer personalId);
    
    // Nuevo método: Obtener equipos por lista de IDs
    @Query("SELECT e FROM Equipo e WHERE e.id IN :ids")
    List<Equipo> findAllByIds(@Param("ids") List<Integer> ids);
}