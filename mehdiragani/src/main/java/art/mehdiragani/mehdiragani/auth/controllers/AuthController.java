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
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(@RequestParam(value = "redirectTo", required = false) String redirectTo, Model model, HttpSession session) {
        // Store the current session ID before authentication for cart/order merging
        session.setAttribute("PRE_AUTH_SESSION_ID", session.getId());
        
        RegisterForm registerForm = new RegisterForm();
        model.addAttribute("registerForm", registerForm);
        if (redirectTo != null) {
            model.addAttribute("redirectTo", redirectTo);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerForm") RegisterForm registerForm, 
                                @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                BindingResult binding, Model model, HttpSession session) {
        
        if (userService.getUserByUsername(registerForm.getUsername()).isPresent()) {
            binding.rejectValue("username", "error.username", "Username already exists. Please choose another.");
        }
        if (binding.hasErrors()) {
            // Re-add the registerForm to the model so the form can be re-displayed with errors
            model.addAttribute("registerForm", registerForm);
            if (redirectTo != null) {
                model.addAttribute("redirectTo", redirectTo);
            }
            return "auth/register";
        }
        userService.registerUser(registerForm);
        
        // Store redirectTo in session for the login redirect
        if (redirectTo != null) {
            session.setAttribute("POST_REGISTRATION_REDIRECT", redirectTo);
        }
        
        // Redirect to login with registered flag
        return "redirect:/user/login?registered";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            @RequestParam(value = "redirectTo", required = false) String redirectTo,
            Model model, HttpSession session
        ) {
        // Store the current session ID before authentication for cart/order merging
        session.setAttribute("PRE_AUTH_SESSION_ID", session.getId());
        
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        if (registered != null) {
            model.addAttribute("registered", true);
        }
        if (redirectTo != null) {
            model.addAttribute("redirectTo", redirectTo);
        }
        return "auth/login"; 
    }
    
}
