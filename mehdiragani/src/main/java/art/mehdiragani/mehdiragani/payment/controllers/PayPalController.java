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
    public Map<String, String> createOrder(HttpSession session, Authentication auth) {
        // 1. Load or create the cart
        Cart cart = cartService.getOrCreateCart(session, auth);

        // 2. Create a pending Order in DB
        Order order = orderService.createPendingOrder(cart);

        // 3. Hit PayPal to create an order for that amount
        String paypalOrderId = paypalService.createOrder(order.getTotal(), order.getCurrency());

        // 4. Store PayPal orderId in your Order.paymentReference
        orderService.updatePaymentStatus(order.getId(), PaymentStatus.Pending, paypalOrderId);
        
        return Map.of(
          "paypalOrderId", paypalOrderId,
          "ourOrderId",        order.getId().toString()
        );

    }

    // Much cleaner and safer!
    @PostMapping("/capture-order")
    public ResponseEntity<?> captureOrder(@RequestParam String paypalOrderId, @RequestParam UUID ourOrderId, HttpSession session, Authentication auth) {
        // 1. Capture on PayPal
        PayPalCaptureResponse captureResponse = paypalService.captureOrder(paypalOrderId);

        // 2. Extract the capture ID using our helper method
        String captureId = captureResponse.findCompletedCaptureId();
        
        // 3. Check for successful capture
        if (captureId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to find completed capture ID.");
        }

        // 4. Finalize our Order and clear the cart
        orderService.completeOrder(ourOrderId, captureId, session, auth);

        return ResponseEntity.ok(Map.of(
            "status",    "COMPLETED",
            "captureId", captureId
        ));
}
}