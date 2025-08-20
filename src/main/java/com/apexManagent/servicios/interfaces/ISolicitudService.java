package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISolicitudService {
    Page<Solicitud> obtenerSolicitudesPorEstado(short estado, Pageable pageable);
    Page<Solicitud> obtenerSolicitudesPorEstadoYEmpleado(short estado, String nombreEmpleado, Pageable pageable);
    Solicitud obtenerPorId(Integer id);
    Solicitud guardar(Solicitud solicitud);
    void cambiarEstado(Integer id, short estado);
}   