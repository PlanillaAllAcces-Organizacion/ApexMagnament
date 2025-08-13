package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            @RequestParam(required = false) String searchNombre,
            @RequestParam(required = false) String searchModelo,
            @RequestParam(required = false) String searchSerie) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        if (searchSerie != null && !searchSerie.isEmpty()) {
            // Búsqueda paginada por serie
            Page<Equipo> equipos = equipoService.buscarPorSerie(searchSerie, pageable);
            model.addAttribute("equipos", equipos);
            
            int totalPages = equipos.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
            
        } else if (searchNombre != null && !searchNombre.isEmpty()) {
            // Búsqueda no paginada por nombre
            List<Equipo> equipos = equipoService.buscarPorNombre(searchNombre);
            model.addAttribute("equipos", new PageImpl<>(equipos, pageable, equipos.size()));
            model.addAttribute("pageNumbers", List.of(1)); // Solo una página
            
        } else if (searchModelo != null && !searchModelo.isEmpty()) {
            // Búsqueda no paginada por modelo
            List<Equipo> equipos = equipoService.buscarPorModelo(searchModelo);
            model.addAttribute("equipos", new PageImpl<>(equipos, pageable, equipos.size()));
            model.addAttribute("pageNumbers", List.of(1)); // Solo una página
            
        } else {
            // Búsqueda general paginada
            Page<Equipo> equipos = equipoService.buscarTodosPaginados(pageable);
            model.addAttribute("equipos", equipos);
            
            int totalPages = equipos.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }

        model.addAttribute("searchNombre", searchNombre);
        model.addAttribute("searchModelo", searchModelo);
        model.addAttribute("searchSerie", searchSerie);

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

        if (result.hasErrors()) {
            // Agregar mensajes de error específicos
            if (result.getFieldError("descripcion") != null) {
                attributes.addFlashAttribute("errorDescripcion",
                        "La descripción no puede exceder los 255 caracteres");
            }
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        // Validar número de serie único
        if (equipoService.existePorNserie(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        // Procesar imagen si fue subida
        if (!imagen.isEmpty()) {
            // Validar tamaño máximo (2MB)
            if (imagen.getSize() > 2097152) {
                attributes.addFlashAttribute("error", "La imagen no debe exceder 2MB");
                attributes.addFlashAttribute("equipo", equipo);
                return "redirect:/equipo/create";
            }

            // Validar tipo de imagen
            String contentType = imagen.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                attributes.addFlashAttribute("error", "Solo se permiten imágenes JPEG o PNG");
                attributes.addFlashAttribute("equipo", equipo);
                return "redirect:/equipo/create";
            }

            // Asignar imagen al equipo (se guardará en la base de datos)
            equipo.setImg(imagen.getBytes());
        } else {
            attributes.addFlashAttribute("error", "Debe seleccionar una imagen");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        // Establecer fecha de registro
        equipo.setFechaRegistro(LocalDateTime.now());

        // Guardar equipo (incluyendo la imagen)
        equipoService.guardarEquipo(equipo);

        attributes.addFlashAttribute("msg", "Equipo registrado correctamente");
        return "redirect:/equipo";
    }

    @GetMapping("/details/{id}")
    public String mostrarEquipo(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        return "equipo/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        try {
            Equipo equipo = equipoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

            model.addAttribute("equipo", equipo);
            model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());

            return "equipo/edit";
        } catch (Exception e) {
            System.err.println("Error al cargar edición: " + e.getMessage());
            return "redirect:/equipo?error=Error al cargar equipo para edición";
        }

    }

   @PostMapping("/update")
public String update(
        @RequestParam("id") Integer id,
        @RequestParam(value = "imagen", required = false) MultipartFile imagen,
        @Valid @ModelAttribute("equipo") Equipo equipo,
        BindingResult result,
        RedirectAttributes attributes) throws IOException {

    if (result.hasErrors()) {
        if (result.getFieldError("descripcion") != null) {
            attributes.addFlashAttribute("errorDescripcion", 
                "La descripción no puede exceder los 255 caracteres");
        }
        attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
        attributes.addFlashAttribute("equipo", equipo);
        return "redirect:/equipo/edit/" + id;
    }

        // Obtener equipo existente
        Equipo equipoExistente = equipoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

        // Validar número de serie único (excepto para el equipo actual)
        if (equipoService.existePorNserie(equipo.getNserie()) &&
                !equipoExistente.getNserie().equals(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
            return "redirect:/equipo/edit/" + id;
        }

        // Resto del código para manejar la imagen...
        if (imagen != null && !imagen.isEmpty()) {
            // Validaciones de imagen...
            equipo.setImg(imagen.getBytes());
        } else {
            equipo.setImg(equipoExistente.getImg());
        }

        // Preservar otros datos importantes
        equipo.setFechaRegistro(equipoExistente.getFechaRegistro());

        // Actualizar equipo
        equipoService.guardarEquipo(equipo);
        attributes.addFlashAttribute("msg", "Informacion actualizada correctamente");

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