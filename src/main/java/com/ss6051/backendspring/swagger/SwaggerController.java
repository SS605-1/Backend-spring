package com.ss6051.backendspring.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    // Dev only: Redirect to Swagger UI
    @GetMapping(path = {"/", "", "/swagger"})
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
