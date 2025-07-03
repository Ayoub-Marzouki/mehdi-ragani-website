package art.mehdiragani.mehdiragani.admin.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.models.enums.UserRole;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", List.of(UserRole.values()));

        return "admin/add-user";
    }
    
    @PostMapping("/add")
    public String handleAdd(@Valid @ModelAttribute("user") User user, BindingResult binding) {
        if (binding.hasErrors()) return "admin/add-user";

        userService.createUser(user);

        return "redirect:/admin/users";
    }


    @GetMapping("/{id}/change")
    public String showEditForm(@PathVariable("id") UUID id, Model model) {
        User user = userService.getUserById(id);
        
        model.addAttribute("user", user);
        model.addAttribute("allRoles", List.of(UserRole.values()));

        return "admin/edit-user";
    }

    @PostMapping("/{id}/change")
    public String updateUser(@PathVariable("id") UUID id, 
        @Valid @ModelAttribute("user") User user, BindingResult binding) {
        if (binding.hasErrors()) return "admin/users";
        
        user.setId(id);
        userService.updateUser(user);

        return "redirect:/admin/users";
    }
    

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") UUID id) {
        User user = userService.getUserById(id);
        userService.deleteUser(user);

        return "redirect:/admin/users";
    }
}
