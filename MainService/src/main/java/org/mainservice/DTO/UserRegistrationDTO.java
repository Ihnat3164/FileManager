package org.mainservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO for user registration")
public class UserRegistrationDTO {

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "User's password", example = "securePassword123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}
