package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IReporteCorrectivoService {

    Page<ReporteCorrectivo> buscarReportes(String search, Short tipoMantenimiento, Pageable pageable);

    ReporteCorrectivo guardarReporte(ReporteCorrectivo reporte);

    Optional<ReporteCorrectivo> obtenerPorSolicitud(Solicitud solicitud);

    Optional<ReporteCorrectivo> obtenerPorSolicitudId(Integer solicitudId);

    boolean existeReporteParaSolicitud(Integer solicitudId);

    ReporteCorrectivo crearReporteDesdeSolicitud(Solicitud solicitud, String observacion,
            Short tipoMantenimiento, Short estado);

    List<ReporteCorrectivo> findAll();
}