package com.apexManagent.controladores;

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
import com.apexManagent.servicios.utilerias.PdfGeneraterService;
import com.apexManagent.servicios.interfaces.IPreventivoService;
import com.apexManagent.modelos.CalendarioPreventivo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.ReportePreventivo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IReportePreventivoService;
import com.apexManagent.servicios.interfaces.IPersonalService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/preventivos")
public class PreventivoController {

    @Autowired
    private IPreventivoService preventivoService;

    @Autowired
    private IEquipoService equipoService;

    @Autowired
    private IReportePreventivoService reportePreventivoService;

    @Autowired
    private IPersonalService personalService;

    @Autowired
    private PdfGeneraterService pdfGeneraterService;

    // VISTA PARA VER REPORTES PREVENTIVOS
    @GetMapping("/reportes-preventivos")
    public String verReportesPreventivos(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Short tipoMantenimiento) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(10);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fechaAtencion").descending());

        Page<ReportePreventivo> reportes = reportePreventivoService.buscarReportes(search, tipoMantenimiento, pageable);

        model.addAttribute("reportes", reportes);
        model.addAttribute("search", search);
        model.addAttribute("tipoMantenimiento", tipoMantenimiento);
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());
        model.addAttribute("estadosMap", getEstados());

        int totalPages = reportes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "preventivo/index";
    }

    // Mapa para estados
    private Map<Short, String> getEstados() {
        Map<Short, String> estados = new HashMap<>();
        estados.put((short) 1, "Pendiente");
        estados.put((short) 2, "En proceso");
        estados.put((short) 3, "Completado");
        return estados;
    }

    // Mapa para tipos de mantenimiento
    private Map<Short, String> getTiposMantenimiento() {
        Map<Short, String> tipos = new HashMap<>();
        tipos.put((short) 1, "Hardware");
        tipos.put((short) 2, "Software");
        tipos.put((short) 3, "Hardware y Software");
        return tipos;
    }

    // GENERAR REPORTE EN PDF
    @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> ReporteGeneral(@PathVariable("visualizacion") String visualizacion) {
        try {
            List<ReportePreventivo> reportes = reportePreventivoService.listarTodos(Pageable.unpaged()).getContent();

            // Pasar la lista con el nombre correcto que espera la plantilla
            byte[] pdfBytes = pdfGeneraterService.generatePdfFromHtml("reportes/rpPreventivo", "reporte_preventivo",
                    reportes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", visualizacion + "; filename=reporte_preventivo.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MÉTODOS EXISTENTES
    @GetMapping("/index")
    public String indexOld(Model model,
            @RequestParam(value = "estado", required = false) Short estado,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        return "redirect:/preventivos?estado=" + (estado != null ? estado : 1);
    }

    @GetMapping("/calendario")
    public String calendarioEquipo(@RequestParam("equipoId") Integer equipoId, Model model) {
        Optional<Equipo> equipoOpt = equipoService.obtenerPorId(equipoId);

        if (equipoOpt.isEmpty()) {
            model.addAttribute("error", "Equipo no encontrado.");
            return "error";
        }

        Equipo equipo = equipoOpt.get();
        CalendarioPreventivo calendario = new CalendarioPreventivo();
        calendario.setEquipo(equipo);

        model.addAttribute("equipo", equipo);
        model.addAttribute("calendario", calendario);

        return "preventivo/calendario";
    }

    @PostMapping("/guardar")
    public String guardarCalendario(
            @Valid @ModelAttribute("calendario") CalendarioPreventivo calendario,
            BindingResult result,
            @RequestParam("equipoId") Integer equipoId,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            Equipo equipo = equipoService.obtenerPorId(equipoId)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

            model.addAttribute("equipo", equipo);
            model.addAttribute("calendario", calendario);
            return "preventivo/calendario";
        }

        Equipo equipo = equipoService.obtenerPorId(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

        calendario.setEquipo(equipo);
        calendario.setEstadoMantenimiento((short) 1);
        preventivoService.guardar(calendario);

        redirectAttributes.addFlashAttribute("msg", "Equipo guardado y calendario preventivo guardado exitosamente.");
        return "redirect:/equipo";
    }

    // VISTA INDEX PREVENTIVO
    @GetMapping("/indexPreventivo")
    public String indexPreventivo(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Short estado) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(10);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fechaInicio").descending());

        Page<CalendarioPreventivo> calendarios;

        if (search != null && !search.isEmpty()) {
            calendarios = preventivoService.buscarCalendarios(search, estado, pageable);
        } else if (estado != null) {
            calendarios = preventivoService.listarPorEstado(estado, pageable);
        } else {
            calendarios = preventivoService.listarTodos(pageable);
        }

        for (CalendarioPreventivo calendario : calendarios) {
            if (calendario.getEquipo() != null) {
                calendario.getEquipo().getNombre();
            }
        }

        model.addAttribute("calendarios", calendarios);
        model.addAttribute("search", search);
        model.addAttribute("estado", estado);
        model.addAttribute("estadosMap", getEstados());

        int totalPages = calendarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "mantenimiento/indexPreventivo";
    }

    // Método para cambiar estado
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Integer id,
            @RequestParam Short estado,
            RedirectAttributes redirectAttributes) {
        Optional<CalendarioPreventivo> calendarioOpt = preventivoService.obtenerPorId(id);

        if (calendarioOpt.isPresent()) {
            CalendarioPreventivo calendario = calendarioOpt.get();
            calendario.setEstadoMantenimiento(estado);
            preventivoService.guardar(calendario);
            redirectAttributes.addFlashAttribute("msg", "Estado actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
        }

        return "redirect:/preventivos/indexPreventivo";
    }

    // ENVIAR REPORTE PREVENTIVO
    @GetMapping("/reporte/enviar/{id}")
    public String enviarReporte(@PathVariable Integer id, Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal User user) {

        Optional<CalendarioPreventivo> calendarioOpt = preventivoService.obtenerPorId(id);

        if (calendarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/preventivos/indexPreventivo";
        }

        CalendarioPreventivo calendario = calendarioOpt.get();

        // Cambiar estado a "En proceso" (2) si está pendiente
        if (calendario.getEstadoMantenimiento() == 1) {
            calendario.setEstadoMantenimiento((short) 2);
            preventivoService.guardar(calendario);
        }

        // Obtener el personal autenticado
        Personal personal = obtenerPersonalDesdeUsuario(user);
        if (personal == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al personal");
            return "redirect:/preventivos/indexPreventivo";
        }

        // Crear nuevo reporte
        ReportePreventivo reporte = new ReportePreventivo();
        reporte.setCalendarioPreventivo(calendario);
        reporte.setPersonal(personal);
        reporte.setFechaAtencion(LocalDateTime.now());

        // CAMBIO: Establecer estado como COMPLETADO (3) por defecto
        reporte.setEstado((short) 3);

        model.addAttribute("calendario", calendario);
        model.addAttribute("reporte", reporte);
        model.addAttribute("tiposMantenimiento", getTiposMantenimiento());

        return "mantenimiento/enviarReportePreventivo";
    }

    // GUARDAR REPORTE PREVENTIVO
    @PostMapping("/reporte/guardar")
    public String guardarReporte(@ModelAttribute ReportePreventivo reporte,
            @RequestParam("calendarioId") Integer calendarioId,
            @AuthenticationPrincipal User user,
            RedirectAttributes attributes) {

        Optional<CalendarioPreventivo> calendarioOpt = preventivoService.obtenerPorId(calendarioId);
        if (calendarioOpt.isEmpty()) {
            attributes.addFlashAttribute("error", "Mantenimiento no encontrado");
            return "redirect:/preventivos/indexPreventivo";
        }

        // Obtener el personal autenticado
        Personal personal = obtenerPersonalDesdeUsuario(user);
        if (personal == null) {
            attributes.addFlashAttribute("error", "No se pudo identificar al personal");
            return "redirect:/preventivos/indexPreventivo";
        }

        CalendarioPreventivo calendario = calendarioOpt.get();

        try {
            // Validar campos requeridos
            if (reporte.getObservacion() == null || reporte.getObservacion().trim().isEmpty()) {
                attributes.addFlashAttribute("error", "La observación es requerida");
                return "redirect:/preventivos/reporte/enviar/" + calendarioId;
            }

            if (reporte.getTipoMantenimiento() == null) {
                attributes.addFlashAttribute("error", "El tipo de mantenimiento es requerido");
                return "redirect:/preventivos/reporte/enviar/" + calendarioId;
            }

            // Asignar personal y calendario
            reporte.setPersonal(personal);
            reporte.setCalendarioPreventivo(calendario);
            reporte.setEstado((short) 3);

            // Guardar el reporte
            reportePreventivoService.guardar(reporte);

            // Cambiar estado del calendario a "Completado" (3)
            calendario.setEstadoMantenimiento((short) 3);
            preventivoService.guardar(calendario);

            attributes.addFlashAttribute("msg",
                    "Reporte enviado y mantenimiento marcado como completado exitosamente.");

        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al enviar el reporte: " + e.getMessage());
        }

        return "redirect:/preventivos/indexPreventivo";
    }

    // Método auxiliar para obtener personal desde usuario autenticado
    private Personal obtenerPersonalDesdeUsuario(User user) {
        if (user != null) {
            String username = user.getUsername();
            Optional<Personal> personalOpt = personalService.obtenerPorUsername(username);
            return personalOpt.orElse(null);
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/newCalendario/{id}")
    public String NewcalendarioEquipo(@PathVariable("id") Integer equipoId, Model model) {
        Optional<Equipo> equipoOpt = equipoService.obtenerPorId(equipoId);

        if (equipoOpt.isEmpty()) {
            model.addAttribute("error", "Equipo no encontrado.");
            return "error";
        }

        Equipo equipo = equipoOpt.get();
        CalendarioPreventivo calendario = new CalendarioPreventivo();
        calendario.setEquipo(equipo);

        model.addAttribute("equipo", equipo);
        model.addAttribute("calendario", calendario);

        return "preventivo/newCalendario";
    }

    @PostMapping("/Newguardar")
    public String guardarNewCalendario(
            @Valid @ModelAttribute("calendario") CalendarioPreventivo calendario,
            BindingResult result,
            @RequestParam("equipoId") Integer equipoId,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            Equipo equipo = equipoService.obtenerPorId(equipoId)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

            model.addAttribute("equipo", equipo);
            model.addAttribute("calendario", calendario);
            return "preventivo/calendario";
        }

        Equipo equipo = equipoService.obtenerPorId(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

        calendario.setEquipo(equipo);
        calendario.setEstadoMantenimiento((short) 1);
        preventivoService.guardar(calendario);

        redirectAttributes.addFlashAttribute("msg", "Reporte y nuevo calendario preventivo guardado exitosamente.");
        return "redirect:/indexPreventivo";
    }

}