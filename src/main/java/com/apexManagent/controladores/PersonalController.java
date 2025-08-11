package com.apexManagent.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;
// import java.util.stream.IntStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.apexManagent.modelos.Personal;
import com.apexManagent.modelos.Rol;
import com.apexManagent.repositorio.IPersonalRepository;
//import com.apexManagent.repositorio.IRolRepository;
import com.apexManagent.servicios.interfaces.IPersonalService;
import com.apexManagent.servicios.interfaces.IRolService;

import jakarta.validation.Valid;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/personales")
public class PersonalController {

    @Autowired
    private IPersonalService personalService;

    @Autowired
    private IRolService rolService;

    @Autowired
    private IPersonalRepository personalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String search,
            @RequestParam("nombre") Optional<String> nombre) {

        int currentPage = page.orElse(1) - 1; // Ajuste para que empiece en 0
        int pageSize = size.orElse(5);

        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nombreSearch = nombre.orElse("");
        Page<Personal> personales = personalService.findByNombreContaining(nombreSearch, pageable);

        model.addAttribute("personales", personales);
        model.addAttribute("roles", rolService.obtenerTodos());

        int totalPages = personales.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "personal/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("personal")) {
            Personal p = new Personal();
            p.setRol(new Rol());
            model.addAttribute("personal", p);

        }
        model.addAttribute("roles", rolService.obtenerTodos());
        return "personal/create";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("personal") Personal personal,
            BindingResult result,
            @RequestParam("imagen") MultipartFile imagen,
            Model model, RedirectAttributes attributes) throws IOException {

        // Encriptar la contraseña antes de guardar
        String password = passwordEncoder.encode(personal.getPassword());
        personal.setPassword(password);

        // Si hay errores de validación en el modelo
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.obtenerTodos());
            return "personal/create"; // vuelve a la vista sin perder datos
        }

        // Verificar si ya existe un personal con los mismos datos únicos
        if (personalRepository.existsByNombreOrTelefonoOrEmailOrUsername(
                personal.getNombre(),
                personal.getTelefono(),
                personal.getEmail(),
                personal.getUsername())) {

            model.addAttribute("error", "Ya existe un registro con esos datos");
            model.addAttribute("roles", rolService.obtenerTodos());
            return "personal/create";

        }

        // Procesar imagen si fue subida
        if (imagen.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar una imagen");
            model.addAttribute("roles", rolService.obtenerTodos());
            return "personal/create";
        }

        // Validar tamaño máximo (2MB)
        if (imagen.getSize() > 2097152) {
            model.addAttribute("error", "La imagen no debe exceder 2MB");
            model.addAttribute("roles", rolService.obtenerTodos());
            return "personal/create";
        }

        // Validar tipo de imagen
        String contentType = imagen.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            model.addAttribute("error", "Solo se permiten imágenes JPEG o PNG");
            model.addAttribute("roles", rolService.obtenerTodos());
            return "personal/create";
        }

        // Asignar imagen al personal
        personal.setImgPersonal(imagen.getBytes());

        // Guardar personal
        personalService.guardar(personal);

        // Mensaje de éxito
        attributes.addFlashAttribute("msg", "Personal registrado correctamente");
        return "redirect:/personales";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        Personal p = personalService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado"));

        model.addAttribute("personal", p);
        model.addAttribute("roles", rolService.obtenerTodos());

        return "personal/edit";

    }

    @PostMapping("/update")
    public String update(
            @RequestParam("id") Integer id,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("personal") Personal personal,
            BindingResult result,
            RedirectAttributes attributes, Model model) throws IOException {

        // Obtener el personal existente de la BD
        Personal personalExistente = personalService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));

        // Actualizar solo los campos editables
        personalExistente.setNombre(personal.getNombre());
        personalExistente.setApellido(personal.getApellido());
        personalExistente.setEmail(personal.getEmail());
        personalExistente.setTelefono(personal.getTelefono());
        personalExistente.setRol(personal.getRol());
        personalExistente.setStatus(personal.getStatus());
        personalExistente.setUsername(personal.getUsername());

        // Si el campo contraseña en el formulario no está vacío, actualizar
        if (personal.getPassword() != null && !personal.getPassword().isEmpty()) {
            // Aquí puedes aplicar el hashing de la contraseña antes de guardarla
            String passwordEncriptada = passwordEncoder.encode(personal.getPassword());
            personalExistente.setPassword(passwordEncriptada);
        }

        // Imagen
        if (imagen != null && !imagen.isEmpty()) {
            // Validar tamaño, tipo, etc. si lo deseas
            personalExistente.setImgPersonal(imagen.getBytes());
        }
        // Si no se sube imagen nueva, mantenemos la anterior

        // Guardar cambios
        personalService.guardar(personalExistente);

        // Mensaje SweetAlert
        attributes.addFlashAttribute("msg", "Personal registrado correctamente");

        return "redirect:/personales";
    }

    @GetMapping("/details/{id}")
    public String mostrarPersonal(@PathVariable("id") Integer id, Model model) {
        Personal personal = personalService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
        model.addAttribute("personal", personal);
        return "personal/details";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            personalService.eliminarPorId(id);
            attributes.addFlashAttribute("swal", Map.of(
                    "title", "¡Eliminado!",
                    "text", "El equipo ha sido eliminado correctamente",
                    "icon", "success"));
        } catch (Exception e) {
            attributes.addFlashAttribute("swal", Map.of(
                    "title", "Error",
                    "text", "No se pudo eliminar el equipo: " + e.getMessage(),
                    "icon", "error"));
        }
        return "redirect:/personales";
    }

}
