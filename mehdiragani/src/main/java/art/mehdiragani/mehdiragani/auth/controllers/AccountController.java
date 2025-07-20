package art.mehdiragani.mehdiragani.auth.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import art.mehdiragani.mehdiragani.payment.models.Order;
import art.mehdiragani.mehdiragani.payment.services.OrderService;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final UserService userService;
    private final OrderService orderService;
    
    public AccountController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }
    
    /**
     * Main account page - user profile and overview
     */
    @GetMapping
    public String accountOverview(Model model, Authentication auth) {
        User user = userService.currentUser(auth);
        if (user == null) {
            return "redirect:/user/login";
        }
        
        // Get recent orders for overview
        List<Order> recentOrders = orderService.listUserOrders(user);
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5); // Show only 5 most recent
        }
        
        model.addAttribute("user", user);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("totalOrders", orderService.listUserOrders(user).size());
        
        return "auth/account";
    }
    
    /**
     * List all orders for the authenticated user
     */
    @GetMapping("/orders")
    public String listOrders(Model model, Authentication auth) {
        User user = userService.currentUser(auth);
        if (user == null) {
            return "redirect:/user/login";
        }
        
        List<Order> orders = orderService.listUserOrders(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        
        return "auth/orders";
    }
    
    /**
     * Detail view of a single order
     */
    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable UUID orderId, Model model, Authentication auth) {
        User user = userService.currentUser(auth);
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Optional<Order> orderOpt = orderService.getOrder(orderId);
        if (orderOpt.isEmpty()) {
            return "redirect:/account/orders";
        }
        
        Order order = orderOpt.get();
        
        // Security check: ensure user can only view their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/account/orders";
        }
        
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        
        return "auth/order-details";
    }
} 