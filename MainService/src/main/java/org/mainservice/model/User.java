package org.mainservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "USERS")
@Schema(description = "Represents a user in the system.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the user.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "User's full name.", example = "John Doe")
    private String name;

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address.", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's password.", example = "securePassword123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Pattern(regexp = "ADMIN|USER", message = "Role must be either ADMIN or USER")
    @Schema(description = "User's role in the system.", example = "ADMIN")
    private String role;
}
