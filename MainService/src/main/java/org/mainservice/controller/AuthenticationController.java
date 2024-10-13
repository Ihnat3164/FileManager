package org.mainservice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.JwtRequest;
import org.mainservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "Controller to receive jwt after entering credentials.")
public class AuthenticationController {

    private final UserService userService;

    @Operation(summary = "Authenticate user", description = "Authenticates the user and provides a JWT token if the credentials are correct.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully, JWT token generated"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@Parameter(description = "User login details including email and password", required = true)
                                                  @RequestBody JwtRequest authRequest) {
        return userService.createAuthToken(authRequest);
    }
}
