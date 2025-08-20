package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.Personal;
import com.apexManagent.modelos.Solicitud;
import com.apexManagent.repositorio.ISolicitudRepository;
import com.apexManagent.servicios.interfaces.IPersonalService;
import com.apexManagent.servicios.interfaces.ISolicitudService;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudService implements ISolicitudService {

    @Autowired
    private ISolicitudRepository solicitudRepository;

    @Autowired
    private IPersonalService personalService;

    @Override
    @Transactional(readOnly = true)
    public Page<Solicitud> obtenerSolicitudesPorEstado(short estado, Pageable pageable) {
        return solicitudRepository.findByEstado(estado, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Solicitud> obtenerSolicitudesPorEstadoYEmpleado(short estado, String nombreEmpleado,
            Pageable pageable) {
        return solicitudRepository.findByEstadoAndPersonalNombreContaining(estado, nombreEmpleado, pageable);
    }

    @Override
    public Page<Solicitud> obtenerLasSolicitudesDelUsuario(String nombre, String modelo, Date fecha,
            Pageable pageable) {
        Personal personal = personalService.getAuthenticatedPersonal();
        return solicitudRepository.findByPersonalWithFilters(personal, nombre, modelo, fecha, pageable);
    }

    @Override
    @Transactional
    public Solicitud obtenerPorId(Integer id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Override
    @Transactional
    public void cambiarEstado(Integer id, short estado) {
        Solicitud solicitud = obtenerPorId(id);
        if (solicitud != null) {
            solicitud.setEstado(estado);
            guardar(solicitud);
        }
    }
}