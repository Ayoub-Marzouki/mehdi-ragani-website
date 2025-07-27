package art.mehdiragani.mehdiragani.payment.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import art.mehdiragani.mehdiragani.payment.models.Order;
import art.mehdiragani.mehdiragani.payment.services.OrderService;

@Controller
@RequestMapping("/payment")
public class PaymentResultController {
    
    private final OrderService orderService;
    
    public PaymentResultController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping("/payment-completed")
    public String paymentCompleted(@RequestParam("orderId") UUID orderId, Model model, Authentication auth) {
        Optional<Order> orderOpt = orderService.getOrder(orderId);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            model.addAttribute("order", order);
            model.addAttribute("orderId", orderId);
            
            // Check if user is authenticated or guest
            boolean isAuthenticated = auth != null && auth.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            return "payment/payment-completed";
        } else {
            // Order not found - redirect to error page or show error
            return "redirect:/payment/payment-failed";
        }
    }
    
    /**
     * POST endpoint to store a payment error message as a flash attribute and redirect to the payment-failed page.
     * This is best practice for passing error messages after a redirect in Spring MVC, as it avoids exposing errors in the URL
     * and ensures the message is only visible for the next request.
     */
    @PostMapping("/store-failure")
    public String storePaymentFailure(@RequestParam("errorMsg") String errorMsg, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
        return "redirect:/payment/payment-failed";
    }

    /**
     * GET endpoint for the payment failed page. Displays the error message from a flash attribute if present,
     * or a default message otherwise. This keeps error messages out of the URL and provides a user-friendly experience.
     */
    @GetMapping("/payment-failed")
    public String paymentFailed(Model model) {
        if (!model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", "Payment processing failed. Please try again.");
        }
        return "payment/payment-failed";
    }
} 