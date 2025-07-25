package art.mehdiragani.mehdiragani.payment.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import art.mehdiragani.mehdiragani.payment.dto.PayPalCaptureResponse;
import art.mehdiragani.mehdiragani.payment.enums.PaymentStatus;
import art.mehdiragani.mehdiragani.payment.models.Order;
import art.mehdiragani.mehdiragani.payment.services.OrderService;
import art.mehdiragani.mehdiragani.payment.services.PayPalService;
import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {
    private final PayPalService paypalService;
    private final CartService cartService;
    private final OrderService orderService;

    public PayPalController(PayPalService paypalService, CartService cartService, OrderService orderService) {
        this.paypalService = paypalService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    /**
     * 1) Snapshot current cart → pending Order
     * 2) Call PayPal to create an order
     * 3) Persist PayPal’s orderId in paymentReference
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(HttpSession session, Authentication auth) {
        try {
            Cart cart = cartService.getOrCreateCart(session, auth);
            if (cart.getTotalPrice() == null || cart.getTotalPrice().doubleValue() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Cart total must be greater than zero."));
            }
            Order order = orderService.createPendingOrder(cart);
            String paypalOrderId = paypalService.createOrder(order.getTotal(), order.getCurrency());
            orderService.updatePaymentStatus(order.getId(), PaymentStatus.PENDING, paypalOrderId);
            return ResponseEntity.ok(Map.of(
                "paypalOrderId", paypalOrderId,
                "ourOrderId", order.getId().toString()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create PayPal order: " + e.getMessage()));
        }
    }

    @PostMapping("/capture-order")
    public ResponseEntity<?> captureOrder(@RequestParam String paypalOrderId, @RequestParam UUID ourOrderId, HttpSession session, Authentication auth) {
        try {
            PayPalCaptureResponse captureResponse = paypalService.captureOrder(paypalOrderId);
            String captureId = captureResponse.findCompletedCaptureId();
            if (captureId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to find completed capture ID."));
            }
            orderService.completeOrder(ourOrderId, captureId, session, auth);
            return ResponseEntity.ok(Map.of(
                "status", "COMPLETED",
                "captureId", captureId
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to capture PayPal order: " + e.getMessage()));
        }
    }
}