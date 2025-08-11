package com.apexManagent.repositorio;

import com.apexManagent.modelos.Personal;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IPersonalRepository extends JpaRepository<Personal, Integer> {

     Optional<Personal> findByUsername(String username);

    boolean existsByNombreOrTelefonoOrEmailOrUsername(String nombre, String telefono, String email, String username);

    Page<Personal> findByNombreContaining(String nombre, Pageable pageable);

    @Modifying
    @Query("UPDATE Personal e SET e.rol.id = :rolId WHERE e.id = :personalId") 
    void actualizarRol(@Param("personalId") Integer personalId, 
                           @Param("rolId") Integer rolId);

    @Modifying
    @Query("UPDATE Personal p SET p.nombre = :nombre, p.apellido = :apellido, p.email = :emial, p.telefono = :telefono WHERE p.id = :personalId") 
    void modificarPersonal(@Param("personalId") Integer personalId,
                       @Param("nombre") String nombre,
                       @Param("apellido") String apellido, 
                       @Param("email") String email,
                       @Param("telefono") String telefono);

}
