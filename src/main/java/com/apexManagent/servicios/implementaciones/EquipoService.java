package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.repositorio.IEquiposRepository;
import com.apexManagent.servicios.interfaces.IEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService implements IEquipoService {

    @Autowired
    private IEquiposRepository equiposRepository;

    @Autowired
    public EquipoService(IEquiposRepository equiposRepository) {
        this.equiposRepository = equiposRepository;
    }

    @Override
    @Transactional
    public Equipo guardarEquipo(Equipo equipo) {
        if (equipo.getNserie() == null || equipo.getNserie().isEmpty()) {
            throw new IllegalArgumentException("El número de serie es obligatorio");
        }
        return equiposRepository.save(equipo);
    }

  

    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> buscarTodosPaginados(Pageable pageable) {
        return equiposRepository.findAll(pageable);
    }

     @Override
    public List<Equipo> obtenerTodos() {
        return equiposRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> buscarPorId(Integer id) {
        return equiposRepository.findById(id);
    }

    @Override
    public Equipo createOrEditOne(Equipo equipo) {
        return equiposRepository.save(equipo);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<Equipo> buscarPorNserie(String nserie) {
        return equiposRepository.findByNserie(nserie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> buscarPorNombre(String nombre) {
        return equiposRepository.findByNombreContainingIgnoreCase(nombre);
    }

   
    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> buscarPorNombreModeloOSerie(String filtro, Pageable pageable) {
        return equiposRepository.findByNombreContainingIgnoreCaseOrModeloContainingIgnoreCaseOrNserieContainingIgnoreCase(
            filtro, filtro, filtro, pageable);
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        equiposRepository.deleteById(id);
    }

    
    @Override
    @Transactional
    public void actualizarUbicacion(Integer equipoId, Integer ubicacionId) {
        equiposRepository.actualizarUbicacion(equipoId, ubicacionId);
    }

    @Override
    @Transactional
    public void modificarEquipo(Integer equipoId, String nombre, String modelo, String descripcion) {
        equiposRepository.modificarEquipo(equipoId, nombre, modelo, descripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNserie(String nserie) {
        return equiposRepository.existsByNserie(nserie);
    }

   
    @Override
    public Optional<Equipo> buscarSerie(String nserie) {
     return equiposRepository.findByNserie(nserie);   
    }

   

    
}