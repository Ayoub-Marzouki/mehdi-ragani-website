package art.mehdiragani.mehdiragani.admin.controllers;

import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.services.PrintService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/prints")
public class AdminPrintController {
    private final PrintService printService;

    public AdminPrintController(PrintService printService) {
        this.printService = printService;
    }

    @GetMapping
    public String listPrints(Model model) {
        List<Print> prints = printService.getAllPrints();
        model.addAttribute("prints", prints);
        return "admin/prints";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("print", new Print());
        model.addAttribute("printTypes", PrintType.values());
        model.addAttribute("printSizes", PrintSize.values());
        model.addAttribute("framings", Framing.values());
        model.addAttribute("feels", ArtworkFeel.values());
        model.addAttribute("themes", ArtworkTheme.values());
        return "admin/add-print";
    }

    @PostMapping("/add")
    public String handleAdd(@Valid @ModelAttribute("print") Print print, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("printTypes", PrintType.values());
            model.addAttribute("printSizes", PrintSize.values());
            model.addAttribute("framings", Framing.values());
            model.addAttribute("feels", ArtworkFeel.values());
            model.addAttribute("themes", ArtworkTheme.values());
            return "admin/add-print";
        }
        printService.savePrint(print);
        return "redirect:/admin/prints";
    }

    @PostMapping("/{id}/delete")
    public String deletePrint(@PathVariable("id") UUID id) {
        printService.deletePrintById(id);
        return "redirect:/admin/prints";
    }

    @GetMapping("/{id}/change")
    public String showEditForm(@PathVariable("id") UUID id, Model model) {
        Print print = printService.getPrintById(id);
        model.addAttribute("print", print);
        model.addAttribute("printTypes", PrintType.values());
        model.addAttribute("printSizes", PrintSize.values());
        model.addAttribute("framings", Framing.values());
        model.addAttribute("feels", ArtworkFeel.values());
        model.addAttribute("themes", ArtworkTheme.values());
        return "admin/edit-print";
    }

    @PostMapping("/{id}/change")
    public String updatePrint(@PathVariable("id") UUID id, @Valid @ModelAttribute("print") Print print, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("printTypes", PrintType.values());
            model.addAttribute("printSizes", PrintSize.values());
            model.addAttribute("framings", Framing.values());
            model.addAttribute("feels", ArtworkFeel.values());
            model.addAttribute("themes", ArtworkTheme.values());
            return "admin/edit-print";
        }
        print.setId(id);
        printService.savePrint(print);
        return "redirect:/admin/prints";
    }
} 