package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.repositorio.IAsignacionEquipoRepository;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AsignacionEquipoService implements IAsignacionEquipoService {

    @Autowired
    private IAsignacionEquipoRepository repository;

    @Override
    @Transactional
    public AsignacionEquipo guardar(AsignacionEquipo asignacion) {
        return repository.save(asignacion);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AsignacionEquipo> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsignacionEquipo> buscarTodas(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorPersonalYEquipo(Integer personalId, Integer equipoId) {
        return repository.existsByPersonalIdAndEquipoId(personalId, equipoId);
    }

    
}