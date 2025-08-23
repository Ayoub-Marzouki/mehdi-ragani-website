package art.mehdiragani.mehdiragani.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {
    private String id;
    private String secret;
    private String apiBaseUrl;

}
