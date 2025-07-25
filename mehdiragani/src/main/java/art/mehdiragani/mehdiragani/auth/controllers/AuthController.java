package art.mehdiragani.mehdiragani.auth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import art.mehdiragani.mehdiragani.auth.dto.RegisterForm;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegisterForm registerForm = new RegisterForm();
        model.addAttribute("registerForm", registerForm);
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute RegisterForm registerForm, BindingResult binding, Model model) {
        if (binding.hasErrors()) return "auth/register";
        try {
            userService.registerUser(registerForm);
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerForm", registerForm);
            model.addAttribute("registrationError", e.getMessage());
            return "auth/register";
        }
        return "redirect:/user/login?registered";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            Model model
        ) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        if (registered != null) {
            model.addAttribute("registered", true);
        }
        return "auth/login"; 
    }
    
}
