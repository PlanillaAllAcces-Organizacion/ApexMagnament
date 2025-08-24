package com.apexManagent.servicios.implementaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.apexManagent.repositorio.IReportePreventivoRepository;
import com.apexManagent.servicios.interfaces.IReportePreventivoService;
import com.apexManagent.modelos.ReportePreventivo;
import java.util.Optional;

@Service
public class ReportePreventivoService implements IReportePreventivoService {

    @Autowired
    private IReportePreventivoRepository reportePreventivoRepository;

    @Override
    public ReportePreventivo guardar(ReportePreventivo reportePreventivo) {
        return reportePreventivoRepository.save(reportePreventivo);
    }

    @Override
    public Optional<ReportePreventivo> obtenerPorId(Integer id) {
        return reportePreventivoRepository.findById(id);
    }

    @Override
    public Optional<ReportePreventivo> obtenerPorCalendarioId(Integer calendarioId) {
        return reportePreventivoRepository.findByCalendarioPreventivoId(calendarioId);
    }

    @Override
    public Page<ReportePreventivo> buscarReportes(String search, Short tipoMantenimiento, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return reportePreventivoRepository.buscarReportes(search, tipoMantenimiento, pageable);
        } else if (tipoMantenimiento != null) {
            return reportePreventivoRepository.findByTipoMantenimiento(tipoMantenimiento, pageable);
        } else {
            return reportePreventivoRepository.findAll(pageable);
        }
    }

    @Override
    public Page<ReportePreventivo> listarTodos(Pageable pageable) {
        return reportePreventivoRepository.findAll(pageable);
    }
}