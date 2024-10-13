package org.mainservice.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO representing user's login credentials.")
public class JwtRequest {
    @Schema(description = "The email address of the user.", example = "user@example.com")
    private String email;

    @Schema(description = "The password of the user.", example = "password123")
    private String password;
}
