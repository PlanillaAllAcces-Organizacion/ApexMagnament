package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/equipo")
public class EquipoController {

    @Autowired
    private IEquipoService equipoService;

    @Autowired
    private IUbicacionService ubicacionService;

    @GetMapping
    public String index(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam("nserie") Optional<String> nserie,
            @RequestParam("nombre") Optional<String> nombre,
            @RequestParam("modelo") Optional<String> modelo) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nserieSearch = nserie.orElse("");
        String nombreSearch = nombre.orElse("");
        String modeloSearch = modelo.orElse("");
        
        Page<Equipo> equipos = equipoService.findByNserieContainingAndNombreContainingAndModeloContaining(
                nserieSearch, nombreSearch, modeloSearch, pageable);

        model.addAttribute("equipos", equipos);
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());

        int totalPages = equipos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "equipo/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("equipo")) {
            model.addAttribute("equipo", new Equipo());
        }
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());
        return "equipo/create";
    }

   @PostMapping("/save")
public String save(@Valid @ModelAttribute Equipo equipo,
        BindingResult result,
        @RequestParam("imagen") MultipartFile imagen,
        RedirectAttributes attributes) throws IOException {

    // Validación de errores del modelo
    if (result.hasErrors()) {
        // Mensaje específico para descripción
        if (result.getFieldError("descripcion") != null) {
            attributes.addFlashAttribute("errorDescripcion", 
                "La descripción no puede exceder los 255 caracteres");
        }
        attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/create";
    }

    if (equipoService.existePorNserie(equipo.getNserie())) {
        attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/create";
    }

    if (imagen.isEmpty()) {
        attributes.addFlashAttribute("error", "Debe seleccionar una imagen");
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/create";
    }

    if (imagen.getSize() > 2097152) {
        attributes.addFlashAttribute("error", "La imagen no debe exceder 2MB");
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/create";
    }

    String contentType = imagen.getContentType();
    if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
        attributes.addFlashAttribute("error", "Solo se permiten imágenes JPEG o PNG");
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/create";
    }

    equipo.setImg(imagen.getBytes());
    equipo.setFechaRegistro(LocalDateTime.now());
    equipoService.guardar(equipo);

    attributes.addFlashAttribute("msg", "Equipo registrado correctamente");
    return "redirect:/equipo";
}

    @GetMapping("/details/{id}")
    public String mostrarEquipo(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        return "equipo/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());
        return "equipo/edit";
    }

    @PostMapping("/update")
    public String update(
            @RequestParam("id") Integer id,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("equipo") Equipo equipo,
            BindingResult result,
            RedirectAttributes attributes) throws IOException {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/edit/" + id;
        }

        Equipo equipoExistente = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

        if (equipoService.existePorNserie(equipo.getNserie()) &&
                !equipoExistente.getNserie().equals(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
            return "redirect:/equipo/edit/" + id;
        }

        if (imagen != null && !imagen.isEmpty()) {
            equipo.setImg(imagen.getBytes());
        } else {
            equipo.setImg(equipoExistente.getImg());
        }

        equipo.setFechaRegistro(equipoExistente.getFechaRegistro());
        equipoService.guardar(equipo);
        
        attributes.addFlashAttribute("msg", "Información actualizada correctamente");
        return "redirect:/equipo/details/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            equipoService.eliminarPorId(id);
            attributes.addFlashAttribute("swal", Map.of(
                    "title", "¡Eliminado!",
                    "text", "El equipo ha sido eliminado correctamente",
                    "icon", "success"));
        } catch (Exception e) {
            attributes.addFlashAttribute("swal", Map.of(
                    "title", "Error",
                    "text", "No se pudo eliminar el equipo: " + e.getMessage(),
                    "icon", "error"));
        }
        return "redirect:/equipo";
    }
}