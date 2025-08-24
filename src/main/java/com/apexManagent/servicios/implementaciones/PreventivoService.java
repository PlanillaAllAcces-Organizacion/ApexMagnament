package com.apexManagent.servicios.implementaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.apexManagent.repositorio.IPreventivoRepository;
import com.apexManagent.servicios.interfaces.IPreventivoService;
import com.apexManagent.modelos.CalendarioPreventivo;
import java.util.Optional;

@Service
public class PreventivoService implements IPreventivoService {

    @Autowired
    private IPreventivoRepository preventivoRepository;

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

    @Override
    public Page<CalendarioPreventivo> buscarCalendarios(String search, Short estado, Pageable pageable) {
        return preventivoRepository.buscarCalendarios(search, estado, pageable);
    }

    @Override
    public Optional<CalendarioPreventivo> obtenerPorId(Integer id) {
        return preventivoRepository.findById(id);
    }
}