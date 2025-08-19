package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISolicitudService {
    Page<Solicitud> obtenerSolicitudesPendientes(Pageable pageable);

    Page<Solicitud> buscarSolicitudes(String search, Pageable pageable);

    Page<Solicitud> obtenerSolicitudesPorPersonal(Integer personalId, Pageable pageable);

    Solicitud obtenerPorId(Integer id);

    Solicitud guardar(Solicitud solicitud);

    void cambiarEstado(Integer id, short estado);
}