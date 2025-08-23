package com.apexManagent.controladores;

import com.apexManagent.modelos.ReporteCorrectivo;
import com.apexManagent.modelos.Solicitud;
import com.apexManagent.servicios.interfaces.IReporteCorrectivoService;
import com.apexManagent.servicios.interfaces.ISolicitudService;
import com.apexManagent.servicios.utilerias.PdfGeneraterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/mantenimiento/solicitud")
public class SolicitudController {

    @Autowired
    private ISolicitudService solicitudService;

    @Autowired
    private IReporteCorrectivoService reporteCorrectivoService;

    @Autowired
    private PdfGeneraterService pdfGeneraterService;

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

    // VISTA PARA VER REPORTES CORRECTIVOS
    @GetMapping("/reportes-correctivos")
    public String verReportes(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Short tipoMantenimiento) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(10);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fechaCreacion").descending());

        // Usar el método único de búsqueda
        Page<ReporteCorrectivo> reportes = reporteCorrectivoService.buscarReportes(search, tipoMantenimiento, pageable);

        model.addAttribute("reportes", reportes);
        model.addAttribute("search", search);
        model.addAttribute("tipoMantenimiento", tipoMantenimiento);
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());

        int totalPages = reportes.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers", IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList()));
        }

        return "mantenimiento/reporteCorrectivo";
    }

    // VISTA PRINCIPAL DE SOLICITUDES
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
        model.addAttribute("estadosMap", getEstados());

        int totalPages = solicitudes.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers", IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList()));
        }

        return "mantenimiento/solicitud";
    }

    // CAMBIAR ESTADO DE SOLICITUD
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

    // MOSTRAR FORMULARIO PARA CREAR REPORTE
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
        reporte.setSolicitud(solicitud);

        model.addAttribute("reporte", reporte);
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());
        model.addAttribute("estados", getEstados());

        return "mantenimiento/enviarReporte";
    }

    // GUARDAR REPORTE CORRECTIVO
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

            System.out.println(
                    "Creando reporte con: " + reporte.getObservacion() + ", tipo: " + reporte.getTipoMantenimiento());

            // Crear y guardar el reporte
            ReporteCorrectivo reporteGuardado = reporteCorrectivoService.crearReporteDesdeSolicitud(
                    solicitud,
                    reporte.getObservacion(),
                    reporte.getTipoMantenimiento(),
                    estadoFinal);

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

    // VER DETALLES DE UN REPORTE ESPECÍFICO
    @GetMapping("/reporte/ver/{solicitudId}")
    public String verDetalleReporte(@PathVariable Integer solicitudId, Model model) {
        Optional<ReporteCorrectivo> reporteOpt = reporteCorrectivoService.obtenerPorSolicitudId(solicitudId);

        if (!reporteOpt.isPresent()) {
            return "redirect:/mantenimiento/solicitud/reportes?error=Reporte no encontrado";
        }

        model.addAttribute("reporte", reporteOpt.get());
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());
        model.addAttribute("estados", getEstados());

        return "mantenimiento/detalleReporte";
    }

    @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> ReporteGeneral(@PathVariable("visualizacion") String visualizacion) {
        try {
            List<ReporteCorrectivo> rCorrectivos = reporteCorrectivoService.findAll();

            // Genera el PDF
          byte[] pdfBytes = pdfGeneraterService.generatePdfFromHtml("reportes/rpCorrectivo", "rCorrectivos",
                       rCorrectivos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // inline= vista previa, attachment=descarga el archivo
            headers.add("Content-Disposition", visualizacion + "; filename=reporte_correctivo.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}