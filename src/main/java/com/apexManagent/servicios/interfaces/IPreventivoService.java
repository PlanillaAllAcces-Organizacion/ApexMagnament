package com.apexManagent.servicios.interfaces;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apexManagent.modelos.CalendarioPreventivo;

public interface IPreventivoService {
CalendarioPreventivo guardar(CalendarioPreventivo calendarioPreventivo);
    Page<CalendarioPreventivo> listarTodos(Pageable pageable);
    Page<CalendarioPreventivo> listarPorEstado(short estado, Pageable pageable);
    Page<CalendarioPreventivo> buscarCalendarios(String search, Short estado, Pageable pageable);
    Optional<CalendarioPreventivo> obtenerPorId(Integer id);
    

}
