package art.mehdiragani.mehdiragani.admin.controllers;

import art.mehdiragani.mehdiragani.payment.models.Order;
import art.mehdiragani.mehdiragani.payment.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable("id") UUID id, Model model) {
        Order order = orderService.getOrderWithItems(id).orElse(null);
        model.addAttribute("order", order);
        return "admin/order-details";
    }
} 