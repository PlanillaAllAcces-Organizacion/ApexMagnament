package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface IReporteCorrectivoService {

    Page<ReporteCorrectivo> buscarReportes(String search, Short tipoMantenimiento,
            org.springframework.data.domain.Pageable pageable);

    ReporteCorrectivo guardarReporte(ReporteCorrectivo reporte);

    Optional<ReporteCorrectivo> obtenerPorSolicitud(Solicitud solicitud);

    Optional<ReporteCorrectivo> obtenerPorSolicitudId(Integer solicitudId);

    boolean existeReporteParaSolicitud(Integer solicitudId);

    ReporteCorrectivo crearReporteDesdeSolicitud(Solicitud solicitud, String observacion,
            Short tipoMantenimiento, Short estado); // Cambiado a Short

    List<ReporteCorrectivo> findAll();

}