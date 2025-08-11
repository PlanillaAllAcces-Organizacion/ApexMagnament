package com.apexManagent.servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.apexManagent.modelos.Personal;
import com.apexManagent.modelos.Rol;
import com.apexManagent.repositorio.IPersonalRepository;
import com.apexManagent.servicios.interfaces.IPersonalService;

@Service
public class PersonalService implements IPersonalService {

    @Autowired
    private IPersonalRepository personalRepository;

    @Override
    public Personal guardar(Personal personal) {
        return personalRepository.save(personal);
    }

    @Override
    public Optional<Personal> obtenerPorId(Integer id) {
        return personalRepository.findById(id);
    }

    @Override
    public Page<Personal> findByNombreContaining(String nombre, Pageable pageable) {
        return personalRepository.findByNombreContaining(nombre, pageable);
    }

    @Override
    public void eliminarPorId(Integer id) {
        personalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void actualizarRol(Integer personalId, Integer rolId) {
        personalRepository.actualizarRol(personalId, rolId);
    }

    @Override
    @Transactional
    public void modificarPersonal(Integer personalId, String nombre, String apellido, String email, String telefono) {
        personalRepository.modificarPersonal(personalId, nombre, apellido, email, telefono);
    }

    



   


}
