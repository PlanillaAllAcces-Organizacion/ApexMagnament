package com.apexManagent.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.apexManagent.modelos.CalendarioPreventivo;

@Repository
public interface IPreventivoRepository extends JpaRepository<CalendarioPreventivo, Integer> {

        @EntityGraph(attributePaths = "equipo")
        Page<CalendarioPreventivo> findByEstadoMantenimiento(short estado, Pageable pageable);


}
