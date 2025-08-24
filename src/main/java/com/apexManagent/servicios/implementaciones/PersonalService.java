package com.apexManagent.servicios.implementaciones;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apexManagent.modelos.Personal;
import com.apexManagent.repositorio.IPersonalRepository;
import com.apexManagent.servicios.interfaces.IPersonalService;

@Service
public class PersonalService implements IPersonalService {

    @Autowired
    private IPersonalRepository personalRepository;

    public Personal getAuthenticatedPersonal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return personalRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    @Override
    public Personal guardar(Personal personal) {
        return personalRepository.save(personal);
    }

    @Override
    public Optional<Personal> obtenerPorId(Integer id) {
        return personalRepository.findById(id);
    }
      @Override
    public Optional<Personal> obtenerPorUsername(String Username) {
        return personalRepository.findByUsername(Username);
    }

    @Override
    public Page<Personal> findByNombreContainingAndApellidoContainingAndEmailContainingAndRol_NombreContaining(
            String nombre, String apellido, String email, String rolNombre, Pageable pageable) {
        return personalRepository.findByNombreContainingAndApellidoContainingAndEmailContainingAndRol_NombreContaining(
                nombre, apellido, email, rolNombre, pageable);
    }

    @Override
    public void eliminarPorId(Integer id) {
        personalRepository.deleteById(id);
    }

}
