package com.apexManagent.controladores;

import com.apexManagent.modelos.Ubicacion;
import com.apexManagent.servicios.interfaces.IUbicacionService;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ubicacion")
public class UbicacionController {

    private final IUbicacionService ubicacionService;

    @Autowired
    public UbicacionController(IUbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    public String index(Model model,
            @RequestParam(value = "msg", required = false) String msg,
            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("ubicaciones", ubicacionService.listarTodas());
        model.addAttribute("msg", msg);
        model.addAttribute("error", error);
        return "ubicacion/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ubicacion", new Ubicacion());
        return "ubicacion/create";
    }

    @PostMapping("/save")
    public String saveUbicacion(@ModelAttribute("ubicacion") Ubicacion ubicacion,
            RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.guardarUbicacion(ubicacion);
            redirectAttributes.addFlashAttribute("msg", "Ubicación creada exitosamente");
            return "redirect:/ubicacion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear ubicación: " + e.getMessage());
            return "redirect:/ubicacion/create";
        }
    }

    // Mostrar formulario de edición
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Ubicacion> ubicacion = ubicacionService.buscarPorId(id);
        if (ubicacion.isPresent()) {
            model.addAttribute("ubicacion", ubicacion.get());
            return "ubicacion/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", "Ubicación no encontrada");
            return "redirect:/ubicacion";
        }
    }

    // Procesar actualización
    @PostMapping("/update")
    public String updateUbicacion(@ModelAttribute("ubicacion") Ubicacion ubicacion,
            RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.guardarUbicacion(ubicacion);
            redirectAttributes.addFlashAttribute("msg", "Ubicación actualizada exitosamente");
            return "redirect:/ubicacion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/ubicacion/edit/" + ubicacion.getId();
        }
    }

    // Eliminar ubicación
    @GetMapping("/delete/{id}")
    public String deleteUbicacion(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("msg", "Ubicación eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/ubicacion";
    }
}