package com.apexManagent.servicios.implementaciones;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.apexManagent.modelos.Ubicacion;
import com.apexManagent.repositorio.IUbicacionRepository;
import com.apexManagent.servicios.interfaces.IUbicacionService;

@Service
public class UbicacionService implements IUbicacionService {

    @Autowired
    private IUbicacionRepository ubicacionRepository;

    @Override
    public Page<Ubicacion> buscarTodosPaginados(Pageable pageable) {
        return ubicacionRepository.findAll(pageable);
    }

    @Override
    public List<Ubicacion> obtenerTodos() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Optional<Ubicacion> obtenerPorId(Integer id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    public Page<Ubicacion> findByNombreUbicacionContaining(String nombreUbicacion, Pageable pageable) {
        return ubicacionRepository.findByNombreUbicacionContaining(nombreUbicacion, pageable);
    }

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }


}
