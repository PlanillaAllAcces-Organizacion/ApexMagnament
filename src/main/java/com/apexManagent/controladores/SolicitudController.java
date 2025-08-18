package com.apexManagent.controladores;

import com.apexManagent.modelos.Solicitud;
import com.apexManagent.servicios.interfaces.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/mantenimiento/solicitud")
public class SolicitudController {

    @Autowired
    private ISolicitudService solicitudService;

    @GetMapping
    public String index(Model model,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam(required = false) String search,
                       @AuthenticationPrincipal User user) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        Page<Solicitud> solicitudes;
        if (search != null && !search.isEmpty()) {
            solicitudes = solicitudService.buscarSolicitudes(search, pageable);
        } else {
            solicitudes = solicitudService.obtenerSolicitudesPendientes(pageable);
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("search", search);

        int totalPages = solicitudes.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers", java.util.stream.IntStream.rangeClosed(1, totalPages).boxed().toList());
        }

        return "mantenimiento/solicitud";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Integer id, 
                               @RequestParam short estado,
                               RedirectAttributes attributes) {
        solicitudService.cambiarEstado(id, estado);
        attributes.addFlashAttribute("msg", estado == 1 ? "Solicitud aprobada" : "Solicitud rechazada");
        return "redirect:/mantenimiento/solicitud";
    }
}