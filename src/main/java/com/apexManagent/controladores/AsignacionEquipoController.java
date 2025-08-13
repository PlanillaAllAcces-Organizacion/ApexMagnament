package com.apexManagent.controladores;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionEquipoController {

    @Autowired
    private IAsignacionEquipoService asignacionService;
    
    @Autowired
    private IPersonalService personalService;
    
    @Autowired
    private IEquipoService equipoService;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        
        Page<AsignacionEquipo> asignaciones = asignacionService.buscarTodas(PageRequest.of(page-1, size));
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("equipos", equipoService.obtenerTodos());
        return "asignaciones/index";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute AsignacionEquipo asignacion,
                         RedirectAttributes attributes) {
        
        if(asignacionService.existePorPersonalYEquipo(
                asignacion.getPersonal().getId(), 
                asignacion.getEquipo().getId())) {
            attributes.addFlashAttribute("error", "Esta asignación ya existe");
        } else {
            asignacionService.guardar(asignacion);
            attributes.addFlashAttribute("msg", "Asignación registrada correctamente");
        }
        return "redirect:/asignaciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes attributes) {
        asignacionService.eliminar(id);
        attributes.addFlashAttribute("msg", "Asignación eliminada correctamente");
        return "redirect:/asignaciones";
    }
}