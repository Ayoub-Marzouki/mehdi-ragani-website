package art.mehdiragani.mehdiragani.payment.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import art.mehdiragani.mehdiragani.payment.config.PayPalConfig;
import art.mehdiragani.mehdiragani.payment.dto.PayPalAccessTokenResponse;
import art.mehdiragani.mehdiragani.payment.dto.PayPalCaptureResponse;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PayPalService {
    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);
    private final PayPalConfig payPalConfig;
    private final RestTemplate restTemplate;
    // Cache for access token to avoid fetching it multiple times
    private String accessToken;
    private Instant tokenExpiration;
    

    public PayPalService(PayPalConfig payPalConfig) {
        this.payPalConfig = payPalConfig;
        this.restTemplate = new RestTemplate();
        this.tokenExpiration = Instant.MIN;
    }

    public synchronized String getAccessToken() {
        try {
            if (accessToken == null || Instant.now().isAfter(tokenExpiration.minus(1, ChronoUnit.MINUTES))) {
                logger.debug("Fetching new PayPal access token...");
                String url = payPalConfig.getApiBaseUrl() + "/v1/oauth2/token";
                String credentials = payPalConfig.getId() + ":" + payPalConfig.getSecret();
                String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("Authorization", "Basic " + encodedCredentials);
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "client_credentials");
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
                ResponseEntity<PayPalAccessTokenResponse> response = restTemplate.postForEntity(url, request, PayPalAccessTokenResponse.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    PayPalAccessTokenResponse tokenResponse = response.getBody();
                    this.accessToken = tokenResponse.getAccessToken();
                    this.tokenExpiration = Instant.now().plusSeconds(tokenResponse.getExpiresIn());
                } else {
                    logger.error("Failed to get PayPal access token: {} - {}", response.getStatusCode(), response.getBody());
                    throw new RuntimeException("Failed to get PayPal access token: " + response.getStatusCode() + " - " + response.getBody());
                }
            }
            return accessToken;
        } catch (Exception e) {
            logger.error("Exception while getting PayPal access token", e);
            throw new RuntimeException("Exception while getting PayPal access token: " + e.getMessage(), e);
        }
    }

    public String createOrder(BigDecimal amount, String currency) {
        try {
            String url = payPalConfig.getApiBaseUrl() + "/v2/checkout/orders";
            String formattedAmount = String.format("%.2f", amount);
            Map<String,Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                    "amount", Map.of(
                        "currency_code", currency,
                        "value", formattedAmount
                    )
                ))
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> request = new HttpEntity<>(orderRequest, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, request, Map.class);
            logger.debug("Creating PayPal order with amount: {} {}", formattedAmount, currency);
            if (resp.getStatusCode() == HttpStatus.CREATED) {
                return ((Map)resp.getBody()).get("id").toString();
            }
            logger.error("Failed to create PayPal order: {} - {}", resp.getStatusCode(), resp.getBody());
            throw new RuntimeException("Failed to create PayPal order: " + resp.getStatusCode() + " - " + resp.getBody());
        } catch (Exception e) {
            logger.error("Exception while creating PayPal order", e);
            throw new RuntimeException("Exception while creating PayPal order: " + e.getMessage(), e);
        }
    }

    public PayPalCaptureResponse captureOrder(String orderId) {
        try {
            String url = payPalConfig.getApiBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<PayPalCaptureResponse> resp = restTemplate.postForEntity(url, request, PayPalCaptureResponse.class);
            if (resp.getStatusCode() == HttpStatus.CREATED) {
                return resp.getBody();
            }
            logger.error("Failed to capture PayPal order: {} - {}", resp.getStatusCode(), resp.getBody());
            throw new RuntimeException("Failed to capture PayPal order: " + resp.getStatusCode() + " - " + resp.getBody());
        } catch (Exception e) {
            logger.error("Exception while capturing PayPal order", e);
            throw new RuntimeException("Exception while capturing PayPal order: " + e.getMessage(), e);
        }
    }
}
