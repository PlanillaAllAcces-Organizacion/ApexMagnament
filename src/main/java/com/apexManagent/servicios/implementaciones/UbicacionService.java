package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.Ubicacion;
import com.apexManagent.repositorio.IUbicacionRepository;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService implements IUbicacionService {

    private final IUbicacionRepository ubicacionRepository;

    @Autowired
    public UbicacionService(IUbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    @Transactional
    public Ubicacion guardarUbicacion(Ubicacion ubicacion) {
        if (ubicacion.getNombreUbicacion() == null || ubicacion.getNombreUbicacion().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ubicación es obligatorio");
        }
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ubicacion> listarTodas() {
        return ubicacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ubicacion> buscarPorId(Integer id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ubicacion> buscarPorNombre(String nombreUbicacion) {
        return ubicacionRepository.findByNombreUbicacion(nombreUbicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ubicacion> buscarPorNombreConteniendo(String nombreUbicacion) {
        return ubicacionRepository.findByNombreUbicacionContainingIgnoreCase(nombreUbicacion);
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        ubicacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombreUbicacion) {
        return ubicacionRepository.existsByNombreUbicacion(nombreUbicacion);
    }
}