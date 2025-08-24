package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.ReportePreventivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IReportePreventivoService {
    ReportePreventivo guardar(ReportePreventivo reportePreventivo);
    Optional<ReportePreventivo> obtenerPorId(Integer id);
    Optional<ReportePreventivo> obtenerPorCalendarioId(Integer calendarioId);
    Page<ReportePreventivo> buscarReportes(String search, Short tipoMantenimiento, Pageable pageable);
    Page<ReportePreventivo> listarTodos(Pageable pageable);
}