package com.apexManagent.controladores;

import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;
// import java.util.stream.IntStream;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;


@Controller
@RequestMapping("/personales")
public class PersonalController {

    @GetMapping
    public String index() {
        return "personal/index";
    }
    

}
