package com.apexManagent.servicios.implementaciones;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.repositorio.IEquiposRepository;
import com.apexManagent.servicios.interfaces.IEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EquipoService implements IEquipoService {

    @Autowired
    private IEquiposRepository equipoRepository;

    @Override
    public Equipo guardar(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    @Override
    public Optional<Equipo> obtenerPorId(Integer id) {
        return equipoRepository.findById(id);
    }

    @Override
    public void eliminarPorId(Integer id) {
        equipoRepository.deleteById(id);
    }

    @Override
    public boolean existePorNserie(String nserie) {
        return equipoRepository.existsByNserie(nserie);
    }

    @Override
    public Page<Equipo> findByNserieContainingAndNombreContainingAndModeloContaining(
            String nserie, String nombre, String modelo, Pageable pageable) {
        return equipoRepository.findByNserieContainingAndNombreContainingAndModeloContaining(
                nserie, nombre, modelo, pageable);
    }
}