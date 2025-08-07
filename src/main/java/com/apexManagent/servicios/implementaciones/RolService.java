package com.apexManagent.servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.apexManagent.modelos.Rol;
import com.apexManagent.servicios.interfaces.IRolService;
import com.apexManagent.repositorio.IRolRepository;


@Service
public class RolService implements IRolService {

    @Autowired
    private IRolRepository rolRepository; 

    @Override
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> obtenerPorId(Integer id) {
        return rolRepository.findById(id);
    }

    @Override
    public Page<Rol> findByNombreContaining(String nombre, Pageable pageable) {
        return rolRepository.findByNombreContaining(nombre, pageable);
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }


}
