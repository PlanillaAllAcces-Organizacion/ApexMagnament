package com.apexManagent.servicios.implementaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.apexManagent.modelos.CalendarioPreventivo;
import com.apexManagent.repositorio.IPreventivoRepository;
import com.apexManagent.servicios.interfaces.IPreventivoService;

@Service
public class PreventivoService implements IPreventivoService {

    @Autowired
    public IPreventivoRepository preventivoRepository;

    @Override
    public CalendarioPreventivo guardar(CalendarioPreventivo calendarioPreventivo) {
        return preventivoRepository.save(calendarioPreventivo);

    }

    @Override
    public Page<CalendarioPreventivo> listarTodos(Pageable pageable) {
        return preventivoRepository.findAll(pageable);
    }

    @Override
    public Page<CalendarioPreventivo> listarPorEstado(short estado, Pageable pageable) {
        return preventivoRepository.findByEstadoMantenimiento(estado, pageable);
    }

}
