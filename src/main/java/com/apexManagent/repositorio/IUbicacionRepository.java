package com.apexManagent.repositorio;

import com.apexManagent.modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    
    Optional<Ubicacion> findByNombreUbicacion(String nombreUbicacion);
    
    List<Ubicacion> findByNombreUbicacionContainingIgnoreCase(String nombreUbicacion);
    
    boolean existsByNombreUbicacion(String nombreUbicacion);
}