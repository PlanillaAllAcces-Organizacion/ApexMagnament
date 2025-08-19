package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAsignacionEquipoService {

    AsignacionEquipo obtenerAsignacionDelUsuarioAutenticadoYEquipo(Integer equipoId);

    Page<Equipo> obtenerEquiposDelUsuarioAutenticado(String nserie, String nombre, String model, Pageable pageable);

    AsignacionEquipo crearAsignacion(Personal personal, Equipo equipo);

    void asignarEquipo(Personal personal, Integer equipoId);

    List<Equipo> obtenerEquiposAsignados(Integer personalId);

    Page<Equipo> buscarEquiposDisponibles(String nombre, String nserie, Integer ubicacion, Pageable pageable);

    void desasignarEquipo(Integer personalId, Integer equipoId);
}