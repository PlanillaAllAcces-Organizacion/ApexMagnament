package com.apexManagent.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/Login")
    public String login() {
        return "/Login";
    }
    
}
