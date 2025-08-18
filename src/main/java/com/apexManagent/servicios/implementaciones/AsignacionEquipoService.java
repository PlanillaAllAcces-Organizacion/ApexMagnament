package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.repositorio.IAsignacionEquipoRepository;
import com.apexManagent.repositorio.IPersonalRepository;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionEquipoService implements IAsignacionEquipoService {

    @Autowired
    private IAsignacionEquipoRepository asignacionRepository;

    @Autowired
    private IPersonalRepository personalRepository;


    @Override
    public Page<Equipo> obtenerEquiposDelUsuarioAutenticado(String nserie, String nombre, String model,
            Pageable pageable) {

        // Usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar personal
        Personal personal = personalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar equipos asignados con filtro + paginación
        Page<AsignacionEquipo> asignaciones = asignacionRepository
                .findByPersonalAndEquipo_NserieContainingAndEquipo_NombreContainingAndEquipo_ModeloContaining(personal,
                        nserie, nombre, model, pageable);

        // Transformar AsignacionEquipo -> Equipo
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
        int eliminados = asignacionRepository.eliminarAsignacion(personalId, equipoId);
        if (eliminados == 0) {
            throw new IllegalArgumentException("No se encontró la asignación especificada");
        }
    }
}