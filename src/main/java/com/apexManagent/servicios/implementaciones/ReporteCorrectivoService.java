package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import com.apexManagent.repositorio.IReporteCorrectivoRepository;
import com.apexManagent.servicios.interfaces.IReporteCorrectivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReporteCorrectivoService implements IReporteCorrectivoService {

    @Autowired
    private IReporteCorrectivoRepository reporteCorrectivoRepository;

    @Override
    @Transactional
    public ReporteCorrectivo guardarReporte(ReporteCorrectivo reporte) {
        // Asegurar que la fecha se establece si no está presente
        if (reporte.getFechaCreacion() == null) {
            reporte.setFechaCreacion(java.time.LocalDateTime.now());
        }
        return reporteCorrectivoRepository.save(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteCorrectivo> obtenerPorSolicitud(Solicitud solicitud) {
        return reporteCorrectivoRepository.findBySolicitud(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteCorrectivo> obtenerPorSolicitudId(Integer solicitudId) {
        return reporteCorrectivoRepository.findBySolicitudId(solicitudId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeReporteParaSolicitud(Integer solicitudId) {
        return reporteCorrectivoRepository.existsBySolicitudId(solicitudId);
    }

    @Override
    @Transactional
    public ReporteCorrectivo crearReporteDesdeSolicitud(Solicitud solicitud, String observacion, 
                                                       Short tipoMantenimiento, Short estado) {
        ReporteCorrectivo reporte = new ReporteCorrectivo();
        reporte.setObservacion(observacion);
        reporte.setTipoMantenimiento(tipoMantenimiento);
        reporte.setEstado(estado);
        reporte.setSolicitud(solicitud);
        reporte.setPersonal(solicitud.getPersonal());
        
        return guardarReporte(reporte);
    }
}