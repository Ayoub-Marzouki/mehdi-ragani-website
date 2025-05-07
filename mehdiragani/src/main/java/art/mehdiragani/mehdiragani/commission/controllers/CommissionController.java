package art.mehdiragani.mehdiragani.commission.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/services")
public class CommissionController {
    
    @GetMapping
    public String services() {
        return "pages/services";
    }
}
