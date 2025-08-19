package com.apexManagent.servicios.interfaces;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.apexManagent.modelos.Personal;

public interface IPersonalService {

    Personal getAuthenticatedPersonal();

    Personal guardar(Personal personal);

    Optional<Personal> obtenerPorId(Integer id);

    Page<Personal> findByNombreContainingAndApellidoContainingAndEmailContainingAndRol_NombreContaining(String nombre,
            String apellido, String email, String rolNombre, Pageable pageable);

    void eliminarPorId(Integer id);

}
