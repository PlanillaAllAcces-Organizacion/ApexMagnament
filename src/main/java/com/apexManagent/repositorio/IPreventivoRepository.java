package com.apexManagent.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.apexManagent.modelos.CalendarioPreventivo;

@Repository
public interface IPreventivoRepository extends JpaRepository<CalendarioPreventivo, Integer> {

        @EntityGraph(attributePaths = "equipo")
        Page<CalendarioPreventivo> findByEstadoMantenimiento(short estado, Pageable pageable);

@Query("SELECT cp FROM CalendarioPreventivo cp WHERE " +
           "(:search IS NULL OR LOWER(cp.equipo.nombre) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:estado IS NULL OR cp.estadoMantenimiento = :estado)")
    Page<CalendarioPreventivo> buscarCalendarios(@Param("search") String search, 
                                                @Param("estado") Short estado, 
                                                Pageable pageable);
}
