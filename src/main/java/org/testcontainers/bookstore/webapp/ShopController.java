package org.testcontainers.bookstore.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
