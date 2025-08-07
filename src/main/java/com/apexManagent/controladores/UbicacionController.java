package com.apexManagent.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.apexManagent.modelos.Ubicacion;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private IUbicacionService ubicacionService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size, @RequestParam("nombreUbicacion") Optional<String> nombreUbicacion) {

        int currentPage = page.orElse(1) - 1; // si no está seteado se asigna 0
        int pageSize = size.orElse(4); // tamaño de la página, se asigna 5

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nombreSearch = nombreUbicacion.orElse("");

        Page<Ubicacion> ubicaciones = ubicacionService.findByNombreUbicacionContaining(nombreSearch, pageable);
        model.addAttribute("ubicaciones", ubicaciones);

        int totalPages = ubicaciones.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "ubicacion/index";
    }

    @GetMapping("/create")
    public String create(Ubicacion ubicacion) {
        return "ubicacion/create";
    }

    @PostMapping("/guardar")
    public String guardarRegistro(Ubicacion ubicacion, BindingResult result, Model model,
            RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(ubicacion);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "ubicacion/create";
        }

        ubicacionService.guardar(ubicacion);
        attributes.addFlashAttribute("msg", "Ubicación guardada exitosamente.");
        return "redirect:/ubicaciones";

    }


    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Ubicacion ubicacion = ubicacionService.obtenerPorId(id).get();
        model.addAttribute("ubicacion", ubicacion);
        return "ubicacion/details";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Ubicacion ubicacion = ubicacionService.obtenerPorId(id).get();
        model.addAttribute("ubicacion", ubicacion);
        return "ubicacion/edit";
    }

   

   


}
