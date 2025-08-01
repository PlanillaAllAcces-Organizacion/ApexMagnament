package com.apexManagent.servicios.interfaces;
import com.apexManagent.modelos.Rol;

import java.util.List;

public interface IRolService{

    List<Rol> obtenerTodos();

    Rol obtenerPorId(Integer id);

    Rol obtnerPorNombre (String nombre);
}