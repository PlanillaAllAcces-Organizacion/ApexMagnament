package com.apexManagent.repositorio;

import com.apexManagent.modelos.Personal;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPersonalRepository extends JpaRepository<Personal, Integer> {

     Optional<Personal> findByUsername(String username);

     boolean existsByNombreOrTelefonoOrEmailOrUsername(String nombre, String telefono, String email, String username);

     Page<Personal> findByNombreContainingAndApellidoContainingAndEmailContainingAndRol_NombreContaining(String nombre,
               String apellido, String email, String rolNombre, Pageable pageable);

}
