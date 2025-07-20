package art.mehdiragani.mehdiragani.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "paypal")
@PropertySource("classpath:paypal.properties")
public class PayPalConfig {
    private String id;
    private String secret;
    private String apiBaseUrl;

    public void printApi() {
        System.out.println("PayPal API Base URL: " + apiBaseUrl);
    }
}
