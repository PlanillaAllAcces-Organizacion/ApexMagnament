package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IPersonalService;
import com.apexManagent.servicios.interfaces.IUbicacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionEquipoController {

    @Autowired
    private IAsignacionEquipoService asignacionService;

    @Autowired
    private IUbicacionService ubicacionService;
    
    @Autowired
    private IPersonalService personalService;
@GetMapping("/asignar/{personalId}")
public String mostrarFormularioAsignacion(
        @PathVariable Integer personalId,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String nserie,
        @RequestParam(required = false) Integer ubicacion,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {
    
    Personal personal = personalService.obtenerPorId(personalId)
            .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
    
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("nombre").ascending());
    
    List<Equipo> equiposAsignados = asignacionService.obtenerEquiposAsignados(personalId);
    Page<Equipo> equiposDisponibles = asignacionService.buscarEquiposDisponibles(nombre, nserie, ubicacion, pageable);
    
    // Agregar la lista de ubicaciones al modelo
    model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());
    model.addAttribute("personal", personal);
    model.addAttribute("equiposAsignados", equiposAsignados);
    model.addAttribute("equiposDisponibles", equiposDisponibles);
    
    int totalPages = equiposDisponibles.getTotalPages();
    if (totalPages > 0) {
        List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);
    }
    
    return "personal/asignacion";
}

    @PostMapping("/asignar-equipo")
    public String asignarEquipo(
            @RequestParam Integer personalId,
            @RequestParam Integer equipoId,
            RedirectAttributes redirectAttributes) {
        
        Personal personal = personalService.obtenerPorId(personalId)
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
        
        asignacionService.asignarEquipo(personal, equipoId);
        
        redirectAttributes.addFlashAttribute("success", "Equipo asignado correctamente");
        return "redirect:/asignaciones/asignar/" + personalId;
    }

   @GetMapping("/desasignar/{personalId}/{equipoId}")
public String desasignarEquipo(
        @PathVariable Integer personalId,
        @PathVariable Integer equipoId,
        RedirectAttributes redirectAttributes) {
    
    try {
        asignacionService.desasignarEquipo(personalId, equipoId);
        redirectAttributes.addFlashAttribute("success", "Equipo desasignado correctamente");
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/asignaciones/asignar/" + personalId;
}
}