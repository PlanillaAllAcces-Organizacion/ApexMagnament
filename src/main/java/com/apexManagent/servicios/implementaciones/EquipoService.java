package com.apexManagent.servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.repositorio.IAsignacionEquipoRepository;
import com.apexManagent.repositorio.IEquiposRepository;
import com.apexManagent.servicios.interfaces.IEquipoService;


@Service
public class EquipoService implements IEquipoService {

    @Autowired
    private IEquiposRepository equipoRepository;

    @Autowired
    private IAsignacionEquipoRepository asignacionRepository;

    @Override
    public Equipo guardar(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    @Override
    public Optional<Equipo> obtenerPorId(Integer id) {
        return equipoRepository.findById(id);
    }

    @Override
    public void eliminarPorId(Integer id) {
        equipoRepository.deleteById(id);
    }

    @Override
    public boolean existePorNserie(String nserie) {
        return equipoRepository.existsByNserie(nserie);
    }

    @Override
    public Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
            String nserie, String nombre, String modelo, Pageable pageable) {
        return equipoRepository.findByNserieContainingAndNombreContainingAndModeloContaining(
                nserie, nombre, modelo, pageable);
    }

     @Override
    @Transactional(readOnly = true)
    public List<Equipo> findAllWithAsignacionStatus() {
        List<Equipo> equipos = equipoRepository.findAll();
        equipos.forEach(equipo -> 
            equipo.setAsignado(asignacionRepository.existsByEquipoId(equipo.getId()))
        );
        return equipos;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Equipo> findEquiposDisponibles() {
        return equipoRepository.findEquiposDisponibles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> findEquiposAsignadosAPersonal(Integer personalId) {
        return equipoRepository.findEquiposAsignadosAPersonal(personalId);
    }
}