package art.mehdiragani.mehdiragani.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.core.services.DatabaseService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private final DatabaseService databaseService;

    @Autowired
    public AdminDashboardController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("tables", databaseService.getTables());
        return "admin/dashboard";
    }
}
