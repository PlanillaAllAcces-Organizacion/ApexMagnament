package com.apexManagent.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private IAsignacionEquipoService asignacionEquipoService;

    @GetMapping("/index")
    public String index() {
        return "empleado/index";
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
    public String miSolicitud() {
        return "empleado/solicitud";
    }


}
