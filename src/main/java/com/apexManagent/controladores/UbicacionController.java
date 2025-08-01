package com.apexManagent.controladores;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ubicacion")
public class UbicacionController {

    @GetMapping
    public  String index() {
        return "ubicacion/index";
    }
    
}
