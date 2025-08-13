package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.repositorio.IAsignacionEquipoRepository;
import com.apexManagent.repositorio.IEquiposRepository;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AsignacionEquipoService implements IAsignacionEquipoService {

    @Autowired
    private IAsignacionEquipoRepository asignacionRepository;
    
    @Autowired
    private IEquiposRepository equipoRepository;

    @Override
    @Transactional
    public AsignacionEquipo crearAsignacion(Personal personal, Equipo equipo) {
        if (equipoEstaAsignado(equipo.getId())) {
            throw new IllegalStateException("El equipo ya está asignado a otro personal");
        }
        
        AsignacionEquipo asignacion = new AsignacionEquipo(personal, equipo);
        return asignacionRepository.save(asignacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> obtenerEquiposAsignados(Integer personalId) {
        return asignacionRepository.findEquiposAsignadosPorPersonal(personalId);
    }

    @Override
    @Transactional
    public void eliminarAsignacion(Integer id) {
        asignacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionEquipo> obtenerAsignacionesPorPersonal(Integer personalId) {
        return asignacionRepository.findAsignacionesCompletasPorPersonal(personalId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean equipoEstaAsignado(Integer equipoId) {
        return asignacionRepository.existsByEquipoId(equipoId);
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionEquipo obtenerPorId(Integer id) {
        return asignacionRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
    }

    @Override
    @Transactional
    public void eliminarAsignacionPorPersonalYEquipo(Integer personalId, Integer equipoId) {
        asignacionRepository.deleteByPersonalIdAndEquipoId(personalId, equipoId);
    }

    @Override
    @Transactional
    public void asignarMultiplesEquipos(Personal personal, List<Equipo> equipos) {
        equipos.forEach(equipo -> {
            if (!equipoEstaAsignado(equipo.getId())) {
                crearAsignacion(personal, equipo);
            }
        });
    }
}