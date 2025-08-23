package art.mehdiragani.mehdiragani.public_.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        Object message = request.getAttribute("jakarta.servlet.error.message");

        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("errorCode", "404");
                    model.addAttribute("errorTitle", "Page Not Found");
                    model.addAttribute("errorMessage", "The page you're looking for doesn't exist.");
                    break;
                case 500:
                    model.addAttribute("errorCode", "500");
                    model.addAttribute("errorTitle", "Internal Server Error");
                    model.addAttribute("errorMessage", "Something went wrong on our end. Please try again later.");
                    break;
                case 403:
                    model.addAttribute("errorCode", "403");
                    model.addAttribute("errorTitle", "Access Denied");
                    model.addAttribute("errorMessage", "You don't have permission to access this resource.");
                    break;
                default:
                    model.addAttribute("errorCode", statusCode);
                    model.addAttribute("errorTitle", "Error");
                    model.addAttribute("errorMessage", message != null ? message.toString() : "An unexpected error occurred.");
            }
        } else {
            model.addAttribute("errorCode", "Error");
            model.addAttribute("errorTitle", "Error");
            model.addAttribute("errorMessage", "An unexpected error occurred.");
        }
        
        return "error";
    }
} 