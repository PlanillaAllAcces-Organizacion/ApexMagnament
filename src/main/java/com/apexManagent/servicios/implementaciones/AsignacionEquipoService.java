package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.repositorio.IAsignacionEquipoRepository;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IPersonalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionEquipoService implements IAsignacionEquipoService {

    @Autowired
    private IAsignacionEquipoRepository asignacionRepository;

    @Autowired
    private IPersonalService personalService; 

    @Override
    public AsignacionEquipo obtenerAsignacionDelUsuarioAutenticadoYEquipo(Integer equipoId) {
        Personal personal = personalService.getAuthenticatedPersonal(); 
        return asignacionRepository.findByPersonalAndEquipoId(personal, equipoId);
    }

    @Override
    public Page<Equipo> obtenerEquiposDelUsuarioAutenticado(String nserie, String nombre, String modelo, Pageable pageable) {
        Personal personal = personalService.getAuthenticatedPersonal(); 
        
        Page<AsignacionEquipo> asignaciones = asignacionRepository
                .findByPersonalAndEquipo_NserieContainingAndEquipo_NombreContainingAndEquipo_ModeloContaining(
                        personal, nserie, nombre, modelo, pageable);

        return asignaciones.map(AsignacionEquipo::getEquipo);
    }

    @Override
    @Transactional
    public AsignacionEquipo crearAsignacion(Personal personal, Equipo equipo) {
        if (asignacionRepository.existsByEquipoId(equipo.getId())) {
            throw new IllegalStateException("El equipo ya está asignado a otro personal");
        }

        AsignacionEquipo asignacion = new AsignacionEquipo();
        asignacion.setPersonal(personal);
        asignacion.setEquipo(equipo);
        return asignacionRepository.save(asignacion);
    }

    @Override
    @Transactional
    public void asignarEquipo(Personal personal, Integer equipoId) {
        if (!asignacionRepository.existsByEquipoId(equipoId)) {
            Equipo equipo = new Equipo();
            equipo.setId(equipoId);
            crearAsignacion(personal, equipo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> obtenerEquiposAsignados(Integer personalId) {
        return asignacionRepository.findEquiposAsignadosPorPersonal(personalId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> buscarEquiposDisponibles(String nombre, String nserie, Integer ubicacion, Pageable pageable) {
        return asignacionRepository.findEquiposDisponiblesConFiltros(nombre, nserie, ubicacion, pageable);
    }

     @Override
    @Transactional
    public void desasignarEquipo(Integer personalId, Integer equipoId) {
        if (!asignacionRepository.existsByPersonalIdAndEquipoId(personalId, equipoId)) {
            throw new IllegalArgumentException("No se encontró la asignación especificada para desasignar");
        }
        asignacionRepository.deleteByPersonalIdAndEquipoId(personalId, equipoId);
    }
}