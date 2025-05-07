package art.mehdiragani.mehdiragani.public_.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {
    
    @GetMapping
    public String about() {
        return "pages/about";
    }
}
