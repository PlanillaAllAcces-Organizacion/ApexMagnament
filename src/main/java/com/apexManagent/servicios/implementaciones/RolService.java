package com.apexManagent.servicios.implementaciones;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Rol obtnerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre); 
    }

    @Override
    public Rol obtenerPorId(Integer id) {
        return rolRepository.findById(id).orElse(null);
    }
}