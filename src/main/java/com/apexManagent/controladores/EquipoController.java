package com.apexManagent.controladores;

import com.apexManagent.modelos.Equipo;
import com.apexManagent.servicios.interfaces.IAsignacionEquipoService;
import com.apexManagent.servicios.interfaces.IEquipoService;
import com.apexManagent.servicios.interfaces.IUbicacionService;
import com.apexManagent.servicios.utilerias.PdfGeneraterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private IAsignacionEquipoService asignacionService;
    @Autowired
    private IEquipoService equipoService;
    @Autowired
    private IUbicacionService ubicacionService;
    @Autowired
    private PdfGeneraterService pdfGeneraterService;

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

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute Equipo equipo,
            BindingResult result,
            @RequestParam("fileImagen") MultipartFile fileImagen,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.equipo", result);
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        if (equipoService.existePorNserie(equipo.getNserie())) {
            attributes.addFlashAttribute("error", "Ya existe un equipo con este número de serie.");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        if (fileImagen.isEmpty()) {
            attributes.addFlashAttribute("error", "Debe seleccionar una imagen");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        // Validación de descripción (máximo 255 caracteres)
        if (equipo.getDescripcion() != null && equipo.getDescripcion().length() > 255) {
            attributes.addFlashAttribute("errorDescripcion", "La descripción no puede exceder los 255 caracteres");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        // Validación de tamaño ANTES de cualquier operación
        if (fileImagen.getSize() > 2 * 1024 * 1024) {
            attributes.addFlashAttribute("error", "La imagen no debe superar los 2MB");
            attributes.addFlashAttribute("equipo", equipo);
            return "redirect:/equipo/create";
        }

        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Guardar imagen con nombre original
            String fileName = fileImagen.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            equipo.setImg(fileName);
            equipo.setFechaRegistro(LocalDateTime.now());
            equipoService.guardar(equipo);

            attributes.addAttribute("equipoId", equipo.getId());
            attributes.addFlashAttribute("msg", "Equipo registrado correctamente");
            return "redirect:/preventivos/calendario";

        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/equipo/create";
        }
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
            RedirectAttributes attributes) {

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

        if (fileImagen != null && !fileImagen.isEmpty() && fileImagen.getSize() > 2 * 1024 * 1024) {
            attributes.addFlashAttribute("error", "La imagen no debe superar los 2MB");
            return "redirect:/equipo/edit/" + id;
        }

        if (equipo.getDescripcion() != null && equipo.getDescripcion().length() > 255) {
            attributes.addFlashAttribute("errorDescripcion", "La descripción no puede exceder los 255 caracteres");
            return "redirect:/equipo/edit/" + id;
        }

        try {
            // Si se sube nueva imagen
            if (fileImagen != null && !fileImagen.isEmpty()) {
                // Eliminar imagen anterior si existe
                if (equipoExistente.getImg() != null) {
                    Path oldFilePath = Paths.get(UPLOAD_DIR + equipoExistente.getImg());
                    Files.deleteIfExists(oldFilePath);
                }

                // Guardar nueva imagen con nombre original
                String fileName = fileImagen.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                equipo.setImg(fileName);
            } else {
                // Mantener la imagen existente
                equipo.setImg(equipoExistente.getImg());
            }

            // Mantener la fecha de registro original
            equipo.setFechaRegistro(equipoExistente.getFechaRegistro());
            equipoService.guardar(equipo);

            attributes.addFlashAttribute("success", "Información actualizada correctamente.");
            return "redirect:/equipo"; // Redirección a la vista principal de equipos
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/equipo/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            Equipo equipo = equipoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

            // Validar si el equipo está asignado a algún personal
            if (asignacionService.existeAsignacionPorEquipoId(id)) {
                attributes.addFlashAttribute("error",
                        "No se puede eliminar el equipo porque está asignado a un personal.");
                return "redirect:/equipo";
            }

            // Eliminar imagen asociada si existe
            if (equipo.getImg() != null) {
                Path filePath = Paths.get(UPLOAD_DIR + equipo.getImg());
                Files.deleteIfExists(filePath);
            }

            equipoService.eliminarPorId(id);
            attributes.addFlashAttribute("success", "Equipo eliminado correctamente");

        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("error", "Equipo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al eliminar la imagen del equipo: " + e.getMessage());
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al eliminar el equipo: " + e.getMessage());
        }
        return "redirect:/equipo";
    }

    @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> ReporteGeneral(@PathVariable("visualizacion") String visualizacion) {

        try {
            List<Equipo> equipos = equipoService.findAll();

            // Genera el PDF. Si hay un error aquí, la excepción será capturada.
            byte[] pdfBytes = pdfGeneraterService.generatePdfFromHtml("reportes/rpEquipo", "equipo", equipos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // inline= vista previa, attachment=descarga el archivo
            headers.add("Content-Disposition", visualizacion + "; filename=reporte_general.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}