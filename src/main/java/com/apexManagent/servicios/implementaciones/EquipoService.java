package com.apexManagent.servicios.implementaciones;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.repositorio.IEquiposRepository;
import com.apexManagent.servicios.interfaces.IEquipoService;


@Service
public class EquipoService implements IEquipoService {

    @Autowired
    private IEquiposRepository equipoRepository;


    @Override
    @Transactional
    public Equipo guardar(Equipo equipo) {
        if (equipo.getId() == null && equipoRepository.existsByNserie(equipo.getNserie())) {
            throw new IllegalArgumentException("Ya existe un equipo con este número de serie");
        }
        return equipoRepository.save(equipo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> obtenerPorId(Integer id) {
        return equipoRepository.findById(id);
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        equipoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNserie(String nserie) {
        return equipoRepository.existsByNserie(nserie);
    }

  
    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
            String nserie, String nombre, String modelo, Pageable pageable) {
        return equipoRepository.findByNserieContainingAndNombreContainingAndModeloContaining(
                nserie, nombre, modelo, pageable);
    }

    
}