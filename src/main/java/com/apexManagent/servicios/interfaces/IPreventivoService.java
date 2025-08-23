package com.apexManagent.servicios.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apexManagent.modelos.CalendarioPreventivo;

public interface IPreventivoService {

    CalendarioPreventivo guardar(CalendarioPreventivo calendarioPreventivo);

    Page<CalendarioPreventivo> listarTodos(Pageable pageable);

    Page<CalendarioPreventivo> listarPorEstado(short estado, Pageable pageable);

    

}
