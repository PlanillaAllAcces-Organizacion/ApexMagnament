package com.apexManagent.controladores;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IPersonalService;
import com.apexManagent.repositorio.IEquiposRepository;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IEquiposRepository equipoRepository;

 @GetMapping("/asignar/{personalId}")
public String mostrarFormularioAsignacion(@PathVariable Integer personalId, Model model) {
    Personal personal = personalService.obtenerPorId(personalId)
            .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
    
    // Asegúrate que estos métodos no retornen null
    List<Equipo> equiposDisponibles = equipoService.findEquiposDisponibles();
    List<Equipo> equiposAsignados = equipoRepository.findEquiposAsignadosAPersonal(personalId); // Usa el nombre correcto
    
    if(equiposDisponibles == null) equiposDisponibles = new ArrayList<>();
    if(equiposAsignados == null) equiposAsignados = new ArrayList<>();
    
    model.addAttribute("personal", personal);
    model.addAttribute("equiposDisponibles", equiposDisponibles);
    model.addAttribute("equiposAsignados", equiposAsignados);
    
    return "personal/asignacion";
}

    @PostMapping("/guardar")
    public String guardarAsignacion(
            @RequestParam Integer personalId,
            @RequestParam Integer equipoId,
            RedirectAttributes attributes) {
        
        try {
            Personal personal = personalService.obtenerPorId(personalId).get();
            Equipo equipo = equipoService.obtenerPorId(equipoId).get();
            
            asignacionService.crearAsignacion(personal, equipo);
            attributes.addFlashAttribute("msg", "Equipo asignado correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/asignaciones/asignar/" + personalId;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable Integer id, RedirectAttributes attributes) {
        AsignacionEquipo asignacion = asignacionService.obtenerPorId(id);
        Integer personalId = asignacion.getPersonal().getId();
        
        asignacionService.eliminarAsignacion(id);
        attributes.addFlashAttribute("msg", "Asignación eliminada correctamente");
        
        return "redirect:/asignaciones/asignar/" + personalId;
    }

    @GetMapping("/por-personal/{personalId}")
    @ResponseBody
    public List<Equipo> obtenerEquiposAsignadosJson(@PathVariable Integer personalId) {
        return asignacionService.obtenerEquiposAsignados(personalId);
    }

    @GetMapping("/verificar/{equipoId}")
    @ResponseBody
    public boolean verificarAsignacionEquipo(@PathVariable Integer equipoId) {
        return asignacionService.equipoEstaAsignado(equipoId);
    }

    
}