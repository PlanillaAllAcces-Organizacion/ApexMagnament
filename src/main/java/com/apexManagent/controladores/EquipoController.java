package com.apexManagent.controladores;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/equipos")
public class EquipoController {

    @GetMapping
    public String listarEquipos(Model model) {
        // tu lógica aquí
        return "equipos/index"; // Asegúrate que coincida con la estructura real
    }
}