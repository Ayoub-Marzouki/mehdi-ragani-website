package art.mehdiragani.mehdiragani.payment.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String paymentCompleted(@RequestParam("orderId") UUID orderId, Model model) {
        Optional<Order> orderOpt = orderService.getOrder(orderId);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            model.addAttribute("order", order);
            model.addAttribute("orderId", orderId);
            return "payment/payment-completed";
        } else {
            // Order not found - redirect to error page or show error
            return "redirect:/payment/payment-failed";
        }
    }
    
    @GetMapping("/payment-failed")
    public String paymentFailed(Model model) {
        // You can add any error messages or data here
        model.addAttribute("errorMessage", "Payment processing failed. Please try again.");
        return "payment/payment-failed";
    }
} 