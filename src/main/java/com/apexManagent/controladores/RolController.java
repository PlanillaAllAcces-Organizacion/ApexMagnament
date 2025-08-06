package com.apexManagent.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apexManagent.modelos.Rol;
import com.apexManagent.servicios.implementaciones.RolService;

@Controller
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public String index() {
        return "rol/index";
    }

    @GetMapping("/create")
    public String create(Rol rol) {
        return "rol/create";
    }

}
