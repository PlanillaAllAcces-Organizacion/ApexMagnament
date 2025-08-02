package com.apexManagent.servicios.interfaces;

import com.apexManagent.modelos.Ubicacion;
import java.util.List;
import java.util.Optional;

public interface IUbicacionService {
    Ubicacion guardarUbicacion(Ubicacion ubicacion);
    List<Ubicacion> listarTodas();
    Optional<Ubicacion> buscarPorId(Integer id);
    Optional<Ubicacion> buscarPorNombre(String nombreUbicacion);
    List<Ubicacion> buscarPorNombreConteniendo(String nombreUbicacion);
    void eliminarPorId(Integer id);
    boolean existePorNombre(String nombreUbicacion);
}