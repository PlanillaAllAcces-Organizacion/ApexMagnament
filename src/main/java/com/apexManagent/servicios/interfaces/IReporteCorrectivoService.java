package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import java.util.Optional;

public interface IReporteCorrectivoService {
    
    ReporteCorrectivo guardarReporte(ReporteCorrectivo reporte);
    
    Optional<ReporteCorrectivo> obtenerPorSolicitud(Solicitud solicitud);
    
    Optional<ReporteCorrectivo> obtenerPorSolicitudId(Integer solicitudId);
    
    boolean existeReporteParaSolicitud(Integer solicitudId);
    
    ReporteCorrectivo crearReporteDesdeSolicitud(Solicitud solicitud, String observacion, 
                                                Short tipoMantenimiento, Short estado); // Cambiado a Short
}