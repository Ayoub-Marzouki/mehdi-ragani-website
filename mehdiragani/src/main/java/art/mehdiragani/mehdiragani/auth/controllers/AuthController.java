package art.mehdiragani.mehdiragani.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import art.mehdiragani.mehdiragani.auth.dto.RegisterForm;
import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.models.enums.UserRole;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegisterForm registerForm = new RegisterForm();
        model.addAttribute("registerForm", registerForm);
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute RegisterForm registerForm, BindingResult binding) {
        if (binding.hasErrors()) return "auth/register";

        User user = new User();
        user.setUsername(registerForm.getUsername());
        user.setEmail(registerForm.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerForm.getPassword()));
        user.setFirstName(registerForm.getFirstName());
        user.setLastName(registerForm.getLastName());
        user.setPhoneNumber(registerForm.getPhoneNumber());
        user.setRole(UserRole.Customer);
        
        userService.createUser(user);

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
