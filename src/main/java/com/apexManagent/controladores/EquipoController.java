package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/equipo")
public class EquipoController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private IEquipoService equipoService;

    @Autowired
    private IUbicacionService ubicacionService;

    @GetMapping
    public String index(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam("nserie") Optional<String> nserie,
            @RequestParam("nombre") Optional<String> nombre,
            @RequestParam("modelo") Optional<String> modelo) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nserieSearch = nserie.orElse("");
        String nombreSearch = nombre.orElse("");
        String modeloSearch = modelo.orElse("");

        Page<Equipo> equipos = equipoService.findByNserieContainingAndNombreContainingAndModeloContaining(
                nserieSearch, nombreSearch, modeloSearch, pageable);

        model.addAttribute("equipos", equipos);
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());

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
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());
        return "equipo/create";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Equipo equipo,
            BindingResult result,
            @RequestParam("fileImagen") MultipartFile fileImagen,
            RedirectAttributes attributes) throws IOException {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        if (equipoService.existePorNserie(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        if (fileImagen.isEmpty()) {
            attributes.addFlashAttribute("error", "Debe seleccionar una imagen");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        if (fileImagen.getSize() > 2097152) {
            attributes.addFlashAttribute("error", "La imagen no debe exceder 2MB");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        String contentType = fileImagen.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            attributes.addFlashAttribute("error", "Solo se permiten imágenes JPEG o PNG");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = fileImagen.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            equipo.setImg(fileName);
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/equipo/create";
        }

        equipo.setFechaRegistro(LocalDateTime.now());
        equipoService.guardar(equipo);
        attributes.addFlashAttribute("msg", "Equipo registrado correctamente");
        return "redirect:/equipo";
    }

    @GetMapping("/details/{id}")
    public String mostrarEquipo(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        return "equipo/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Equipo equipo = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));
        model.addAttribute("equipo", equipo);
        model.addAttribute("ubicaciones", ubicacionService.obtenerTodos());
        return "equipo/edit";
    }

    @PostMapping("/update")
    public String update(
            @RequestParam("id") Integer id,
            @RequestParam(value = "fileImagen", required = false) MultipartFile fileImagen,
            @Valid @ModelAttribute("equipo") Equipo equipo,
            BindingResult result,
            RedirectAttributes attributes) throws IOException {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/edit/" + id;
        }

        Equipo equipoExistente = equipoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

        if (equipoService.existePorNserie(equipo.getNserie()) &&
                !equipoExistente.getNserie().equals(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie");
            return "redirect:/equipo/edit/" + id;
        }

        if (fileImagen != null && !fileImagen.isEmpty()) {
            try {
                // Validar imagen nueva
                if (fileImagen.getSize() > 2097152) {
                    attributes.addFlashAttribute("error", "La imagen no debe exceder 2MB");
                    return "redirect:/equipo/edit/" + id;
                }

                String contentType = fileImagen.getContentType();
                if (!(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                    attributes.addFlashAttribute("error", "Solo se permiten imágenes JPEG o PNG");
                    return "redirect:/equipo/edit/" + id;
                }

                // Eliminar imagen anterior si existe
                if (equipoExistente.getImg() != null) {
                    Path oldFilePath = Paths.get(UPLOAD_DIR + equipoExistente.getImg());
                    Files.deleteIfExists(oldFilePath);
                }

                // Guardar nueva imagen
                String fileName = fileImagen.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                equipo.setImg(fileName);
            } catch (Exception e) {
                attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
                return "redirect:/equipo/edit/" + id;
            }
        } else {
            equipo.setImg(equipoExistente.getImg());
        }

        equipo.setFechaRegistro(equipoExistente.getFechaRegistro());
        equipoService.guardar(equipo);
        attributes.addFlashAttribute("msg", "Información actualizada correctamente");
        return "redirect:/equipo/details/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            Equipo equipo = equipoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

            // Eliminar imagen asociada si existe
            if (equipo.getImg() != null) {
                Path filePath = Paths.get(UPLOAD_DIR + equipo.getImg());
                Files.deleteIfExists(filePath);
            }

            equipoService.eliminarPorId(id);
            attributes.addFlashAttribute("msg", "Equipo eliminado correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al eliminar el equipo: " + e.getMessage());
        }
        return "redirect:/equipo";
    }

    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}