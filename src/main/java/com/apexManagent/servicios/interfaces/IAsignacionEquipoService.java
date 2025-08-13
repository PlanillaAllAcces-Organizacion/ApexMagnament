package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.AsignacionEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IAsignacionEquipoService {
    AsignacionEquipo guardar(AsignacionEquipo asignacion);
    void eliminar(Integer id);
    Optional<AsignacionEquipo> buscarPorId(Integer id);
    Page<AsignacionEquipo> buscarTodas(Pageable pageable);
    boolean existePorPersonalYEquipo(Integer personalId, Integer equipoId);
}