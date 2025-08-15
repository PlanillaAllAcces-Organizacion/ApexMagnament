package com.apexManagent.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @GetMapping
    public String index() {
        return "empleado/index";
    }

    @GetMapping("/miEquipo")
    public String miEquipo(){
        return "empleado/miEquipo";
    }

}
