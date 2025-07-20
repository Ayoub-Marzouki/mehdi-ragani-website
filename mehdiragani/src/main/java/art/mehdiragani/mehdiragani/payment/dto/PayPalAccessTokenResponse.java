package art.mehdiragani.mehdiragani.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PayPalAccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("nonce")
    private String nonce;
}
