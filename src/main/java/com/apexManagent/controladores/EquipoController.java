package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            @RequestParam(required = false) String search) {

        int currentPage = page.orElse(1) - 1; // Ajuste para que empiece en 0
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Equipo> equipos;

        if (search != null && !search.isEmpty()) {
            equipos = equipoService.buscarPorNombreModeloOSerie(search, pageable);
        } else {
            equipos = equipoService.buscarTodosPaginados(pageable);
        }

        System.out.println("Equipos encontrados: " + equipos.getTotalElements());

        model.addAttribute("equipos", equipos);
        model.addAttribute("search", search);
        model.addAttribute("ubicaciones", ubicacionService.listarTodas());

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
        model.addAttribute("ubicaciones", ubicacionService.listarTodas());
        return "equipo/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Equipo equipo,
            @RequestParam("imagen") MultipartFile imagen,
            BindingResult result,
            RedirectAttributes attributes) throws IOException {

        if (result.hasErrors()) {
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

            // Asignar imagen al equipo
            equipo.setImg(imagen.getBytes());
        }

        // Establecer fecha de registro
        equipo.setFechaRegistro(LocalDateTime.now());

        // Guardar equipo
        equipoService.guardarEquipo(equipo);

        attributes.addFlashAttribute("success", "Equipo registrado correctamente");
        return "redirect:/equipo";
    }

    @GetMapping("/details/{nserie}")
    public String detailsByNserie(@PathVariable String nserie, Model model) {
        try {
            Equipo equipo = equipoService.buscarPorNserie(nserie)
                    .orElseThrow(
                            () -> new IllegalArgumentException("Equipo no encontrado con número de serie: " + nserie));

            model.addAttribute("equipo", equipo);
            return "equipo/details";
        } catch (Exception e) {
            System.err.println("Error al cargar detalles: " + e.getMessage());
            return "redirect:/equipo?error=Equipo no encontrado";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        model.addAttribute("ubicaciones", ubicacionService.listarTodas());

        return "equipo/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Equipo equipo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes attributes) throws IOException {

        if (imagen != null && !imagen.isEmpty()) {
            equipo.setImg(imagen.getBytes());
        }

        equipoService.guardarEquipo(equipo);
        attributes.addFlashAttribute("success", "Equipo actualizado correctamente");
        return "redirect:/equipo";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes attributes) {
        equipoService.eliminarPorId(id);
        attributes.addFlashAttribute("success", "Equipo eliminado correctamente");
        return "redirect:/equipo";
    }
}