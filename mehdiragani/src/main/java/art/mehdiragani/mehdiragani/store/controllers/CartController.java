package art.mehdiragani.mehdiragani.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {

    public CartController() {

    }

    @GetMapping
    public String cart() {
        return "store/cart";
    }
}
