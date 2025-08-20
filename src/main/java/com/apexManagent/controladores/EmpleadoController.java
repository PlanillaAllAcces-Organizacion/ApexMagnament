package com.apexManagent.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.apexManagent.modelos.AsignacionEquipo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.modelos.Personal;
import com.apexManagent.modelos.Solicitud;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IPersonalService;
import com.apexManagent.servicios.interfaces.ISolicitudService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IAsignacionEquipoService asignacionEquipoService;

    @Autowired
    private ISolicitudService solicitudService;

    @Autowired
    private IPersonalService personalService;

    @GetMapping("/historial")
    public String historialSolicitud(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("nombre") Optional<String> nombre,
            @RequestParam("modelo") Optional<String> modelo,
            @RequestParam("fechaRegistro") Optional<String> fechaRegistro) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        Date fechaSearch = null; // Inicializa la fecha como nula
        if (fechaRegistro.isPresent() && !fechaRegistro.get().isBlank()) {
            try {
                // Si la fecha no está vacía, la conviertes manualmente
                fechaSearch = Date.valueOf(fechaRegistro.get());
            } catch (IllegalArgumentException e) {
                // Maneja el error si el formato de la fecha es incorrecto
                model.addAttribute("error", "Formato de fecha inválido.");
                // Puedes devolver la vista con un mensaje de error o redirigir
                return "empleado/historial";
            }
        }

        String nombreSearch = nombre.orElse(null);
        String modeloSearch = modelo.orElse(null);
        Page<Solicitud> solicitudes = solicitudService.obtenerLasSolicitudesDelUsuario(nombreSearch, modeloSearch,
                fechaSearch, pageable);

        Map<Short, String> estados = new HashMap<>();
        estados.put((short) 0, "Rechazada");
        estados.put((short) 1, "En espera");
        estados.put((short) 2, "En proceso");
        estados.put((short) 3, "Finalizado");

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("estados", estados);

        int totalPages = solicitudes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "empleado/historial";
    }

    @GetMapping("/miEquipo")
    public String MiEquipo(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("nserie") Optional<String> nserie,
            @RequestParam("nombre") Optional<String> nombre,
            @RequestParam("modelo") Optional<String> modelo) {

        int currentPage = page.orElse(1) - 1; // page empieza en 0 en Spring
        int pageSize = size.orElse(5); // cantidad por página

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nserieSearch = nserie.orElse("");
        String nombreSearch = nombre.orElse("");
        String modeloSearch = modelo.orElse("");
        Page<Equipo> equipos = asignacionEquipoService.obtenerEquiposDelUsuarioAutenticado(nserieSearch, nombreSearch,
                modeloSearch, pageable);

        model.addAttribute("equipos", equipos);

        int totalPages = equipos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "empleado/miEquipo";
    }

    @GetMapping("/solicitud")
    public String miSolicitud(@RequestParam("equipoId") Integer equipoId, Model model, Principal principal,
            Pageable pageable) {

        // 1. Obtener equipos asignados al usuario actual
        Page<Equipo> equipos = asignacionEquipoService.obtenerEquiposDelUsuarioAutenticado(
                "", "", "", pageable);

        // 2. Buscar si el equipoId está en la lista
        Equipo equipo = equipos.getContent().stream()
                .filter(eq -> eq.getId().equals(equipoId))
                .findFirst()
                .orElse(null);

        if (equipo == null) {
            model.addAttribute("error", "No tienes asignado este equipo.");
            return "empleados/solicitud-error";
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setFechaRegistro(LocalDateTime.now());
        solicitud.setDescripcion("");
        model.addAttribute("equipo", equipo);
        model.addAttribute("solicitud", solicitud);

        return "empleado/solicitud";
    }

    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute("solicitud") Solicitud solicitud,
            @RequestParam("equipoId") Integer equipoId,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // 2. Obtener las entidades necesarias
        Personal personal = personalService.getAuthenticatedPersonal(); // Llama al nuevo servicio
        AsignacionEquipo asignacionEquipo = asignacionEquipoService
                .obtenerAsignacionDelUsuarioAutenticadoYEquipo(equipoId);

        // 3. Validar si la asignación existe
        if (asignacionEquipo == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar la solicitud. No se encontró la asignación de equipo.");
            return "redirect:/empleado/miEquipo";
        }

        // 4. Completar los datos de la solicitud
        solicitud.setPersonal(personal);
        solicitud.setAsignacionEquipo(asignacionEquipo);
        solicitud.setEstado((short) 1);

        // 5. Guardar la solicitud
        solicitudService.guardar(solicitud);

        redirectAttributes.addFlashAttribute("msg", "Solicitud enviada, espere su respuesta");

        return "redirect:/empleados/miEquipo";
    }

}
