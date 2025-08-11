package com.apexManagent.servicios.interfaces;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.apexManagent.modelos.Personal;

public interface IPersonalService {

    Personal guardar(Personal personal);

    Optional<Personal> obtenerPorId(Integer id);

    Page<Personal> findByNombreContaining(String nombre, Pageable pageable);

    void eliminarPorId(Integer id);

    void actualizarRol(Integer personalId, Integer rolId);

    void modificarPersonal(Integer personalId, String nombre, String apellido, String email, String telefono);

    

}
