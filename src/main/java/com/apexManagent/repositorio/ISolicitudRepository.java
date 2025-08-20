package com.apexManagent.repositorio;

import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // Buscar por estado (0=Pendiente, 1=Aceptado, 2=Rechazado, 3=Finalizado)
    Page<Solicitud> findByEstado(short estado, Pageable pageable);
    
    // Buscar por estado + nombre del empleado
    Page<Solicitud> findByEstadoAndPersonalNombreContaining(
            short estado, String nombreEmpleado, Pageable pageable);
}