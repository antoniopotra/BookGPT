package software.design.gamegpt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IgdbAuthResponse(@JsonProperty("access_token") String accessToken) {
}
