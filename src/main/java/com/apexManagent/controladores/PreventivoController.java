package com.apexManagent.controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apexManagent.modelos.CalendarioPreventivo;
import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IPreventivoService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/preventivos")
public class PreventivoController {

    @Autowired
    public IEquipoService equipoService;

    @Autowired
    private IPreventivoService preventivoService;

    @GetMapping("/index")
    public String index(Model model,
            @RequestParam(value = "estado", required = false) Short estado,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1) - 1; // PageRequest empieza en 0
        int pageSize = size.orElse(5);

        Sort sortByFechaInicioDesc = Sort.by(Sort.Direction.DESC, "fechaInicio");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByFechaInicioDesc);

        Page<CalendarioPreventivo> calendarios;

        // Estado por defecto: 1 (En espera)
        short estadoFiltro = estado != null ? estado : (short) 1;

        calendarios = preventivoService.listarPorEstado(estadoFiltro, pageable);

        // Bucle para forzar la carga de los objetos "equipo"
        for (CalendarioPreventivo calendario : calendarios) {
            // Acceder a cualquier propiedad de "equipo" para inicializar el proxy
            calendario.getEquipo().getNombre();
        }

        model.addAttribute("calendarios", calendarios);
        model.addAttribute("estado", estadoFiltro);

        int totalPages = calendarios.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers",
                    java.util.stream.IntStream.rangeClosed(1, totalPages).boxed().toList());
        }

        return "preventivo/index";
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

        // 1. Validar errores del formulario
        if (result.hasErrors()) {
            // Si hay errores, volvemos al formulario
            Equipo equipo = equipoService.obtenerPorId(equipoId)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

            model.addAttribute("equipo", equipo);
            model.addAttribute("calendario", calendario);

            return "preventivo/calendario";
        }

        // 2. Obtener el objeto Equipo
        Equipo equipo = equipoService.obtenerPorId(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + equipoId));

        // 3. Asociar equipo y valores por defecto
        calendario.setEquipo(equipo);
        calendario.setEstadoMantenimiento((short) 1);

        // 4. Guardar en BD
        preventivoService.guardar(calendario);

        redirectAttributes.addFlashAttribute("msg", "Equipo guardado y calendario preventivo guardado exitosamente.");

        return "redirect:/equipo";
    }

}
