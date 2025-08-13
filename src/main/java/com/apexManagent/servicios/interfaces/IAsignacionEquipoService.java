package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import java.util.List;

public interface IAsignacionEquipoService {
    AsignacionEquipo crearAsignacion(Personal personal, Equipo equipo);
    void eliminarAsignacion(Integer id);
    List<AsignacionEquipo> obtenerAsignacionesPorPersonal(Integer personalId);
    List<Equipo> obtenerEquiposAsignados(Integer personalId);
    boolean equipoEstaAsignado(Integer equipoId);
    AsignacionEquipo obtenerPorId(Integer id);
    void eliminarAsignacionPorPersonalYEquipo(Integer personalId, Integer equipoId);
    void asignarMultiplesEquipos(Personal personal, List<Equipo> equipos);
}