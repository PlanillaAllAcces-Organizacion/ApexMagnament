package com.apexManagent.controladores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apexManagent.modelos.Categoria;
import com.apexManagent.servicios.interfaces.ICategoriaService;

@Controller
public class CategoriaController {

    @Autowired
    private ICategoriaService categoriaService;

    @GetMapping("/categoria")
    public String index(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("nombreCategoria") Optional<String> nombreCategoria) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(4);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nombreSearch = nombreCategoria.orElse("");
        Page<Categoria> categorias = categoriaService.findByNombreCategoriaContaining(nombreSearch, pageable);
        model.addAttribute("categorias", categorias);

        int totalPages = categorias.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "categoria/index";
    }

    @GetMapping("/categoria/create")
    public String create(Categoria categoria) {
        return "categoria/create";
    }

    @PostMapping("/categoria/guardar")
    public String guardarRegistro(Categoria categoria, BindingResult result, Model model,
            RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("categoria", categoria);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "categoria/create";
        }

        if (categoriaService.existsByNombreCategoria(categoria.getNombreCategoria())) {
            attributes.addFlashAttribute("error", "Ya existe una categoría con ese nombre.");
            model.addAttribute("categoria", categoria);
            return "redirect:/categoria/create";
        }

        categoriaService.guardar(categoria);
        attributes.addFlashAttribute("msg", "Categoría guardada exitosamente.");
        return "redirect:/categoria";
    }


    @GetMapping("/categoria/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        Optional<Categoria> categoriaOpt = categoriaService.obtenerPorId(id);
        if (categoriaOpt.isPresent()) {
            model.addAttribute("categoria", categoriaOpt.get());
            return "categoria/edit";
        } else {
            attributes.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/categoria";
        }
    }
}
