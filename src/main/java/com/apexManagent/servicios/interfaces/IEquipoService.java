package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IEquipoService {
    Equipo guardar(Equipo equipo);
    Optional<Equipo> obtenerPorId(Integer id);
    void eliminarPorId(Integer id);
    boolean existePorNserie(String nserie);
    
    Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
        String nserie, String nombre, String modelo, Pageable pageable);
}