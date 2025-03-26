package by.modsen.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakAdminTokenResponse(

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("expires_in")
    Integer expiresIn,

    @JsonProperty("refresh_expires_in")
    Integer refreshExpiresIn,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("not_before_policy")
    Integer notBeforePolicy,

    String scope

) {
}
