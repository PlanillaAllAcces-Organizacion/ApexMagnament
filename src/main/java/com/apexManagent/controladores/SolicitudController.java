package com.apexManagent.controladores;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import com.apexManagent.servicios.interfaces.IReporteCorrectivoService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/mantenimiento/solicitud")
public class SolicitudController {

    @Autowired
    private ISolicitudService solicitudService;

    @Autowired
    private IReporteCorrectivoService reporteCorrectivoService;

    // Mapa para tipos de mantenimiento
    private Map<Short, String> getTiposMantenimiento() {
        Map<Short, String> tipos = new HashMap<>();
        tipos.put((short) 1, "Hardware");
        tipos.put((short) 2, "Software");
        tipos.put((short) 3, "Software y Hardware");
        return tipos;
    }

    // Mapa para estados
    private Map<Short, String> getEstados() {
        Map<Short, String> estados = new HashMap<>();
        estados.put((short) 0, "Rechazada");
        estados.put((short) 1, "En espera");
        estados.put((short) 2, "En proceso");
        estados.put((short) 3, "Finalizado");
        return estados;
    }

    @GetMapping
    public String index(Model model,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) Short estado,
                       @AuthenticationPrincipal User user) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        Page<Solicitud> solicitudes;
        short estadoFiltro = estado != null ? estado : (short) 1;
        
        if (search != null && !search.isEmpty()) {
            solicitudes = solicitudService.obtenerSolicitudesPorEstadoYEmpleado(estadoFiltro, search, pageable);
        } else {
            solicitudes = solicitudService.obtenerSolicitudesPorEstado(estadoFiltro, pageable);
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("search", search);
        model.addAttribute("estado", estadoFiltro);

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
        
        String mensaje = switch (estado) {
            case 0 -> "Solicitud rechazada";
            case 1 -> "Solicitud puesta en espera";
            case 2 -> "Solicitud aceptada y en proceso";
            case 3 -> "Solicitud finalizada";
            default -> "Estado actualizado";
        };
        
        attributes.addFlashAttribute("msg", mensaje);
        return "redirect:/mantenimiento/solicitud";
    }

    @GetMapping("/reporte/enviar/{solicitudId}")
    public String mostrarFormularioReporte(@PathVariable Integer solicitudId, Model model) {
        Solicitud solicitud = solicitudService.obtenerPorId(solicitudId);
        
        if (solicitud == null) {
            return "redirect:/mantenimiento/solicitud?error=Solicitud no encontrada";
        }

        if (reporteCorrectivoService.existeReporteParaSolicitud(solicitudId)) {
            return "redirect:/mantenimiento/solicitud?error=Ya existe un reporte para esta solicitud";
        }
        ReporteCorrectivo reporte = new ReporteCorrectivo();
        reporte.setEstado((short) 3);
        reporte.setSolicitud(solicitud);

        model.addAttribute("reporte", reporte);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());
        model.addAttribute("estados", getEstados());
        
        return "mantenimiento/enviarReporte";
    }
   @PostMapping("/reporte/guardar")
public String guardarReporte(@ModelAttribute ReporteCorrectivo reporte,
                            @RequestParam("solicitudId") Integer solicitudId,
                            RedirectAttributes attributes) {
    
    System.out.println("Iniciando guardado de reporte para solicitud: " + solicitudId);
    
    Solicitud solicitud = solicitudService.obtenerPorId(solicitudId);
    
    if (solicitud == null) {
        System.out.println("ERROR: Solicitud no encontrada con ID: " + solicitudId);
        attributes.addFlashAttribute("error", "Solicitud no encontrada");
        return "redirect:/mantenimiento/solicitud";
    }

    try {
        // Validar campos requeridos
        if (reporte.getObservacion() == null || reporte.getObservacion().trim().isEmpty()) {
            attributes.addFlashAttribute("error", "La observación es requerida");
            return "redirect:/mantenimiento/solicitud/reporte/enviar/" + solicitudId;
        }

        if (reporte.getTipoMantenimiento() == null) {
            attributes.addFlashAttribute("error", "El tipo de mantenimiento es requerido");
            return "redirect:/mantenimiento/solicitud/reporte/enviar/" + solicitudId;
        }

        Short estadoFinal = 3; // Siempre finalizado
        
        System.out.println("Creando reporte con: " + reporte.getObservacion() + ", tipo: " + reporte.getTipoMantenimiento());
        
        // Crear y guardar el reporte
        ReporteCorrectivo reporteGuardado = reporteCorrectivoService.crearReporteDesdeSolicitud(
            solicitud,
            reporte.getObservacion(),
            reporte.getTipoMantenimiento(),
            estadoFinal
        );

        System.out.println("Reporte guardado con ID: " + reporteGuardado.getId());

        // Actualizar el estado de la solicitud a finalizado
        solicitudService.cambiarEstado(solicitudId, estadoFinal);
        System.out.println("Estado de solicitud actualizado a finalizado");

        attributes.addFlashAttribute("msg", "Reporte enviado correctamente y solicitud finalizada");
        
    } catch (Exception e) {
        System.out.println("ERROR al guardar reporte: " + e.getMessage());
        e.printStackTrace();
        attributes.addFlashAttribute("error", "Error al enviar el reporte: " + e.getMessage());
    }

    return "redirect:/mantenimiento/solicitud";
}
}